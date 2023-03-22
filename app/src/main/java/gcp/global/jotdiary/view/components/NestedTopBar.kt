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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gcp.global.jotdiary.controller.DiaryViewmodel
import gcp.global.jotdiary.controller.EntryViewmodel

@Composable
fun NestedTopBar(
    navController: NavController,
    previousScreen: String,
    currentScreen: String,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.background(MaterialTheme.colors.primary).padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = previousScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.End
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp)
                )
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
fun DiaryNestedTopBar(
    navController: NavController,
    previousScreen: String,
    currentScreen: String,
    diaryViewmodel: DiaryViewmodel
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.background(MaterialTheme.colors.primary).padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = previousScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.End
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "pointing arrow",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp)
                )
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
                diaryViewmodel.resetState()
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
    navController: NavController,
    previousScreen: String,
    currentScreen: String,
    entryViewmodel: EntryViewmodel
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier.background(MaterialTheme.colors.primary).padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = previousScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.End
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(start = 5.dp, end = 5.dp, top = 5.dp)
                )
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
                entryViewmodel.resetState()
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