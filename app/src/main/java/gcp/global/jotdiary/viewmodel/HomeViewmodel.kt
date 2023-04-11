package gcp.global.jotdiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gcp.global.jotdiary.model.models.Diaries
import gcp.global.jotdiary.model.repository.Resources
import gcp.global.jotdiary.model.repository.StorageRepository
import kotlinx.coroutines.launch

/**
 * This class is a type of ViewModel that is used to get the data of all the diaries from
 * the StorageRepository and display them as a list to the user. New diaries can then
 * be added and deleted from the list.
 **/
class HomeViewModel(
    private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {

    /** This variable gets the current state of the Home Ui, aswell as sets the state of the Home Ui*/
    var homeUiState by mutableStateOf(HomeUiState())

    /** This private variable checks if there is a user logged in */
    val hasUser: Boolean
        get() = repository.hasUser()

    /** This private variable gets the current user id */
    private val userId: String
        get() = repository.getUserId()

    /**
     * loadDiaries()
     * This method retrieves all the diaries associated with the user from the
     * database via the userId (which is found by the currently logged in user).
     * GETTER
     *
     * @return - void
     **/
    fun loadDiaries(){
        if (hasUser){
            if (userId.isNotBlank()){
                getUserDiaries(userId)
            }
        } else {
            homeUiState = homeUiState.copy(diariesList = Resources.Failure(
                throwable = Throwable(message = "The User is not Logged In")
            ))
        }
    }

    /**
     * This function gets the diaries from the StorageRepository, which receives data
     * from the database.
     * GETTER
     *
     * @param - userId - unique
     * @return - void
     **/
    private fun getUserDiaries(userId:String) = viewModelScope.launch {
        repository.getUserDiaries(userId).collect {
            homeUiState = homeUiState.copy(diariesList = it)
        }
    }

    /**
     * This method deletes the selected moment from the selected diary.
     * SETTER
     *
     * @param diaryId - this points to a specific diary
     * @return - void
     **/
    fun deleteDiary(diaryId:String) = repository.deleteDiary(diaryId = diaryId){
        homeUiState = homeUiState.copy(diaryDeletedStatus = it)
    }

    /**
     * This method signs the currently logged in user out of the app.
     **/
    fun signOut() = repository.signOut()

}

/**
 * This data class stores the list of diaries from the database, it stores a Loading, or
 * it stores an error. It also keeps track of a diary being deleted.
 **/
data class HomeUiState(
    val diariesList: Resources<List<Diaries>> = Resources.Loading(),
    val diaryDeletedStatus: Boolean = false,
)
















