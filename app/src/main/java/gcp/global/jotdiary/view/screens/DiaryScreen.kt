package gcp.global.jotdiary.view.screens

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.*
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import gcp.global.jotdiary.R
import gcp.global.jotdiary.controller.DiaryViewmodel
import gcp.global.jotdiary.model.repository.StorageRepository
import gcp.global.jotdiary.view.components.DiaryNestedTopBar
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

    // Outlined text field variables
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

    // take or give an Image from or to the firebase storage
    val storageRef = StorageRepository().storage.reference
    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }

    if (pickedPhoto != null) {
        // Image selected
        diaryViewmodel.onImageChange(pickedPhoto)
    } else {
        // No image selected
        diaryViewmodel.onImageChange(null)
    }

    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> pickedPhoto = uri }
    )

    // Date Variables
    val dialogState = rememberMaterialDialogState()
    val calendar = GregorianCalendar.getInstance()
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)

    var currentDateAndTime: Timestamp

    val saveOrUpdate = if (isDiaryIdNotBlank) "Update This Diary" else "Save Your New Diary"

    var selectedImage = diaryUiState.imageUri

    /*
    val request = ImageRequest.Builder(LocalContext.current)
        .data("${selectedImage?.path}")
        .build()
     */

    var request = selectedImage

    Scaffold(scaffoldState = scaffoldState,
        topBar = { DiaryNestedTopBar(previousScreen = previousScreen, currentScreen = currentScreen, navController = navController, diaryViewmodel = diaryViewmodel) }
    ) { padding ->

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

                // Date Calender
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


                // Date fields
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.End
                ) {

                    // Date Picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        // Display Date in small text
                        Text(
                            text = "${diaryUiState.createdDate.toDate().date}/${diaryUiState.createdDate.toDate().month}/${diaryUiState.createdDate.toDate().year.plus(1900)}",
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

                // Selection fields
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Title Picker
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {

                        // Title
                        Text(
                            text = "Diary Title",
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Title input
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

                    // Description Picker
                    Column(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Description
                        Text(
                            text = "Diary Description",
                            style = TextStyle(
                                fontStyle = MaterialTheme.typography.body1.fontStyle,
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Description input
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

            // Image and Actions
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                if (pickedPhoto != null) {
                    coilImage(Uri = pickedPhoto)
                } else {
                    coilImage(Url = diaryUiState.imageUrl, Modifier = Modifier.fillMaxWidth().height(250.dp).padding(24.dp), Shape = MaterialTheme.shapes.medium)
                }

                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Image Picker
                    Button(
                        onClick = {
                            // Open the users gallery, and get the image
                            singlePhotoLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )

                            //Log.d("Diary", "Imageeee Uri: ${ActivityResultContracts.PickVisualMedia.ImageOnly}")

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

                    // Save or Update
                    Button(
                        onClick = {
                            if (isDiaryIdNotBlank) {
                                diaryViewmodel?.updateDiary(diaryId = diaryId)
                            } else {
                                diaryViewmodel?.addDiary()
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

@Composable
fun coilImage(Url: String, Modifier: Modifier, Shape: Shape) {

    val painter = rememberAsyncImagePainter(
    model = Url,
    imageLoader = ImageLoader.Builder(LocalContext.current).crossfade(true).placeholder(R.drawable.ic_loading_foreground).crossfade(300).build()
    )

    Card(
        modifier = Modifier,
        shape = Shape
    ) {

        Image(
            painter = painter,
            contentDescription = "ImagePainter",
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun coilImage(Uri: Uri?) {

    Card(
        modifier = Modifier
            .width(300.dp)
            .height(250.dp)
            .padding(16.dp),
    ) {

        AsyncImage(
            model = Uri,
            contentDescription = "ImagePainter",
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
fun coilImage() {

    Card(
        modifier = Modifier
            .width(300.dp)
            .height(250.dp)
            .padding(16.dp),
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.saddest),
                contentDescription = "No Image Found",
                modifier = Modifier
                    .size(24.dp)
            )
            Text(
                text = "No Image Found",
                style = TextStyle(
                    fontStyle = MaterialTheme.typography.body1.fontStyle,
                    color = MaterialTheme.colors.onSurface,
                    fontSize = 16.sp,
                )
            )
            Icon(
                painter = painterResource(id = R.drawable.saddest),
                contentDescription = "No Image Found",
                modifier = Modifier
                    .size(24.dp)
            )

        }
    }
}

