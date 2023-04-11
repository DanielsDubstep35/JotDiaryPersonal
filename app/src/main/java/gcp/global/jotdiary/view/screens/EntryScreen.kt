package gcp.global.jotdiary.view.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.*
import com.google.firebase.Timestamp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import gcp.global.jotdiary.view.components.EntryNestedTopBar
import gcp.global.jotdiary.view.components.MoodBottomBar
import gcp.global.jotdiary.view.components.audio.*
import gcp.global.jotdiary.view.theme.JotDiaryTheme
import gcp.global.jotdiary.viewmodel.EntryUiState
import gcp.global.jotdiary.viewmodel.EntryViewmodel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EntryScreen(
    entryViewModel: EntryViewmodel,
    entryId: String,
    diaryId: String,
    navController: NavHostController
) {
    val entryUiState = entryViewModel?.entryUiState ?: EntryUiState()

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

    val saveOrUpdate = if (isEntryIdNotBlank) "Update" else "Save"

    var recordedAudio by remember {
        mutableStateOf<Uri?>(null)
    }

    val AudioPlayModifier = if (recordedAudio == null) Modifier.alpha(1f) else Modifier.alpha(1f)

    val dialogState = rememberMaterialDialogState()

    // to view the image
    var imageDialog by remember {
        mutableStateOf(false)
    }

    val calendar = GregorianCalendar.getInstance()
    var day = calendar.get(Calendar.DAY_OF_MONTH)
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)
    var currentDateAndTime: Timestamp

    var pickedPhoto by remember { mutableStateOf<Uri?>(null) }
    var pickedAudio by remember { mutableStateOf<Uri?>(null) }

    // AudioPermissionsState
    val audioPermissionsState = rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)

    if (pickedPhoto != null) {
        entryViewModel.onImageChange(pickedPhoto)
    } else {
        entryViewModel.onImageChange(null)
    }

    if (pickedAudio != null) {
        entryViewModel.onAudioChange(pickedAudio)
    } else {
        entryViewModel.onAudioChange(null)
    }

    val singlePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> pickedPhoto = uri }
    )

    // Camera permission state
    val micPermissionState = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )

    var clickedCount = 0

    // Audio Stuff
    val context = LocalContext.current
    var entryAudioFile = File(context.getExternalFilesDir(null), "file.mp3")
    var recorderClicked = false
    var playerClicked = true

    val audioPlayer = JotDiaryAudioPlayer(context)
    val audioRecorder = JotDiaryAudioRecorder(context, audioPermissionsState)
    var audioLevels by remember { mutableStateOf(0) }

    Scaffold(scaffoldState = scaffoldState,
        topBar = { EntryNestedTopBar(previousScreen = previousScreen, currentScreen = currentScreen, entryViewmodel = entryViewModel, onBackPress = {

            entryAudioFile.delete()

            navController.popBackStack()

        },
        onImageAdd = {

            singlePhotoLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

        },
        onAudioAdd = {
            recorderClicked = !recorderClicked

            if(recorderClicked) {
                audioRecorder.let {
                    try {
                        scope.apply {
                            launch { it.start(entryAudioFile) }
                            launch {
                                while (it.recording) {
                                    audioLevels = it.getAudioLevels()
                                    delay(500)
                                }
                            }
                            launch { scaffoldState.snackbarHostState.currentSnackbarData?.dismiss() }
                            launch { scaffoldState.snackbarHostState.showSnackbar("Recording Started") }
                        }
                    } catch (e: IOException) {
                        Log.e("Entry Screen, Line 249", "prepare() failed")
                    }
                }
            } else {
                audioRecorder.apply {
                    try {
                        this.stop(entryAudioFile)
                        scope.apply {
                            launch { scaffoldState.snackbarHostState.currentSnackbarData?.dismiss() }
                            launch { scaffoldState.snackbarHostState.showSnackbar("Recording Stopped") }
                        }
                        pickedAudio = entryAudioFile.toUri()

                    } catch (e: IOException) {
                        Log.e("Entry Screen, Line 249", "prepare() failed")
                    }
                }
            }
        } ) },
        bottomBar = { MoodBottomBar(entryViewModel = entryViewModel, mood = entryUiState.mood) }
    ) { padding ->

        /**
        if (entryUiState.entryID == "") {

        }
         **/

        AnimatedVisibility(
            visible = imageDialog,
        ) {
            AlertDialog(
                onDismissRequest = {
                    imageDialog = false
                },
                title = {
                    if (entryUiState.imageUrl != "") {
                        coilImage(
                            Url = entryUiState.imageUrl,
                            Modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),
                            Shape = MaterialTheme.shapes.medium
                        )
                    } else if (entryUiState.imageUri != null) {
                        coilImage(
                            Uri = entryUiState.imageUri,
                            Modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            Shape = MaterialTheme.shapes.medium
                        )
                    } else {
                        Text(
                            text = "No Image Selected, add an image!",
                            color = MaterialTheme.colors.onSurface,
                        )
                    }
                        },
                backgroundColor = MaterialTheme.colors.primary,
                confirmButton = {
                    Button(
                        onClick = {
                            imageDialog = false
                            entryUiState.imageUri = null
                            pickedPhoto = null
                            entryUiState.imageUrl = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Red
                        ),
                        modifier = if (entryUiState.imageUrl != "" || entryUiState.imageUri != null) Modifier.alpha(1f) else Modifier
                            .alpha(0f)
                            .size(0.dp),
                        enabled = entryUiState.imageUrl != "" || entryUiState.imageUri != null
                    ) {
                        Text(
                            text = "Delete Image",
                            color = MaterialTheme.colors.primary
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            imageDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.onSurface
                        ),
                    ) {
                        Text(
                            text = "Stop Viewing",
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            )
        }

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
                scope.apply {
                    launch { scaffoldState.snackbarHostState.showSnackbar("Updated Entry Successfully") }
                    launch { entryViewModel?.resetEntryAddedStatus() }
                    launch { navController.popBackStack() }
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
                        Text(
                            text = "${entryUiState.date.toDate().date}/${entryUiState.date.toDate().month}/${entryUiState.date.toDate().year.plus(1900)}",
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

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {

                Button(
                    onClick = {

                        if (playerClicked) {
                            // if the file is 0 bytes, then it means that the user has not recorded anything
                            // Check firebase for an audio file
                            if (entryAudioFile.length() == 0L && entryUiState.audioUrl != "") {
                                audioPlayer.apply {
                                    try {
                                        this.playFirebaseFile(entryUiState.audioUrl)
                                        scope.apply {
                                            launch {
                                                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                                scaffoldState.snackbarHostState.showSnackbar("Playing Audio")
                                            }
                                        }
                                        playerClicked = !playerClicked
                                    } catch (e: Exception) {
                                        Log.d("////////", e.toString())
                                    }
                                }
                            } else if (entryAudioFile.length() != 0L) {
                                audioPlayer.apply {
                                    try {
                                        this.playFile(entryAudioFile)
                                        scope.apply {
                                            launch {
                                                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                                scaffoldState.snackbarHostState.showSnackbar("Playing Audio")
                                            }
                                        }
                                        playerClicked = !playerClicked
                                    } catch (e: Exception) {
                                        Log.d("////////", e.toString())
                                    }
                                }
                            } else {
                                scope.apply {
                                    launch {
                                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                        scaffoldState.snackbarHostState.showSnackbar("Record audio to get started 🤩🤩🤩")
                                    }
                                }
                            }
                        } else {
                            audioPlayer.apply {
                                try {
                                    this.stop()
                                    scope.apply {
                                        launch {
                                            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                            scaffoldState.snackbarHostState.showSnackbar("Stopped Playing Audio")
                                        }
                                    }
                                    playerClicked = !playerClicked
                                } catch (e: Exception) {
                                    Log.d("////////", e.toString())
                                }
                            }
                        }

                    },
                    modifier = AudioPlayModifier
                        .wrapContentSize()
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play Audio",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Button(
                    onClick = { imageDialog = true },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "View Image",
                        style = TextStyle(
                            fontStyle = MaterialTheme.typography.body1.fontStyle,
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp,
                        )
                    )
                }
                
                Text(text = "$audioLevels")

                Button(
                    onClick = {
                        if (pickedPhoto == null && (entryUiState.imageUri == null && entryUiState.imageUrl == "")) {
                            scope.apply {
                                launch {
                                    entryViewModel.onImageChangeUrl("https://cdn11.bigcommerce.com/s-3uewkq06zr/images/stencil/1280x1280/products/258/543/fluorescent_pink__88610.1492541080.png?c=2")
                                    entryViewModel.addEntryUrl(diaryId = diaryId)
                                }
                                launch {
                                    scaffoldState.snackbarHostState.showSnackbar("New Entry Created")
                                }
                                launch {
                                    entryViewModel.resetEntryAddedStatus()
                                }
                            }
                        } else if (pickedPhoto == null && (entryUiState.imageUri == null && entryUiState.imageUrl != "") ) {
                            scope.apply {
                                launch {
                                    entryViewModel.onImageChangeUrl(entryUiState.imageUrl)
                                    entryViewModel.updateEntry(entryId = entryId, diaryId = diaryId)
                                }
                                launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Entry Updated")
                                }
                                launch {
                                    entryViewModel.resetEntryAddedStatus()
                                }
                            }
                        } else if (pickedPhoto != null && (entryUiState.imageUri != null) ) {
                            scope.apply {
                                launch {
                                    entryViewModel.addEntry(diaryId = diaryId)
                                }
                                launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Entry Updated")
                                }
                                launch {
                                    entryViewModel.resetEntryAddedStatus()
                                }
                            }
                        } else {
                            scope.apply {
                                launch {
                                    scaffoldState.snackbarHostState.showSnackbar("ERROR 😭")
                                }
                                launch {
                                    entryViewModel.updateEntry(entryId = entryId, diaryId = diaryId)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .wrapContentSize()
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
}

@Preview(widthDp = 360, heightDp = 640)
@Composable
fun EntryScreenPreview() {
    JotDiaryTheme() {
        EntryScreen(entryViewModel = EntryViewmodel(), entryId = "", diaryId = "", navController = NavHostController(LocalContext.current)
        )
    }
}