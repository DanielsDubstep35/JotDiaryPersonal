package gcp.global.jotdiary.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import gcp.global.jotdiary.controller.EntryUiState
import gcp.global.jotdiary.controller.EntryViewmodel
import gcp.global.jotdiary.view.components.MoodBottomBar
import gcp.global.jotdiary.view.components.NestedTopBar
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EntryScreen(
    entryViewModel: EntryViewmodel?,
    entryId: String,
    diaryId: String,
    navController: NavHostController
) {
    val entryUiState = entryViewModel?.entryUiState ?: EntryUiState()

    /*
    val isFormsNotBlank = entryUiState.description.isNotBlank() &&
            entryUiState.name.isNotBlank()
    */

    val isEntryIdNotBlank = entryId.isNotBlank()

    LaunchedEffect(key1 = Unit) {
        if (isEntryIdNotBlank) {
            entryViewModel?.getEntry(entryId = entryId, diaryId = diaryId)
        } else {
            entryViewModel?.resetState()
        }
    }
    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    val previousScreen = "Entries"
    val currentScreen = if (isEntryIdNotBlank) entryUiState.name else "Add a new Entry"

    val saveOrUpdate = if (isEntryIdNotBlank) "Update This Diary" else "Save Your New Diary"

    // Date Variables
    val dialogState = rememberMaterialDialogState()
    val calendar = GregorianCalendar.getInstance()
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)

    println(day)
    println(month)
    println(year)
    println("$day/$month/$year")

    var currentDateAndTime: Timestamp

    Scaffold(scaffoldState = scaffoldState,
        topBar = { NestedTopBar(navController = navController, previousScreen = previousScreen, currentScreen = currentScreen) },
        bottomBar = { MoodBottomBar(entryViewModel = entryViewModel, mood = entryUiState.mood) }
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
                    navController.popBackStack()
                }
            }

            if (entryUiState.updateEntryStatus) {
                scope.launch {
                    scaffoldState.snackbarHostState
                        .showSnackbar("Updated Entry Successfully")
                    entryViewModel?.resetEntryAddedStatus()
                    navController.popBackStack()
                }
            }


            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton(
                        text = "Ok",
                        onClick = {
                            currentDateAndTime = Timestamp(Date(year - 1900, month, day))
                            entryViewModel?.onDateChange(currentDateAndTime)
                        },
                        textStyle = TextStyle(
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp
                        )
                    )
                    negativeButton(
                        text = "Cancel",
                        textStyle = TextStyle(
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp
                        )
                    )
                },
                backgroundColor = MaterialTheme.colors.primary,
            ) {
                datepicker(
                    title = "Select A Date",
                    colors = DatePickerDefaults.colors(
                        headerBackgroundColor = MaterialTheme.colors.onSurface,
                        headerTextColor = MaterialTheme.colors.primary,
                        dateInactiveTextColor = MaterialTheme.colors.onSurface,
                        dateInactiveBackgroundColor = MaterialTheme.colors.primary,
                        dateActiveBackgroundColor = MaterialTheme.colors.onSurface,
                        dateActiveTextColor = MaterialTheme.colors.primary,
                        calendarHeaderTextColor = MaterialTheme.colors.onSurface,
                    ),
                ) { date ->
                    year = date.year
                    month = date.monthValue
                    day = date.dayOfMonth

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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display Date in small text
                        Text(
                            text = "${entryUiState.date.toDate().date}/${entryUiState.date.toDate().month}/${entryUiState.date.toDate().year.plus(1900)}",
                            modifier = Modifier.padding(8.dp),
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 12.sp,
                            )
                        )

                        // Date Picker Button
                        IconButton(onClick = { dialogState.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Save",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }

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

            Button(
                onClick = {
                    if (isEntryIdNotBlank) {
                        entryViewModel?.updateEntry(entryId = entryId, diaryId = diaryId)
                    } else {
                        entryViewModel?.addEntry(diaryId = diaryId)
                    }
                },
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Text(
                    text = saveOrUpdate,
                    style = TextStyle(
                        fontStyle = MaterialTheme.typography.body1.fontStyle,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp,
                    )
                )
            }

        }
    }
}