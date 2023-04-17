package gcp.global.jotdiary.model.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import gcp.global.jotdiary.model.models.Diaries
import gcp.global.jotdiary.model.models.Entries
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

const val DIARIES_COLLECTION_REF = "Diaries"
const val ENTRIES_COLLECTION_REF = "Entries"
const val USERS_COLLECTION_REF = "Users"

/**
 * StorageRepository
 *
 * This class is used to get data from the StorageRepository
 * and display it on the screen. The State, aswell as the data is controlled from here.
 **/
class StorageRepository() {

    var storage = FirebaseStorage.getInstance()

    fun user() = Firebase.auth.currentUser

    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    fun getDiariesRef(): CollectionReference {

        val diariesRef = Firebase.firestore.collection(USERS_COLLECTION_REF).document(getUserId()).collection(DIARIES_COLLECTION_REF)



        println("DiariesRef current user: ${getUserId()}")
        println("Path is ${diariesRef.path}")

        return diariesRef
    }

    fun getEntriesRef(diaryId: String): CollectionReference {

        val entriesRef = Firebase.firestore.collection(USERS_COLLECTION_REF).document(getUserId()).collection(DIARIES_COLLECTION_REF).document(diaryId).collection(ENTRIES_COLLECTION_REF)

        return entriesRef
    }

