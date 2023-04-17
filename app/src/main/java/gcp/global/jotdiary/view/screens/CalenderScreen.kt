package gcp.global.jotdiary.view.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import gcp.global.jotdiary.model.models.Diaries
import gcp.global.jotdiary.model.repository.Resources
import gcp.global.jotdiary.view.components.GeneralTopBar
import gcp.global.jotdiary.view.components.bottomBars.BottomNavigationCalender
import gcp.global.jotdiary.viewmodel.CalenderUiState
import gcp.global.jotdiary.viewmodel.CalenderViewModel
import java.util.*

@Composable
fun CalenderScreen(
    calenderViewModel: CalenderViewModel?,
    onNavToSettingsPage: () -> Unit,
    onNavToHomePage: () -> Unit,
    onDiaryClick: (id: String) -> Unit,
    onNavToDiaryEditPage: (id: String) -> Unit,
) {
    val calenderUiState = calenderViewModel?.calenderUiState ?: CalenderUiState()

    val dialogState = rememberMaterialDialogState(false)

    var editDiaryDialog by remember {
        mutableStateOf(false)
    }

    var selectedDiary: Diaries? by remember {
        mutableStateOf(null)
    }


    val calendar = GregorianCalendar.getInstance()
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)
    // var chosenDateandTime: Timestamp

    Scaffold(
        topBar = { GeneralTopBar(currentScreen = "Calender") },
        bottomBar = { BottomNavigationCalender(navToSettingsScreen = onNavToSettingsPage, navToHomeScreen = onNavToHomePage) }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.primary)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Card(
                    backgroundColor = MaterialTheme.colors.background,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .padding(8.dp)
                        .wrapContentSize()
                ) {
                    Text(
                        text = "Search Calenders by Date!",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.body1.fontFamily,
                            fontSize = 16.sp,
                        ),
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Button(
                    onClick = {
                        dialogState.show()
                        calenderViewModel?.resetState()
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.onSurface,
                        contentColor = MaterialTheme.colors.primary
                    ),
                ) {
                    Text(
                        text = "Pick a Date!",
                        color = MaterialTheme.colors.primary,
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.body1.fontFamily,
                            fontSize = 16.sp,
                        ),
                    )
                }
            }

            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton(
                        text = "Ok",
                        onClick = {
                            calenderViewModel?.resetState()
                            // chosenDateandTime = Timestamp(Date(year - 1900, month, day))
                            calenderViewModel?.onDatePicked( dateMinusDay = Timestamp(Date(year - 1900, month, day - 1)), dateExtraDay = Timestamp(Date(year - 1900, month, day + 1)))
                            dialogState.hide()
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
                    title = "Choose A Date to get Diaries from!",
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

            when (calenderUiState.filteredDiariesList) {

                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }

                is Resources.Success -> {
                    if (calenderUiState.filteredDiariesList.data == emptyList<Diaries>()) {
                        Text(
                            text = "No Diaries Found!",
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(1),
                            contentPadding = PaddingValues(4.dp),
                        ) {
                            items(
                                calenderUiState.filteredDiariesList.data!!
                            ) { diary ->
                                DiaryItem(
                                    diaries = diary,
                                    onLongClick = {
                                        editDiaryDialog = true
                                        selectedDiary = diary
                                    },
                                    onClick = {
                                        onDiaryClick.invoke(diary.diaryId)
                                    },
                                    onDiaryEditClick = {
                                        onNavToDiaryEditPage.invoke(diary.diaryId)
                                    }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(visible = editDiaryDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                editDiaryDialog = false
                            },
                            title = { Text(
                                text = "Delete this Diary?",
                                color = MaterialTheme.colors.onSurface,
                            ) },
                            backgroundColor = MaterialTheme.colors.primary,
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedDiary?.diaryId?.let {
                                            calenderViewModel?.deleteDiary(it)
                                        }
                                        editDiaryDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.onSurface
                                    ),
                                ) {
                                    Text(
                                        text = "Delete",
                                        color = MaterialTheme.colors.surface
                                    )
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { editDiaryDialog = false },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = MaterialTheme.colors.surface
                                    ),
                                ) {
                                    Text(
                                        text = "Cancel",
                                        color = MaterialTheme.colors.onSurface
                                    )
                                }
                            }
                        )
                    }
                }

                is Resources.Failure -> {
                    Text(
                        text = calenderUiState
                            .filteredDiariesList.throwable?.localizedMessage ?: "Unknown Error",
                        color = MaterialTheme.colors.onSurface,
                    )
                    Log.e("///// ERROR /////", "Error: ${calenderUiState.filteredDiariesList.throwable?.localizedMessage}")
                }
            }
        }
    }
}