package gcp.global.jotdiary.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import gcp.global.jotdiary.model.models.Diaries
import gcp.global.jotdiary.model.repository.StorageRepository
import kotlinx.coroutines.launch

/**
 * This class is a type of ViewModel that is used to get the data of a single diary
 * from the StorageRepository and display it on the screen. different actions can
 * then be performed, such as updating, deleting, or adding a new diary.
 **/
class DiaryViewModel (
    private val repository: StorageRepository = StorageRepository(),
): ViewModel() {

    /** This variable gets the current state of the Diary Ui, aswell as sets the state of the Diary Ui*/
    var diaryUiState by mutableStateOf(DiaryUiState())
        private set

    /** This private variable checks if there is a user logged in */
    private val hasUser:Boolean
        get() = repository.hasUser()

    /** This private variable gets the current user */
    private val user: FirebaseUser?
        get() = repository.user()


    /**
     * This method takes in a diary title, and passes it to the Ui State
     *
     * @param title String - This is the new title for the diary
     */
    fun onTitleChange(title:String) = viewModelScope.launch {
        diaryUiState = diaryUiState.copy(title = title)
    }

    /**
     * This method takes in a diary description, and passes it to the Ui State
     *
     * @param description string - This is the new description for the diary
     */
    fun onDescriptionChange(description:String) = viewModelScope.launch {
        diaryUiState = diaryUiState.copy(description = description)
    }

    /**
     * This method takes in a diary date, and passes it to the Ui State
     *
     * @param date Timestamp - This is the new date for the diary
     */
    fun onDateChange(date:Timestamp) = viewModelScope.launch {
        diaryUiState = diaryUiState.copy(createdDate = date)
    }

    /**
     * This method takes in a diary image, and passes it to the Ui State
     *
     * @param imageUri Uri? - This is the new image for the diary. This image
     * needs to come from the users device.
     */
    fun onImageChange(imageUri: Uri?) = viewModelScope.launch {
        diaryUiState = diaryUiState.copy(imageUri = imageUri)
    }

    /**
     * This method takes in a diary image, and passes it to the UiState
     *
     * @param imageUrl String - This is the new image for the diary. This image
     * comes from a url on the internet, and is used as a default image incase
     * no image is selected from the users device.
     */
    fun onImageChangeUrl(imageUrl: String) = viewModelScope.launch {
        diaryUiState = diaryUiState.copy(imageUrl = imageUrl)
    }

    /**
     * This method adds a new diary to the database if there is a user logged in.
     * When the diary is added, a boolean is returned, depending on whether
     * the diary is added or not
     */
    fun addDiary() = viewModelScope.launch {
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
     * This method adds a new diary to the database if there is a user logged in.
     * When the diary is added, a boolean is returned, depending on whether
     * the diary is added or not.
     *
     * This method is only triggered if there is no selected image from the users device.
     */
    fun addDiaryUrl() = viewModelScope.launch {
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
     * This method adds the user inputted diary data to the Ui State, which can be
     * added to the database.
     *
     * @param diary Diaries - This is the new diary the user inputted, that will be used to
     * either add a new diary, or update an existing diary by replacing the diary in
     * question with the user inputted diary.
     */
    fun setDiaryFields(diary: Diaries) = viewModelScope.launch {

        diaryUiState = diaryUiState.copy(
            title = diary.diaryTitle,
            description = diary.diaryDescription,
            createdDate = diary.diaryCreatedDate,
            imageUrl = diary.imageUrl,
        )
    }

    /**
     * This method gets a diary from the database that the user selected
     *
     * @param diaryId String - unique for every user agent
     */
    fun getDiary(diaryId: String) = viewModelScope.launch {
        repository.getDiary(
            diaryId = diaryId,
            onError = {},
        ) {
            diaryUiState = diaryUiState.copy(selectedDiary = it)
            diaryUiState.selectedDiary?.let { it1 -> setDiaryFields(it1) }
        }

    }

    /**
     * This method updates the currently selected diary in the database
     *
     * @param diaryId String - unique for every user agent
     */
    fun updateDiary(diaryId: String) = viewModelScope.launch {
        repository.updateDiary(
            title = diaryUiState.title,
            diaryId = diaryId,
            imageUri = diaryUiState.imageUri,
            imageUrl = diaryUiState.imageUrl,
            description = diaryUiState.description,
            createdDate = diaryUiState.createdDate,
        ) {
            diaryUiState = diaryUiState.copy(updateDiaryStatus = it)
        }
    }

    /**
     * This method is usually triggered if a diary is successfully added or updated.
     * It allows new diaries to be added, or existing diaries to be updated.
     */
    fun resetDiaryAddedStatus() = viewModelScope.launch {
        diaryUiState = diaryUiState.copy(
            diaryAddedStatus = false,
            updateDiaryStatus = false,
        )
    }

    /**
     * This method resets the state of the data class "Diary Ui State", which is
     * useful for when the user is not viewing a diary. Another diary can be selected
     * with new data displayed. after calling this function.
     */
    fun resetState() = viewModelScope.launch {
        diaryUiState = DiaryUiState()
    }

}

/**
 * This holds the diary information that the user inputted or selected, which can
 * then be used to add a diary, display the diary information to the screen, or even
 * update the diary.
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