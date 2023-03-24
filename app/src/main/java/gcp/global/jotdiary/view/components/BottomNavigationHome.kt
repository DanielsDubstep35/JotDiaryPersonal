package gcp.global.jotdiary.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationHome() {
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
                enabled = false
            ) {

            }

        }
    }
}