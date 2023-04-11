package gcp.global.jotdiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.Resources
import gcp.global.jotdiary.model.repository.StorageRepository
import kotlinx.coroutines.launch

/**
* DiariesViewmodel
*
* This class is a type of ViewModel that is used to get data from the StorageRepository
* and display it on the screen. The State, aswell as the data is controlled from here.
**/
class DiariesViewmodel(
    private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {

    var diariesUiState by mutableStateOf(DiariesUiState())

    /**
    * loadEntries()
    * This method retrieves all the entries associated with the diary from the
     * database via the diaryId.
    * GETTER
    *
    * @param diaryId - unique for every user agent
    * @return - void
    **/
    fun loadEntries(diaryId: String){
        getUserEntries(diaryId)
    }

    /**
    * This function gets the entries from the StorageRepository, which receives data
    * from the database.
    * GETTER
    *
    * @param - diaryId - unique
    * @return - void
    **/
    private fun getUserEntries(diaryId:String) = viewModelScope.launch {
        repository.getUserEntries(diaryId).collect {
            diariesUiState = diariesUiState.copy(entriesList = it)
        }

    }

    /**
    * This method deletes the selected moment from the selected diary.
    * SETTER
    *
    * @param entryId - the specific moment you want to delete
    * @param diaryId - this points to a specific diary
    * @return - void
    **/
    fun deleteEntry(entryId:String, diaryId: String) = viewModelScope.launch {
        repository.deleteEntry(entryId = entryId, diaryId = diaryId) {
            diariesUiState = diariesUiState.copy(entryDeletedStatus = it)
        }
    }

}

/**
* This data class stores the list of entries from the database, it stores a Loading, or
* it stores an error. It also keeps track of an entry being deleted.
**/
data class DiariesUiState(
    val entriesList: Resources<List<Entries>> = Resources.Loading(),
    val entryDeletedStatus: Boolean = false,
)
