package gcp.global.jotdiary.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.StorageRepository

/**
 * This class is a type of ViewModel that is used to get the data of a single diary from
 * the StorageRepository and display it on the screen. This can then be updated and
 * deleted, or you could even add a new diary
 **/
class EntryViewmodel(
    private val repository: StorageRepository = StorageRepository(),
):ViewModel() {

    /** This variable gets the current state of the Entry Ui, aswell as sets the state of the Entry Ui*/
    var entryUiState by mutableStateOf(EntryUiState())
        private set

    /** This private variable checks if there is a user logged in */
    private val hasUser:Boolean
        get() = repository.hasUser()

    /**
     * This method takes in an entry description, and passes it to the Ui State
     *
     * @param description String - This is the new description for the entry
     */
    fun onDescriptionChange(description:String){
        entryUiState = entryUiState.copy(description = description)
    }

    /**
     * This method takes in a entry mood, and passes it to the Ui State
     *
     * @param mood Int - This is the new mood for the entry
     */
    fun onMoodChange(mood:Int){
        entryUiState = entryUiState.copy(mood = mood)
    }

    /**
     * This method takes in a file path from the system, and passes it to the Ui
     * State. The file should be an image file
     *
     * @param imageUri Uri? - This is the new image for the entry
     */
    fun onImageChange(imageUri: Uri?){
        entryUiState = entryUiState.copy(imageUri = imageUri)
    }

    /**
     * This method takes in an entry image (usually a firebase url), and passes it to
     * the UiState
     *
     * @param imageUrl String - This is the new image for the diary. This image
     * comes from a url on the internet, and is used as a default image incase
     * no image is selected from the users device.
     */
    fun onImageChangeUrl(imageUrl: String){
        entryUiState = entryUiState.copy(imageUrl = imageUrl)
    }

    /**
     * This method takes in a file path from the system, and passes it to the Ui
     * State. The file should be an audio file
     *
     * @param audioUri Uri? - This is the new audio for the entry
     */
    fun onAudioChange(audioUri: Uri?){
        entryUiState = entryUiState.copy(audioUri = audioUri)
    }

    /**
     * This method takes in a diary date, and passes it to the Ui State.
     *
     * @param date Timestamp - This is the new date for the diary
     */
    fun onDateChange(date: Timestamp){
        entryUiState = entryUiState.copy(date = date)
    }

    /**
     * This method takes in a diary name, and passes it to the Ui State
     *
     * @param name String - This is the new name for the diary
     */
    fun onNameChange(name:String){
        entryUiState = entryUiState.copy(name = name)
    }

    /**
     * This method adds a new entry to the database if there is a user logged in.
     * When the entry is added, a boolean is returned, depending on whether
     * the diary is added or not
     *
     * @param diaryId String - This is the diary the entry is being added to via the Id
     */
    fun addEntry(diaryId: String){
        if(hasUser){
            repository.addEntry(
                diaryId = diaryId,
                name = entryUiState.name,
                description = entryUiState.description,
                mood = entryUiState.mood,
                audioUri = entryUiState.audioUri,
                imageUri = entryUiState.imageUri,
                date = entryUiState.date,
            ) {
                entryUiState = entryUiState.copy(entryAddedStatus = it)
            }
        }
    }

    /**
     * This method adds a new entry to the database if there is a user logged in.
     * When the entry is added, a boolean is returned, depending on whether
     * the entry is added or not.
     *
     * This method is only triggered if there is no selected image from the users device.
     *
     * @param diaryId String - This is the diary the entry is being added to via the Id
     */
    fun addEntryUrl(diaryId: String) {
        if(hasUser){
            repository.addEntryUrl(
                diaryId = diaryId,
                name = entryUiState.name,
                description = entryUiState.description,
                mood = entryUiState.mood,
                audioUri = entryUiState.audioUri,
                imageUrl = entryUiState.imageUrl,
                date = entryUiState.date,
            ) {
                entryUiState = entryUiState.copy(entryAddedStatus = it)
            }
        }
    }

    /**
     * This method sets the ui state of the entry to the selected entry. This allows
     * other functions to access the selected entry, and add / change / delete parts
     * of the entry.
     *
     * @param entry Entries - This is the entry that is being selected
     */
    fun setEntryFields(entry: Entries){
        entryUiState = entryUiState.copy(
            name = entry.entryName,
            description = entry.entryDescription,
            mood = entry.entryMood,
            date = entry.entryDate,
            audioUrl = entry.entryAudioUrl,
            imageUrl = entry.entryImageUrl,
        )
    }

    /**
     * This method gets an entry from the database, and sets the ui state of the entry
     * to the selected entry.
     *
     * @param entryId String - This is the entry that is being selected via the Id
     * @param diaryId String - This is the diary the entry is being selected from via the Id
     */
    fun getEntry(entryId:String, diaryId: String) {
        repository.getEntry(
            diaryId = diaryId,
            entryId = entryId,
            onError = {},
        ) {
            entryUiState = entryUiState.copy(selectedEntry = it)
            entryUiState.selectedEntry?.let { it1 -> setEntryFields(it1) }
        }
    }

    /**
     * This method updates an entry in the database if there is a user logged in.
     * When the entry is updated, a boolean is returned, depending on whether
     * the entry is updated or not
     *
     * @param entryId String - This is the entry that is being updated via the Id
     * @param diaryId String - This is the diary the entry is being updated from via the Id
     */
    fun updateEntry(
        entryId: String,
        diaryId: String
    ) {
        repository.updateEntry(
            diaryId = diaryId,
            name = entryUiState.name,
            description = entryUiState.description,
            mood = entryUiState.mood,
            audioUri = entryUiState.audioUri,
            imageUri = entryUiState.imageUri,
            audioUrl = entryUiState.audioUrl,
            imageUrl = entryUiState.imageUrl,
            date = entryUiState.date,
            entryId = entryId
        ) {
            entryUiState = entryUiState.copy(updateEntryStatus = it)
        }
    }

    /**
     * This method resets the entry added status to false. This viewmodel can then
     * be used again to add / view / change / delete another entry.
     */
    fun resetEntryAddedStatus(){
        entryUiState = entryUiState.copy(
            entryAddedStatus = false,
            updateEntryStatus = false,
        )
    }

    /**
     * This method clears the entry ui state. This returns the viewmodel to its default
     * values, which can be used to display another entry by filling the ui state with
     * a different entry, or it can display a blank new entry.
     */
    fun resetState() {
        entryUiState = EntryUiState()
    }

}

/**
 * This holds the entry information that the user inputted or selected, which can
 * then be used to add an entry, display the entry information to the screen, or even
 * update the entry.
 */
data class EntryUiState(
    val entryID: String = "",
    val name: String = "Diary Entry",
    val description: String =  "Describe yourself :)",
    var mood: Int = 4,
    var audioUri: Uri? = null,
    var audioUrl: String = "",
    var imageUri: Uri? = null,
    var imageUrl: String = "",
    val date: Timestamp = Timestamp.now(),
    val entryAddedStatus:Boolean = false,
    val updateEntryStatus:Boolean = false,
    val selectedEntry:Entries? = null,
)