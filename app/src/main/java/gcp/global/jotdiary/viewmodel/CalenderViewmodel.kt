package gcp.global.jotdiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import gcp.global.jotdiary.model.models.Diaries
import gcp.global.jotdiary.model.repository.Resources
import gcp.global.jotdiary.model.repository.StorageRepository
import kotlinx.coroutines.launch

/**
 * This class is a type of ViewModel that is used to handle functionality from the
 * calenderScreen, which includes displaying diaries by date,
 **/
class CalenderViewModel(
    private val repository: StorageRepository = StorageRepository(),
) : ViewModel() {

    var calenderUiState by mutableStateOf(CalenderUiState())
        private set

    private val _searchState = mutableStateOf(SearchBarState.Closed)
    val searchState: SearchBarState
        get() = _searchState.value

    private val _searchingState = mutableStateOf(SearchingState.Initial)
    val searchingState: SearchingState
        get() = _searchingState.value

    private val _searchQuery = mutableStateOf("")
    val searchQuery: String
        get() = _searchQuery.value

    fun onSearchBarChange(option: SearchBarState) {
        _searchState.value = option
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        calenderUiState = calenderUiState.copy(query = query)
    }

    fun onSearchingStateChange(state: SearchingState) {
        _searchingState.value = state
    }

    fun onDatePicked(dateMinusDay: Timestamp, dateExtraDay: Timestamp) = viewModelScope.launch {
        calenderUiState = calenderUiState.copy(dateMinusDay = dateMinusDay)
        calenderUiState = calenderUiState.copy(dateExtraDay = dateExtraDay)
        getDiariesByDate(repository.user()?.uid.toString(), calenderUiState.dateMinusDay, calenderUiState.dateExtraDay)
    }

    fun onSearchQuery() = viewModelScope.launch {
        getDiariesByName(repository.user()?.uid.toString(), calenderUiState.query)
    }

    /**
     * This function gets the diaries from a specific date from the StorageRepository,
     * GETTER
     *
     * @param - userId - unique
     * @param - date - the date of the diaries to be retrieved
     * @return - void
     **/
    private fun getDiariesByDate(userId: String, dateMinusDay: Timestamp, dateExtraDay: Timestamp) = viewModelScope.launch {
        repository.getUserDiariesByDate(userId, dateMinusDay, dateExtraDay).collect {
            if (it != Resources.Success(emptyList<Diaries>()) ) {
                calenderUiState = calenderUiState.copy(filteredDiariesList = it)
                calenderUiState = calenderUiState.copy(diariesPresent = true)
            } else {
                calenderUiState = calenderUiState.copy(diariesPresent = false)
            }
        }
    }

    private fun getDiariesByName(userId: String, query: String) = viewModelScope.launch {
        repository.getUserDiariesByName(userId, query).collect {
            if (it != Resources.Success(emptyList<Diaries>()) ) {
                calenderUiState = calenderUiState.copy(filteredDiariesList = it)
                calenderUiState = calenderUiState.copy(diariesPresent = true)
            } else {
                calenderUiState = calenderUiState.copy(diariesPresent = false)
            }
        }
    }

    /**
     * This method deletes the selected moment from the selected diary.
     * SETTER
     *
     * @param diaryId - this points to a specific diary
     * @return - void
     **/
    fun deleteDiary(diaryId:String) = viewModelScope.launch {
        repository.deleteDiary(diaryId = diaryId){
            calenderUiState = calenderUiState.copy(diaryDeletedStatus = it)
        }
    }

    fun resetState() = viewModelScope.launch {
        calenderUiState = CalenderUiState()
    }

}

data class CalenderUiState(
    val dateMinusDay: Timestamp = Timestamp.now(),
    val dateExtraDay: Timestamp = Timestamp.now(),
    val filteredDiariesList: Resources<List<Diaries>> = Resources.Loading(),
    val diaryDeletedStatus: Boolean = false,
    val selectedDiary:Diaries? = null,
    val diariesPresent: Boolean = false,
    val query: String = "",
)

enum class SearchBarState {
    Open,
    Closed
}

enum class SearchingState {
    Initial,
    Searching
}