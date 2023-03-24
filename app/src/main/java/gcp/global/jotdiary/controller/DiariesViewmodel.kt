package gcp.global.jotdiary.controller

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.Resources
import gcp.global.jotdiary.model.repository.StorageRepository
import kotlinx.coroutines.launch

class DiariesViewmodel(
    private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {

    var diariesUiState by mutableStateOf(DiariesUiState())

    fun loadEntries(diaryId: String){
        getUserEntries(diaryId)
    }

    private fun getUserEntries(diaryId:String) = viewModelScope.launch {
        repository.getUserEntries(diaryId).collect {
            diariesUiState = diariesUiState.copy(entriesList = it)
        }
    }

    fun deleteEntry(entryId:String, diaryId: String) = repository.deleteEntry(entryId = entryId, diaryId = diaryId){
        diariesUiState = diariesUiState.copy(entryDeletedStatus = it)
    }

}

data class DiariesUiState(
    val entriesList: Resources<List<Entries>> = Resources.Loading(),
    val entryDeletedStatus: Boolean = false,
)