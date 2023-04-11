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
import gcp.global.jotdiary.viewmodel.DiaryViewmodel
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DiaryScreen(
    diaryViewmodel: DiaryViewmodel,
    diaryId: String,
    navController: NavHostController
) {
    val diaryUiState = diaryViewmodel.diaryUiState

    val isDiaryIdNotBlank = diaryId.isNotBlank()

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

    val outlinedFieldColors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = MaterialTheme.colors.primary,
        unfocusedBorderColor = MaterialTheme.colors.primary,
        focusedLabelColor = MaterialTheme.colors.primary,
        unfocusedLabelColor = MaterialTheme.colors.primary,
        cursorColor = MaterialTheme.colors.primary,
        errorCursorColor = Color.Red,
        errorLabelColor = Color.Red,
        errorTrailingIconColor = Color.Red,
        errorLeadingIconColor = Color.Red,
        trailingIconColor = MaterialTheme.colors.primary,
        leadingIconColor = MaterialTheme.colors.primary,
    )

    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }

    if (pickedPhoto != null) {
        diaryViewmodel.onImageChange(pickedPhoto)
    } else {
        diaryViewmodel.onImageChange(null)
    }

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

    val saveOrUpdate = if (isDiaryIdNotBlank) "Update This Diary" else "Save Your New Diary"

    Scaffold(scaffoldState = scaffoldState,
        topBar = { DiaryNestedTopBar(previousScreen = previousScreen, currentScreen = currentScreen, navController = navController, diaryViewmodel = diaryViewmodel) }
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
                        diaryViewmodel.resetDiaryAddedStatus()
                    }
                }

                if (diaryUiState.updateDiaryStatus) {
                    scope.launch {
                        scaffoldState.snackbarHostState
                            .showSnackbar("Updated Diary Successfully")
                        diaryViewmodel.resetDiaryAddedStatus()
                    }
                }

                MaterialDialog(
                    dialogState = dialogState,
                    buttons = {
                        positiveButton(
                            text = "Ok",
                            onClick = {
                                currentDateAndTime = Timestamp(Date(year - 1900, month, day))
                                diaryViewmodel.onDateChange(currentDateAndTime)
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
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = diaryUiState.title,
                            onValueChange = {
                                diaryViewmodel.onTitleChange(it)
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
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = diaryUiState.description,
                            onValueChange = {
                                diaryViewmodel.onDescriptionChange(it)
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
                    coilImage(Uri = pickedPhoto, Modifier.width(300.dp)
                        .height(250.dp)
                        .padding(16.dp), Shape = MaterialTheme.shapes.medium)
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
                            if (pickedPhoto == null && (diaryUiState.imageUri == null && diaryUiState.imageUrl == "")) {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("No Image Selected")
                                    diaryViewmodel.onImageChangeUrl("https://cdn11.bigcommerce.com/s-3uewkq06zr/images/stencil/1280x1280/products/258/543/fluorescent_pink__88610.1492541080.png?c=2")
                                    diaryViewmodel.addDiaryUrl()
                                    diaryViewmodel.resetDiaryAddedStatus()
                                }
                            } else if (pickedPhoto == null && (diaryUiState.imageUri == null && diaryUiState.imageUrl != "") ) {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Using Existing Image...")
                                    diaryViewmodel.onImageChangeUrl(diaryUiState.imageUrl)
                                    diaryViewmodel.updateDiary(diaryId = diaryId)
                                    scaffoldState.snackbarHostState.showSnackbar("Diary Updated")
                                    diaryViewmodel.resetDiaryAddedStatus()
                                }
                            } else if (pickedPhoto != null && (diaryUiState.imageUri != null) ) {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Uploading Image...")
                                    diaryViewmodel.addDiary()
                                        scaffoldState.snackbarHostState.showSnackbar("Image Uploaded")
                                    diaryViewmodel.resetDiaryAddedStatus()
                                }
                            } else {
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("ERROR 😭")
                                    diaryViewmodel.updateDiary(diaryId = diaryId)
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

