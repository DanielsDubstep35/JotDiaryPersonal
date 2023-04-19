package gcp.global.jotdiary.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import gcp.global.jotdiary.view.components.GeneralTopBar
import gcp.global.jotdiary.view.components.bottomBars.BottomNavigationSettings
import gcp.global.jotdiary.viewmodel.SettingsUiState
import gcp.global.jotdiary.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewmodel: SettingsViewModel?,
    onNavToCalenderPage: () -> Unit,
    onNavToHomePage: () -> Unit,
) {

    val settingsUiState = settingsViewmodel?.settingsUiState ?: SettingsUiState()

    var themeEmoji = if (settingsUiState.darkMode) "🌙" else "☀️"

    rememberSystemUiController()

    /*
    if (settingsUiState.darkMode) {
        settingsViewmodel?.darkMode(systemUiController)
    } else {
        settingsViewmodel?.lightMode(systemUiController)
    }
    */

    Scaffold(
        topBar = { GeneralTopBar(currentScreen = "Settings") },
        bottomBar = { BottomNavigationSettings(navToCalenderScreen = onNavToCalenderPage, navToHomeScreen = onNavToHomePage) }
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colors.primary)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = MaterialTheme.colors.primary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Palette,
                        tint = MaterialTheme.colors.onSurface,
                        contentDescription = "Customization Icon",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "Customization",
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Divider(
                    color = MaterialTheme.colors.onSurface,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(color = MaterialTheme.colors.primary)
                ) {
                    Text(
                        text = "Dark Theme? $themeEmoji",
                        modifier = Modifier.alpha(0.7f),
                        color = MaterialTheme.colors.onSurface,
                    )
                    Switch(checked = settingsUiState.darkMode, onCheckedChange = { settingsViewmodel?.onDarkModeChange() })
                }
            }
        }
    }
}
