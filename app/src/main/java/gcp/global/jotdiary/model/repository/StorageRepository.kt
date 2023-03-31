package gcp.global.jotdiary.model.repository

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import gcp.global.jotdiary.model.models.Diary
import gcp.global.jotdiary.model.models.Moment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val DIARIES_COLLECTION_REF = "Diaries"
const val ENTRIES_COLLECTION_REF = "Entries"
const val USERS_COLLECTION_REF = "Users"

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
    ): Flow<Resources<List<Moment>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = getEntriesRef(diaryId)
                .orderBy("entryId")
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val entries = snapshot.toObjects(Moment::class.java)
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
        onSuccess: (Moment?) -> Unit
    ) {
        getEntriesRef(diaryId)
            .document(entryId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Moment::class.java))
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
        date: Timestamp,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = getEntriesRef(diaryId).document().id

        val entry = Moment(
            momentId = documentId,
            momentName = name,
            momentDescription = description,
            momentMood = mood,
            momentDate = date
        )
        getEntriesRef(diaryId)
            .document(documentId)
            .set(entry)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }
    }

    fun deleteEntry(
        diaryId: String,
        entryId: String,
        onComplete: (Boolean) -> Unit
    ) {
        getEntriesRef(diaryId).document(entryId)
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
        date: Timestamp,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "entryName" to name,
            "entryDescription" to description,
            "entryMood" to mood,
            "entryDate" to date
        )

        getEntriesRef(diaryId).document(entryId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
            }
    }


    // Diary Functions

    fun getUserDiaries(
        userId: String
    ): Flow<Resources<List<Diary>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {

            snapshotStateListener = getDiariesRef()
                .orderBy("diaryId")
                .whereEqualTo("userId", userId)
                .addSnapshotListener{ snapshot, e ->
                    val response = if (snapshot != null) {
                        val diaries = snapshot.toObjects(Diary::class.java)
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

    fun getDiary(
        diaryId: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Diary?) -> Unit
    ) {
        getDiariesRef()
            .document(diaryId)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Diary::class.java))
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

                val diary = Diary(
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

        val diary = Diary(
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
        description: String,
        createdDate: Timestamp,
        onResult: (Boolean) -> Unit
    ) {

        var updateFile = imageUri
        val updateDiaryImageRef = storage.reference.child("Users/${Firebase.auth.currentUser?.uid}/Images/${diaryId}")
        var updateUploadTask = updateDiaryImageRef.putFile(updateFile!!)

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