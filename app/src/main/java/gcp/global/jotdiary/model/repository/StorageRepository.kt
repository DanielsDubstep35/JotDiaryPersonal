package gcp.global.jotdiary.model.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import gcp.global.jotdiary.model.models.Entries
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val ENTRIES_COLLECTION_REF = "Entries"

class StorageRepository(){

    fun user() = Firebase.auth.currentUser

    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    fun getUserId(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val entriesRef:CollectionReference = Firebase
        .firestore.collection(ENTRIES_COLLECTION_REF)

    fun getUserEntries(
        userId: String
    ): Flow<Resources<List<Entries>>> = callbackFlow {
        var snapshotStateListener:ListenerRegistration? = null

        try {
            snapshotStateListener = entriesRef
                .orderBy("entryID")
                .whereEqualTo("userId", userId)
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
            trySend(Resources.Failure(e?.cause))
            e.printStackTrace()
        }

        awaitClose {
            snapshotStateListener?.remove()
        }
    }

    fun getEntry(
        entryID: String,
        onError: (Throwable?) -> Unit,
        onSuccess: (Entries?) -> Unit
    ) {
        entriesRef
            .document(entryID)
            .get()
            .addOnSuccessListener {
                onSuccess.invoke(it?.toObject(Entries::class.java))
            }
            .addOnFailureListener {result ->
                onError.invoke(result.cause)
            }

    }

    fun addEntry(
        userId: String,
        title: String,
        name: String,
        description: String,
        mood: Int,
        date: String,
        onComplete: (Boolean) -> Unit
    ) {
        val documentId = entriesRef.document().id
        val entry = Entries(
            userId = userId,
            entryID = documentId,
            diaryTitle = title,
            entryName = name,
            entryDescription = description,
            entryMood = mood,
            entryDate = date
        )
        entriesRef
            .document(documentId)
            .set(entry)
            .addOnCompleteListener { result ->
                onComplete.invoke(result.isSuccessful)
            }

    }

    fun deleteEntry(
        entryId: String,
        onComplete: (Boolean) -> Unit
    ) {
        entriesRef.document(entryId)
            .delete()
            .addOnCompleteListener {
                onComplete.invoke(it.isSuccessful)
            }
    }

    fun updateEntry(
        entryId: String,
        title: String,
        name: String,
        description: String,
        mood: Int,
        date: String,
        onResult: (Boolean) -> Unit
    ) {
        val updateData = hashMapOf<String, Any>(
            "diaryTitle" to title,
            "entryName" to name,
            "entryDescription" to description,
            "entryMood" to mood,
            "entryDate" to date
        )

        entriesRef.document(entryId)
            .update(updateData)
            .addOnCompleteListener {
                onResult(it.isSuccessful)
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