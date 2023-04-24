package gcp.global.jotdiary.view.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.*
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import gcp.global.jotdiary.view.components.DiaryNestedTopBar
import gcp.global.jotdiary.view.components.audio.coilImage
import gcp.global.jotdiary.viewmodel.DiaryUiState
import gcp.global.jotdiary.viewmodel.DiaryViewModel
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DiaryScreen(
    diaryViewmodel: DiaryViewModel?,
    diaryId: String,
    navController: NavHostController
) {
    val diaryUiState = diaryViewmodel?.diaryUiState ?: DiaryUiState()

    val isDiaryIdNotBlank = diaryId.isNotBlank()

    LaunchedEffect(key1 = Unit) {
        if (isDiaryIdNotBlank) {
            diaryViewmodel?.getDiary(diaryId = diaryId)
        } else {
            diaryViewmodel?.resetState()
        }
    }

    val scope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    val currentScreen = if (isDiaryIdNotBlank) "Edit: ${diaryUiState.title}" else "Add a new Diary"

    val outlinedFieldColors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = MaterialTheme.colors.onSurface,
        unfocusedBorderColor = MaterialTheme.colors.onSurface,
        focusedLabelColor = MaterialTheme.colors.onSurface,
        unfocusedLabelColor = MaterialTheme.colors.onSurface,
        cursorColor = MaterialTheme.colors.onSurface,
        errorCursorColor = Color.Red,
        errorLabelColor = Color.Red,
        errorTrailingIconColor = Color.Red,
        errorLeadingIconColor = Color.Red,
        trailingIconColor = MaterialTheme.colors.onSurface,
        leadingIconColor = MaterialTheme.colors.onSurface,
    )

    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }

    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> pickedPhoto = uri }
    )

    val dialogState = rememberMaterialDialogState()
    val calendar = GregorianCalendar.getInstance()
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)

    var currentDateAndTime: Timestamp

    val saveOrUpdate = if (isDiaryIdNotBlank) "Update Diary" else "Save Diary"

    Scaffold(scaffoldState = scaffoldState,
        topBar = { DiaryNestedTopBar(currentScreen = currentScreen, navController = navController, diaryViewmodel = diaryViewmodel) }
    ) { padding ->


        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { /*TODO*/ }) {

                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .padding(padding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                if (diaryUiState.diaryAddedStatus) {
                    scope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar("Added Diary Successfully")
                        diaryViewmodel?.resetDiaryAddedStatus()
                    }
                }

                if (diaryUiState.updateDiaryStatus) {
                    scope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar("Updated Diary Successfully")
                        diaryViewmodel?.resetDiaryAddedStatus()
                    }
                }

                MaterialDialog(
                    dialogState = dialogState,
                    buttons = {
                        positiveButton(
                            text = "Ok",
                            onClick = {
                                currentDateAndTime = Timestamp(Date(year - 1900, month, day))
                                diaryViewmodel?.onDateChange(currentDateAndTime)
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

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.End
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {

                        Text(
                            text = "${diaryUiState.createdDate.toDate().date}/${diaryUiState.createdDate.toDate().month}/${diaryUiState.createdDate.toDate().year.plus(1900)}",
                            modifier = Modifier.padding(8.dp),
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 12.sp,
                            )
                        )

                        IconButton(onClick = { dialogState.show() }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Save",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }

                }

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Text(
                            text = "Diary Title",
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 24.sp,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = diaryUiState.title,
                            onValueChange = {
                                diaryViewmodel?.onTitleChange(it)
                            },
                            textStyle = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 20.sp,
                            ),
                            colors = outlinedFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )

                    }

                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Text(
                            text = "Diary Description",
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 24.sp,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = diaryUiState.description,
                            onValueChange = {
                                diaryViewmodel?.onDescriptionChange(it)
                            },
                            textStyle = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 20.sp,
                            ),
                            colors = outlinedFieldColors,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                    }

                }

            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                if (pickedPhoto != null) {
                    coilImage(Uri = pickedPhoto, Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(24.dp), Shape = MaterialTheme.shapes.medium)
                } else {
                    coilImage(Url = diaryUiState.imageUrl, Modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(24.dp), Shape = MaterialTheme.shapes.medium)
                }

                Row(
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Button(
                        onClick = {

                            singlePhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )

                        },
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Pick an Image",
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.onSurface,
                                fontSize = 16.sp,
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (pickedPhoto == null && (diaryUiState.imageUrl == "")) {
                                scope.launch {
                                    diaryViewmodel?.onImageChangeUrl("https://i.pinimg.com/originals/09/76/f3/0976f36bdb7a621383d266c5328ce4a4.jpg")
                                    diaryViewmodel?.addDiaryUrl()
                                    diaryViewmodel?.resetDiaryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("New Diary Created")
                                }
                            } else if (pickedPhoto == null && (diaryUiState.imageUrl != "") ) {
                                scope.launch {
                                    diaryViewmodel?.onImageChangeUrl(diaryUiState.imageUrl)
                                    diaryViewmodel?.updateDiary(diaryId = diaryId)
                                    diaryViewmodel?.resetDiaryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Diary Updated")
                                }
                            } else if (pickedPhoto != null && (diaryUiState.imageUrl == "") ) {
                                scope.launch {
                                    diaryViewmodel?.onImageChange(pickedPhoto)
                                    diaryViewmodel?.addDiary()
                                    diaryViewmodel?.resetDiaryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("New Diary Created")
                                }
                            } else if (pickedPhoto != null && (diaryUiState.imageUrl != "") ) {
                                scope.launch {
                                    diaryViewmodel?.onImageChange(pickedPhoto)
                                    diaryViewmodel?.updateDiary(diaryId = diaryId)
                                    diaryViewmodel?.resetDiaryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Diary Updated")
                                }
                            }
                        },
                        modifier = Modifier
                            .height(60.dp)
                            .wrapContentWidth()
                            .padding(8.dp)
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
    }
}
