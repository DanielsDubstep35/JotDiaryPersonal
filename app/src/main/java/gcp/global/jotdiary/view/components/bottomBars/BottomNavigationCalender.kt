package gcp.global.jotdiary.view.components.bottomBars

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun BottomNavigationCalender(
    navToSettingsScreen: () -> Unit,
    navToHomeScreen: () -> Unit,
) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        cutoutShape = MaterialTheme.shapes.large.copy(
            CornerSize(percent = 50)
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            IconButton(
                onClick = { },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colors.onSurface,
                )
            }
            
            IconButton(
                onClick = {
                    navToHomeScreen.invoke()
                },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Home",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.alpha(0.3f)
                )
            }

            IconButton(
                onClick = {
                    navToSettingsScreen.invoke()
                },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.alpha(0.3f)
                )
            }

        }
    }
}