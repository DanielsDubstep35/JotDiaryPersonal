package gcp.global.jotdiary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    var settingsUiState by mutableStateOf(SettingsUiState())
        private set

    fun onDarkModeChange() = viewModelScope.launch {
        settingsUiState = settingsUiState.copy(darkMode = !settingsUiState.darkMode)
    }

}

data class SettingsUiState(
    val darkMode: Boolean = false,
)
