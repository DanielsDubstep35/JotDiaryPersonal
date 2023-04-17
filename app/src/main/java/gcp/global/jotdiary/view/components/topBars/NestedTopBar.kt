package gcp.global.jotdiary.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gcp.global.jotdiary.viewmodel.DiaryViewModel
import gcp.global.jotdiary.viewmodel.EntryViewModel

@Composable
fun NestedTopBar(
    navController: NavController,
    currentScreen: String,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
    )
}

@Composable
fun GeneralTopBar(
    currentScreen: String,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight().fillMaxWidth()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    )
}

@Composable
fun DiaryNestedTopBar(
    navController: NavController,
    currentScreen: String,
    diaryViewmodel: DiaryViewModel?
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
                diaryViewmodel?.resetState()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
    )
}

@Composable
fun EntryNestedTopBar(
    onImageAdd: () -> Unit,
    onAudioAdd: () -> Unit,
    onBackPress: () -> Unit,
    currentScreen: String,
    entryViewmodel: EntryViewModel?
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                onBackPress()
                entryViewmodel?.resetState()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onAudioAdd()
            }) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Add Audio",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = {
                onImageAdd()
            }) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Add Image",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}