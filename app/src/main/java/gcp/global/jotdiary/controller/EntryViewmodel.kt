package gcp.global.jotdiary.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.StorageRepository
import kotlin.random.Random

class EntryViewmodel(
    private val repository: StorageRepository = StorageRepository(),
):ViewModel() {
    var entryUiState by mutableStateOf(EntryUiState())
        private set

    private val hasUser:Boolean
        get() = repository.hasUser()

    private val user: FirebaseUser?
        get() = repository.user()

    fun onTitleChange(title:String){
        entryUiState = entryUiState.copy(title = title)
    }

    fun onDescriptionChange(description:String){
        entryUiState = entryUiState.copy(description = description)
    }

    fun onMoodChange(mood:Int){
        entryUiState = entryUiState.copy(mood = mood)
    }

    fun onDateChange(date:String){
        entryUiState = entryUiState.copy(date = date)
    }

    fun onNameChange(name:String){
        entryUiState = entryUiState.copy(name = name)
    }

    fun addEntry(){
        if(hasUser){
            repository.addEntry(
                userId = user!!.uid,
                name = entryUiState.name,
                title = entryUiState.title,
                description = entryUiState.description,
                mood = entryUiState.mood,
                date = entryUiState.date,
            ) {
                entryUiState = entryUiState.copy(entryAddedStatus = it)
            }
        }
    }

    // Whenever a new variable is added, add it here, aswell as to StorageRepository.kt, Entries.kt, and EntryUiState.kt
    fun setEditFields(entry: Entries){
        entryUiState = entryUiState.copy(
            title = entry.diaryTitle,
            name = entry.entryName,
            description = entry.entryDescription,
            mood = entry.entryMood,
            date = entry.entryDate,
        )
    }

    fun getEntry(entryID:String) {
        repository.getEntry(
            entryID = entryID,
            onError = {},
        ) {
            entryUiState = entryUiState.copy(selectedEntry = it)
            entryUiState.selectedEntry?.let { it1 -> setEditFields(it1) }
        }
    }

    fun updateEntry(
        entryId: String
    ) {
        repository.updateEntry(
            title = entryUiState.title,
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
    val title: String = "",
    val name: String = "",
    val description: String = "",
    val mood: Int = 0,
    val date: String = "",
    val entryAddedStatus:Boolean = false,
    val updateEntryStatus:Boolean = false,
    val selectedEntry:Entries? = null,
)