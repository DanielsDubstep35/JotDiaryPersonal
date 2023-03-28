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

/**
 * DiaryViewmodel
 *
 * This class is a type of ViewModel that is used to get the data of a single diary from
 * the StorageRepository and display it on the screen. The State, aswell as the data is
 * controlled from here.
 *
 **/

class DiaryViewmodel (
    private val repository: StorageRepository = StorageRepository(),
): ViewModel() {
    var diaryUiState by mutableStateOf(DiaryUiState())
        private set

    // This variable checks if there is a user logged in
    private val hasUser:Boolean
        get() = repository.hasUser()

    // This variable gets the current user
    private val user: FirebaseUser?
        get() = repository.user()


    /**
     * This method takes in a diary title, and passes it to the UiState
     *
     * @param title - This is the new title for the diary
     */
    fun onTitleChange(title:String){
        diaryUiState = diaryUiState.copy(title = title)
    }

    /**
     * This method takes in a diary description, and passes it to the UiState
     *
     * @param description - This is the new title for the diary
     */
    fun onDescriptionChange(description:String){
        diaryUiState = diaryUiState.copy(description = description)
    }

    /**
     * This method takes in a diary date, and passes it to the UiState
     *
     * @param date - This is the new date for the diary
     */
    fun onDateChange(date:Timestamp){
        diaryUiState = diaryUiState.copy(createdDate = date)
    }

    /**
     * This method takes in a diary image, and passes it to the UiState
     *
     * @param imageUri - This is the new image for the diary
     */
    fun onImageChange(imageUri: Uri?){
        diaryUiState = diaryUiState.copy(imageUri = imageUri)
    }

    /**
     * This method takes in a diary image, and passes it to the UiState
     *
     * @param imageUrl - This is the new image for the diary
     */
    fun onImageChangeUrl(imageUrl: String){
        diaryUiState = diaryUiState.copy(imageUrl = imageUrl)
    }

    /**
     * This method adds a new diary if there is a logged in user
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

    /**
     * This method adds a diary with a default image if there is a logged in user
     */
    fun addDiaryUrl() {
        if(hasUser) {
            repository.addDiaryUrl(
                userId = user!!.uid,
                title = diaryUiState.title,
                imageUrl = diaryUiState.imageUrl,
                description = diaryUiState.description,
                createdDate = diaryUiState.createdDate,
            ) {
                diaryUiState = diaryUiState.copy(diaryAddedStatus = it)
            }
        }
    }

    /**
     * This method adds the user inputted diary data to the UiState, which can be added
     * to the database.
     *
     * @param diary - This is the new diary the user inputted
     */
    fun setDiaryFields(diary: Diaries){

        diaryUiState = diaryUiState.copy(
            title = diary.diaryTitle,
            description = diary.diaryDescription,
            createdDate = diary.diaryCreatedDate,
            imageUrl = diary.imageUrl,
        )
    }

    /**
     * This method gets a diary that the user selected
     *
     * @param diaryId - unique for every user agent
     */
    fun getDiary(diaryId: String) {
        repository.getDiary(
            diaryId = diaryId,
            onError = {},
        ) {
            diaryUiState = diaryUiState.copy(selectedDiary = it)
            diaryUiState.selectedDiary?.let { it1 -> setDiaryFields(it1) }
        }

    }

    /**
     * This method updates the currently selected diary
     *
     * @param diaryId - unique for every user agent
     */
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

    /**
     * This method is usually triggered if a diary is added or updated
     */
    fun resetDiaryAddedStatus(){
        diaryUiState = diaryUiState.copy(
            diaryAddedStatus = false,
            updateDiaryStatus = false,
        )
    }

    /**
     * This method resets the state of the DiaryUiState, which allows the user
     * to add a new diary, or update an existing diary
     */
    fun resetState() {
        diaryUiState = DiaryUiState()
    }

}

/**
 * This holds the diary information that the user inputted, which can then be used to
 * perform an action.
 */
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