    fun getUserEntries(
        diaryId: String
    ): Flow<Resources<List<Entries>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getEntriesRef(diaryId)
                .orderBy("entryId")
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val entries = snapshot.toObjects(Entries::class.java)
                        Resources.Success(data = entries)
                    } else {
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }
        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getEntry(
        diaryId: String,
        entryId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Entries?) -> Unit
    ) {
        getEntriesRef(diaryId)
            .document(entryId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Entries::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }

    }

    fun addEntry(
        diaryId: String,
        name: String,
        description: String,
        mood: Int,
        audioUri: Uri?,
        imageUri: Uri?,
        date: Timestamp,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getEntriesRef(diaryId).document().id
        var addAudioFile = audioUri
        val addEntryAudioRef = storage.reference.child("Users/${getUserId()}/Audios/$documentId")

        var addImageFile = imageUri
        val addEntryImageRef = storage.reference.child("Users/${getUserId()}/Images/$documentId")

        var entry = Entries(
            entryId = documentId,
            entryName = name,
            entryDescription = description,
            entryMood = mood,
            entryDate = date
        )

        /**
         * This Global Coroutine Scope will run in the background of the app, which
         * will allow the user to continue using the app while the audio and image
         * files are being uploaded to Firebase Storage. The audio and image urls
         * are then Downloaded and set to the entry object. GlobalScope.launch is
         * kinda risky because it assumes you don't care about concurrency, but here
         * we don't care if audio and image upload at the same time, we just want
         * the entry to include both urls. when they are done uploading, so using
         * it here is fine.
         *
         */
        GlobalScope.launch {

            /**
             * This async block will run in the background of the app, which will
             * return the audio url once the audio file has been uploaded to Firebase.
             * If the audio file is null, then the audio url will be an empty string.
             */
            val audioDeferred = async {
                if (addAudioFile != null) {
                    addEntryAudioRef.putFile(addAudioFile!!).await()
                    addEntryAudioRef.downloadUrl.await().toString()
                } else {
                    ""
                }
            }

            /**
             * This async block will run in the background of the app, which will
             * return the image url once the image file has been uploaded to Firebase.
             * If the image file is null, then the image url will be an empty string.
             */
            val imageDeferred = async {
                if (addImageFile != null) {
                    addEntryImageRef.putFile(addImageFile!!).await()
                    addEntryImageRef.downloadUrl.await().toString()
                } else {
                    ""
                }
            }

            /**
             * This will wait for both the audio and image urls to be returned before
             * the variables can be used anywhere else in the code. Await() waits for
             * the tasks to finish, and then returns the result to the variable.
             */
            val audioUrl = audioDeferred.await()
            val imageUrl = imageDeferred.await()

            /**
             * This will create add ONTO the existing entry with new audio and image
             * urls. NOTE*** audioUrl and imageUrl are tasks, so this new entry is
             * only created once the tasks are finished.
             */
            val addAudioAndImageEntry = entry.copy(
                entryAudioUrl = audioUrl,
                entryImageUrl = imageUrl
            )

            /**
             * This will set the new entry to the database. This is the last step in
             * the process, and will only be done once the audio and image urls have
             * been returned. The addAudioAndImageEntry variable is the entry object
             * and it is made up of task variables, that will only be returned once
             * the tasks are finished.
             */
            getEntriesRef(diaryId)
                .document(documentId)
                .set(addAudioAndImageEntry)
                .addOnCompleteListener {
                    onComplete.invoke(it.isSuccessful)
                }
        }
    }

    fun addEntryUrl(
        diaryId: String,
        name: String,
        description: String,
        mood: Int,
        audioUri: Uri?,
        imageUrl: String,
        date: Timestamp,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getEntriesRef(diaryId).document().id
        var addAudioFile = audioUri
        val addEntryAudioRef = storage.reference.child("Users/${getUserId()}/Audios/$documentId")

        var entry = Entries(
            entryId = documentId,
            entryName = name,
            entryDescription = description,
            entryMood = mood,
            entryDate = date
        )

        /**
         * This Global Coroutine Scope will run in the background of the app, which
         * will allow the user to continue using the app while the audio and image
         * files are being uploaded to Firebase Storage. The audio and image urls
         * are then Downloaded and set to the entry object. GlobalScope.launch is
         * kinda risky because it assumes you don't care about concurrency, but here
         * we don't care if audio and image upload at the same time, we just want
         * the entry to include both urls. when they are done uploading, so using
         * it here is fine.
         *
         */
        GlobalScope.launch {

            /**
             * This async block will run in the background of the app, which will
             * return the audio url once the audio file has been uploaded to Firebase.
             * If the audio file is null, then the audio url will be an empty string.
             */
            val audioDeferred = async {
                if (addAudioFile != null) {
                    addEntryAudioRef.putFile(addAudioFile!!).await()
                    addEntryAudioRef.downloadUrl.await().toString()
                } else {
                    ""
                }
            }

            /**
             * This will wait for both the audio and image urls to be returned before
             * the variables can be used anywhere else in the code. Await() waits for
             * the tasks to finish, and then returns the result to the variable.
             */
            val audioUrl = audioDeferred.await()

            /**
             * This will create add ONTO the existing entry with new audio and image
             * urls. NOTE*** audioUrl and imageUrl are tasks, so this new entry is
             * only created once the tasks are finished.
             */
            val addAudioAndImageEntry = entry.copy(
                entryAudioUrl = audioUrl,
                entryImageUrl = imageUrl
            )

            /**
             * This will set the new entry to the database. This is the last step in
             * the process, and will only be done once the audio and image urls have
             * been returned. The addAudioAndImageEntry variable is the entry object
             * and it is made up of task variables, that will only be returned once
             * the tasks are finished.
             */
            getEntriesRef(diaryId)
                .document(documentId)
                .set(addAudioAndImageEntry)
                .addOnCompleteListener {
                    onComplete.invoke(it.isSuccessful)
                }
        }
    }

    fun deleteEntry(
        diaryId: String,
        entryId: String,
        onComplete: (Boolean) -> Unit
    ) {
        Log.d("//////////", "entryId: $entryId")
        Log.d("//////////", "diaryId: $diaryId")
        getEntriesRef(diaryId)
            .document(entryId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateEntry(
        diaryId: String,
        entryId: String,
        name: String,
        description: String,
        mood: Int,
        audioUri: Uri?,
        imageUri: Uri?,
        audioUrl: String,
        imageUrl: String,
        date: Timestamp,
        onResult: (Boolean) -> Unit
    ) {

        var updateAudioFile = audioUri
        val updateEntryAudioRef = storage.reference.child("Users/${getUserId()}/Audios/$diaryId")

        var updateImageFile = imageUri
        val updateEntryImageRef = storage.reference.child("Users/${getUserId()}/Images/$diaryId")
        // var updateUploadTask = updateEntryImageRef.putFile(updateImageFile!!)

        // updateUploadTask.isComplete

        var updateData = hashMapOf<String, Any>(
            "entryName" to name,
            "entryDescription" to description,
            "entryMood" to mood,
            "entryDate" to date
        )

        GlobalScope.launch {
            val audioDeferred = async {
                if (updateAudioFile != null) {
                    updateEntryAudioRef.putFile(updateAudioFile).await()
                    updateEntryAudioRef.downloadUrl.await().toString()
                } else {
                    audioUrl
                }
            }

            val imageDeferred = async {
                if (updateImageFile != null) {
                    updateEntryImageRef.putFile(updateImageFile).await()
                    updateEntryImageRef.downloadUrl.await().toString()
                } else {
                    imageUrl
                }
            }

            val audioUrl = audioDeferred.await()
            val imageUrl = imageDeferred.await()

            val addAudioAndImageEntry = updateData.apply {
                put("entryAudioUrl", audioUrl)
                put("entryImageUrl", imageUrl)
            }

            getEntriesRef(diaryId)
                .document(entryId)
                .update(addAudioAndImageEntry)
                .addOnCompleteListener {
                    onResult.invoke(it.isSuccessful)
                }
        }
    }


    // Diary Functions

    fun getUserDiaries(
        userId: String
    ): Flow<Resources<List<Diaries>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {

            snapshotStateListener = getDiariesRef()
                .orderBy("diaryId")
                .whereEqualTo("userId", userId)
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val diaries = snapshot.toObjects(Diaries::class.java)
                        Resources.Success(data = diaries)
                    } else {
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }

        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getUserDiariesByDate(
        userId: String,
        dateMinusDay: Timestamp,
        dateExtraDay: Timestamp
    ): Flow<Resources<List<Diaries>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getDiariesRef()
                //.orderBy("diaryCreatedDate")
                .whereGreaterThan("diaryCreatedDate", dateMinusDay)
                .whereLessThan("diaryCreatedDate", dateExtraDay)
                .whereEqualTo("userId", userId)
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val diaries = snapshot.toObjects(Diaries::class.java)
                        Log.e("//////////", "diaries: $diaries")
                        Resources.Success(data = diaries)
                    } else {
                        Log.e("//////////", "Error getting documents: ", e)
                        Resources.Failure(throwable = e)
                    }
                    trySend(response)
                }

        } catch (e: Exception) {
            trySend(Resources.Failure(e.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getDiary(
        diaryId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Diaries?) -> Unit
    ) {
        getDiariesRef()
            .document(diaryId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Diaries::class.java))
            }
            .addOnFailureListener { result ->
                onError.invoke(result.cause)
            }

    }

    fun addDiary(
        userId: String,
        title: String,
        imageUri: Uri?,
        description: String,
        createdDate: Timestamp,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getDiariesRef().document().id
        var addFile = imageUri
        val addDiaryImageRef = storage.reference.child("Users/${getUserId()}/Images/$documentId")

        addDiaryImageRef.putFile(addFile!!).addOnSuccessListener {
            addDiaryImageRef.downloadUrl.addOnSuccessListener { Url ->

                val diary = Diaries(
                    userId = userId,
                    diaryId = documentId,
                    diaryTitle = title,
                    imageUrl = Url.toString(),
                    diaryDescription = description,
                    diaryCreatedDate = createdDate
                )

                getDiariesRef()
                    .document(documentId)
                    .set(diary)
                    .addOnCompleteListener { result ->
                        onComplete.invoke(result.isSuccessful)
                    }

            }
        }
    }

    fun addDiaryUrl(
        userId: String,
        title: String,
        imageUrl: String,
        description: String,
        createdDate: Timestamp,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getDiariesRef().document().id

        val diary = Diaries(
            userId = userId,
            diaryId = documentId,
            diaryTitle = title,
            imageUrl = imageUrl,
            diaryDescription = description,
            diaryCreatedDate = createdDate
        )

        getDiariesRef()
            .document(documentId)
            .set(diary)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }

    }

    fun deleteDiary(
        diaryId: String,
        onComplete: (Boolean) -> Unit
    ) {
        getDiariesRef().document(diaryId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateDiary(
        diaryId: String,
        title: String,
        imageUri: Uri?,
        imageUrl: String,
        description: String,
        createdDate: Timestamp,
        onResult: (Boolean) -> Unit
    ) {

        Log.e("//////////", "updateDiary: $imageUri")
        Log.e("//////////", "updateDiary: $imageUrl")

        var updateFile: Uri?
        var updateDiaryImageRef: StorageReference
        var updateUploadTask: UploadTask

        /**
         * if there is no imageUri, then the image is a url online
         */
        if (imageUri != null) {

            updateFile = imageUri
            updateDiaryImageRef = storage.reference.child("Users/${getUserId()}/Images/${diaryId}")
            updateUploadTask = updateDiaryImageRef.putFile(updateFile!!)

            updateUploadTask.isComplete

            updateDiaryImageRef.downloadUrl.addOnSuccessListener { Url ->

                val imageUrl = Url.toString()

                val updateData = hashMapOf<String, Any>(
                    "diaryTitle" to title,
                    "diaryDescription" to description,
                    "diaryCreatedDate" to createdDate,
                    "imageUrl" to imageUrl
                )

                getDiariesRef().document(diaryId)
                    .update(updateData)
                    .addOnCompleteListener {
                        onResult(it.isSuccessful)
                    }

            }
        } else {

            val ExistingImageUrl = imageUrl

            val updateData = hashMapOf<String, Any>(
                "diaryTitle" to title,
                "diaryDescription" to description,
                "diaryCreatedDate" to createdDate,
                "imageUrl" to ExistingImageUrl
            )

            getDiariesRef().document(diaryId)
                .update(updateData)
                .addOnCompleteListener {
                    onResult(it.isSuccessful)
                }
        }
    }

    fun signOut() = Firebase.auth.signOut()

}

sealed class Resources<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
) {
    class Loading<T> : Resources<T>()
    class Success<T>(data: T?) : Resources<T>(data = data)
    class Failure<T>(throwable: Throwable?) : Resources<T>(throwable = throwable)
}