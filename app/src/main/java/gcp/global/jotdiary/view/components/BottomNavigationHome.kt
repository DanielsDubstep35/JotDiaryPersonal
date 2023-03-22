package gcp.global.jotdiary.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationHome(
    navController: () -> Unit
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

            /*
            IconButton(onClick = { navController.invoke() }) {
                Icon(Icons.Filled.DateRange, contentDescription = null, tint = MaterialTheme.colors.secondary)
            }
            IconButton(onClick = { navController.invoke() }) {
                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colors.secondary)
            }
            */

            IconButton(
                onClick = { },
                enabled = false
            ) {

            }

            /*
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = MaterialTheme.colors.secondary)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colors.secondary)
            }
            */

        }
    }
}