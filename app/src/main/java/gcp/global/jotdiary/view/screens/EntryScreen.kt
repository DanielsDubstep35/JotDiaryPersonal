package gcp.global.jotdiary.view.screens

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gcp.global.jotdiary.controller.EntryUiState
import gcp.global.jotdiary.controller.EntryViewmodel
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EntryScreen(
    entryViewModel: EntryViewmodel?,
    entryId: String,
    onNavigate:() -> Unit
) {
    val entryUiState = entryViewModel?.entryUiState ?: EntryUiState()

    val isFormsNotBlank = entryUiState.description.isNotBlank() &&
            entryUiState.title.isNotBlank()

    val isEntryIdNotBlank = entryId.isNotBlank()
    val icon = if (isFormsNotBlank) Icons.Default.Refresh
        else Icons.Default.Check
    LaunchedEffect(key1 = Unit) {
        if (isEntryIdNotBlank) {
            entryViewModel?.getEntry(entryId)
        } else {
            entryViewModel?.resetState()
        }
    }
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEntryIdNotBlank) {
                        entryViewModel?.updateEntry(entryId)
                    } else {
                        entryViewModel?.addEntry()
                    }
                }
            ) {
                Icon(imageVector = icon, contentDescription = "Save")
            }
        },
    ) { padding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .padding(padding)
        ) {
            if (entryUiState.entryAddedStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Added Entry Successfully")
                    entryViewModel?.resetEntryAddedStatus()
                    onNavigate.invoke()
                }
            }

            if (entryUiState.updateEntryStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Updated Entry Successfully")
                    entryViewModel?.resetEntryAddedStatus()
                    onNavigate.invoke()
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
                        value = entryUiState.name,
                        onValueChange = {
                            entryViewModel?.onNameChange(it)
                        },
                        textStyle = TextStyle(
                            fontStyle = MaterialTheme.typography.body1.fontStyle,
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 20.sp,
                        )
                    )

                    BasicTextField(
                        value = entryUiState.date,
                        onValueChange = {
                            entryViewModel?.onDateChange(it)
                        },
                        textStyle = TextStyle(
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 12.sp,
                            fontStyle = MaterialTheme.typography.body1.fontStyle,
                        ),

                    )
                }

                Divider(color = MaterialTheme.colors.onSurface)

                BasicTextField(
                    value = entryUiState.description,
                    onValueChange = {
                        entryViewModel?.onDescriptionChange(it)
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                )

            }

        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun LightPreview() {
    MaterialTheme() {
        EntryScreen(entryViewModel = null, entryId = "2") {}
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DarkPreview() {
    MaterialTheme() {
        EntryScreen(entryViewModel = null, entryId = "2") {}
    }
}