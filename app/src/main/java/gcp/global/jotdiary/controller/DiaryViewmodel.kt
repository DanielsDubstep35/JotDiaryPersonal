package gcp.global.jotdiary.controller

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import gcp.global.jotdiary.model.models.Diaries
import gcp.global.jotdiary.model.repository.StorageRepository

class DiaryViewmodel(
    private val repository: StorageRepository = StorageRepository(),
):ViewModel() {
    var diaryUiState by mutableStateOf(DiaryUiState())
        private set

    private val hasUser:Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()

    fun onTitleChange(title:String){
        diaryUiState = diaryUiState.copy(title = title)
    }

    fun onDescriptionChange(description:String){
        diaryUiState = diaryUiState.copy(description = description)
    }

    fun onDateChange(date:Timestamp){
        diaryUiState = diaryUiState.copy(createdDate = date)
    }

    fun onImageChange(imageUri: Uri?){
        diaryUiState = diaryUiState.copy(imageUri = imageUri)
    }

    /*
    repository.storage.reference.child("Users/${Firebase.auth.currentUser?.uid}/Images/${diaryId}")
                .downloadUrl
                .addOnSuccessListener {
                    Log.d("GOT IMAGE", "TASK HAS COMPLETED")
                    diaryUiState = diaryUiState.copy(imageUri = it)
                }
                .addOnFailureListener {
                    diaryUiState = diaryUiState.copy(imageUri = null)
                    Log.d("MISSED IMAGE", "TASK HAS FAILED")
                }
    */


    fun addDiary(){
        if(hasUser){

            repository.addDiary(
                userId = user!!.uid,
                title = diaryUiState.title,
                imageUri = diaryUiState.imageUri,
                description = diaryUiState.description,
                createdDate = diaryUiState.createdDate,
            ) {
                diaryUiState = diaryUiState.copy(diaryAddedStatus = it)
            }
        }
    }

    fun setDiaryFields(diary: Diaries){

        diaryUiState = diaryUiState.copy(
            title = diary.diaryTitle,
            description = diary.diaryDescription,
            createdDate = diary.diaryCreatedDate,
            imageUrl = diary.imageUrl,
        )
    }

    fun getDiary(diaryId: String) {
        repository.getDiary(
            diaryId = diaryId,
            onError = {},
        ) {
            diaryUiState = diaryUiState.copy(selectedDiary = it)
            diaryUiState.selectedDiary?.let { it1 -> setDiaryFields(it1) }
        }

    }

    fun updateDiary(
        diaryId: String
    ) {
        repository.updateDiary(
            title = diaryUiState.title,
            diaryId = diaryId,
            imageUri = diaryUiState.imageUri,
            description = diaryUiState.description,
            createdDate = diaryUiState.createdDate,
        ) {
            diaryUiState = diaryUiState.copy(updateDiaryStatus = it)

        }
    }

    fun resetDiaryAddedStatus(){
        diaryUiState = diaryUiState.copy(
            diaryAddedStatus = false,
            updateDiaryStatus = false,
        )
    }

    fun resetState() {
        diaryUiState = DiaryUiState()
    }

}

data class DiaryUiState(
    val diaryId: String = "",
    val title: String = "",
    val imageUri: Uri? = null,
    val imageUrl: String = "",
    val description: String = "",
    val createdDate: Timestamp = Timestamp.now(),
    val diaryAddedStatus:Boolean = false,
    val updateDiaryStatus:Boolean = false,
    val selectedDiary:Diaries? = null,
)