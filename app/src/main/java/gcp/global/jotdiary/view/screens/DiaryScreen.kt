package gcp.global.jotdiary.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import gcp.global.jotdiary.controller.DiaryUiState
import gcp.global.jotdiary.controller.DiaryViewmodel
import gcp.global.jotdiary.view.components.NestedTopBar
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DiaryScreen(
    diaryViewmodel: DiaryViewmodel,
    diaryId: String,
    navController: NavHostController
) {
    val diaryUiState = diaryViewmodel.diaryUiState

    val isFormsNotBlank = diaryUiState.title.isNotBlank()

    val isDiaryIdNotBlank = diaryId.isNotBlank()
    val icon = if (isFormsNotBlank) Icons.Default.Refresh
    else Icons.Default.Check
    LaunchedEffect(key1 = Unit) {
        if (isDiaryIdNotBlank) {
            diaryViewmodel.getDiary(diaryId = diaryId)
        } else {
            diaryViewmodel.resetState()
        }
    }
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    val previousScreen = "Home"
    val currentScreen = if (isDiaryIdNotBlank) "Edit: ${diaryUiState.title}" else "Add a new Diary"

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isDiaryIdNotBlank) {
                        diaryViewmodel.updateDiary(diaryId = diaryId)
                    } else {
                        diaryViewmodel.addDiary()
                    }
                }
            ) {
                Icon(imageVector = icon, contentDescription = "Save")
            }
        },
        topBar = { NestedTopBar(previousScreen = previousScreen, currentScreen = currentScreen, navController = navController) }
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .padding(padding)
        ) {
            if (diaryUiState.diaryAddedStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Added Diary Successfully")
                    diaryViewmodel.resetDiaryAddedStatus()
                    navController.popBackStack()
                }
            }

            if (diaryUiState.updateDiaryStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Updated Diary Successfully")
                    diaryViewmodel.resetDiaryAddedStatus()
                    navController.popBackStack()
                }
            }

            // Paper

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(400.dp)
                    .background(color = MaterialTheme.colors.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    BasicTextField(
                        value = diaryUiState.title,
                        onValueChange = {
                            diaryViewmodel.onTitleChange(it)
                        },
                        textStyle = TextStyle(
                            fontStyle = MaterialTheme.typography.body1.fontStyle,
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 20.sp,
                        )
                    )
                }

                Divider(color = MaterialTheme.colors.onSurface)

            }

        }
    }
}