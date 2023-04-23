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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

@Composable
fun BottomNavigationHome(
    navToSettingsScreen: () -> Unit,
    navToCalenderScreen: () -> Unit,
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
                onClick = { navToCalenderScreen.invoke() },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Navigate to Calender Screen",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.alpha(0.3f)
                )
            }
            
            IconButton(
                onClick = { },
                enabled = false
            ) {

            }

            IconButton(
                onClick = { navToSettingsScreen.invoke() },
                enabled = true
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Navigate to Settings Screen",
                    tint = MaterialTheme.colors.onSurface,
                    modifier = Modifier.alpha(0.3f)
                )
            }

        }
    }
}