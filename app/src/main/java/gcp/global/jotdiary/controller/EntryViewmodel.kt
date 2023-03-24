package gcp.global.jotdiary.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.StorageRepository

class EntryViewmodel(
    private val repository: StorageRepository = StorageRepository(),
):ViewModel() {

    var entryUiState by mutableStateOf(EntryUiState())
        private set

    private val hasUser:Boolean
        get() = repository.hasUser()

    fun onDescriptionChange(description:String){
        entryUiState = entryUiState.copy(description = description)
    }

    fun onMoodChange(mood:Int){
        entryUiState = entryUiState.copy(mood = mood)
    }

    fun onDateChange(date: Timestamp){
        entryUiState = entryUiState.copy(date = date)
    }

    fun onNameChange(name:String){
        entryUiState = entryUiState.copy(name = name)
    }

    fun addEntry(diaryId: String){
        if(hasUser){
            repository.addEntry(
                diaryId = diaryId,
                name = entryUiState.name,
                description = entryUiState.description,
                mood = entryUiState.mood,
                date = entryUiState.date,
            ) {
                entryUiState = entryUiState.copy(entryAddedStatus = it)
            }
        }
    }

    fun setEntryFields(entry: Entries){
        entryUiState = entryUiState.copy(
            name = entry.entryName,
            description = entry.entryDescription,
            mood = entry.entryMood,
            date = entry.entryDate,
        )
    }

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

    fun updateEntry(
        entryId: String,
        diaryId: String
    ) {
        repository.updateEntry(
            diaryId = diaryId,
            name = entryUiState.name,
            description = entryUiState.description,
            mood = entryUiState.mood,
            date = entryUiState.date,
            entryId = entryId
        ) {
            entryUiState = entryUiState.copy(updateEntryStatus = it)
        }
    }

    fun resetEntryAddedStatus(){
        entryUiState = entryUiState.copy(
            entryAddedStatus = false,
            updateEntryStatus = false,
        )
    }

    fun resetState() {
        entryUiState = EntryUiState()
    }

}

data class EntryUiState(
    val entryID: String = "",
    val name: String = "Diary Entry",
    val description: String =  "Describe yourself :)",
    var mood: Int = 4,
    val date: Timestamp = Timestamp.now(),
    val entryAddedStatus:Boolean = false,
    val updateEntryStatus:Boolean = false,
    val selectedEntry:Entries? = null,
)