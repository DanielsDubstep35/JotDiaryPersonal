package gcp.global.jotdiary.controller

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcp.global.jotdiary.model.models.Diaries
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

    fun loadDiaries(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserDiaries(userId)
            }
        }else{
            homeUiState = homeUiState.copy(diariesList = Resources.Failure(
                throwable = Throwable(message = "The User is not Logged In")
            ))
        }
    }

    private fun getUserDiaries(userId:String) = viewModelScope.launch {
        repository.getUserDiaries(userId).collect {
            homeUiState = homeUiState.copy(diariesList = it)
        }
    }

    fun deleteDiary(diaryId:String) = repository.deleteDiary(diaryId = diaryId){
        homeUiState = homeUiState.copy(diaryDeletedStatus = it)
    }

    fun signOut() = repository.signOut()

}

data class HomeUiState(
    val diariesList: Resources<List<Diaries>> = Resources.Loading(),
    val diaryDeletedStatus: Boolean = false,
)
















