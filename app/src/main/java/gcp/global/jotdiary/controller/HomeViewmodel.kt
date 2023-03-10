package gcp.global.jotdiary.controller

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.Resources
import gcp.global.jotdiary.model.repository.StorageRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())

    val user = repository.user()
    val hasUser: Boolean
        get() = repository.hasUser()
    private val userId: String
        get() = repository.getUserId()

    fun loadEntries(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserEntries(userId)
                Log.d("HomeViewModel: Line 29, folder: controller/HomeViewModel", "userId = $userId")
            }
        }else{
            homeUiState = homeUiState.copy(entriesList = Resources.Failure(
                throwable = Throwable(message = "The User is not Logged In")
            ))
        }
    }

    private fun getUserEntries(userId:String) = viewModelScope.launch {
        repository.getUserEntries(userId).collect {
            homeUiState = homeUiState.copy(entriesList = it)
        }
    }

    fun deleteEntry(entryId:String) = repository.deleteEntry(entryId = entryId){
        homeUiState = homeUiState.copy(entryDeletedStatus = it)
    }

    fun signOut() = repository.signOut()

}

data class HomeUiState(
    val entriesList: Resources<List<Entries>> = Resources.Loading(),
    val entryDeletedStatus: Boolean = false,
)
















