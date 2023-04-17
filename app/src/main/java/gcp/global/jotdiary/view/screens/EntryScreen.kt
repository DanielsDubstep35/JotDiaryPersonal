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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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
import gcp.global.jotdiary.view.components.audio.*
import gcp.global.jotdiary.view.components.bottomBars.MoodBottomBar
import gcp.global.jotdiary.viewmodel.EntryUiState
import gcp.global.jotdiary.viewmodel.EntryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

@OptIn(ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun EntryScreen(
    entryViewModel: EntryViewModel?,
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

    val saveOrUpdate = if (isEntryIdNotBlank) Icons.Filled.Update else Icons.Filled.Save

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
        entryViewModel?.onImageChange(pickedPhoto)
    } else {
        entryViewModel?.onImageChange(null)
    }

    if (pickedAudio != null) {
        entryViewModel?.onAudioChange(pickedAudio)
    } else {
        entryViewModel?.onAudioChange(null)
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
        topBar = { EntryNestedTopBar(currentScreen = currentScreen, entryViewmodel = entryViewModel, onBackPress = {

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
                            launch {
                                it.start(entryAudioFile)
                                //delay(400)
                            }.invokeOnCompletion { throwable ->
                                scope.launch {
                                    scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                                    scaffoldState.snackbarHostState.showSnackbar("Recording Started")
                                }
                                scope.launch {
                                    while (it.recording) {
                                        delay(500)
                                        audioLevels = it.getAudioLevels()
                                    }
                                }
                            }

                            Log.e("Entry Screen, Line 249", "prepare() failed")
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
                                .height(250.dp),
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
                            backgroundColor = MaterialTheme.colors.onSurface
                        ),
                        modifier = if (entryUiState.imageUrl != "" || entryUiState.imageUri != null) Modifier.alpha(1f) else Modifier
                            .alpha(0f)
                            .size(0.dp),
                        enabled = entryUiState.imageUrl != "" || entryUiState.imageUri != null
                    ) {
                        Text(
                            text = "Delete Image",
                            color = MaterialTheme.colors.surface
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            imageDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                    ) {
                        Text(
                            text = "Stop Viewing",
                            color = MaterialTheme.colors.onSurface
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
                scope.apply {
                    launch { entryViewModel?.resetEntryAddedStatus() }
                    launch {
                        navController.popBackStack()
                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                        scaffoldState.snackbarHostState.showSnackbar("Added Entry Successfully")
                    }
                }
            }

            if (entryUiState.updateEntryStatus) {
                scope.apply {
                    launch { entryViewModel?.resetEntryAddedStatus() }
                    launch {
                        navController.popBackStack()
                        scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                        scaffoldState.snackbarHostState.showSnackbar("Updated Entry Successfully")
                    }
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
                        .fillMaxSize(),
                    textStyle = TextStyle(
                        fontStyle = MaterialTheme.typography.body1.fontStyle,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 16.sp,
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
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
                                                scaffoldState.snackbarHostState.showSnackbar("Playing Audio from the Cloud")
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
                                                scaffoldState.snackbarHostState.showSnackbar("Playing Audio from the Device")
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
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .size(20.dp)
                            .background(color = MaterialTheme.colors.primary)
                    )
                }

                Button(
                    onClick = { imageDialog = true },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp)
                ) {
                    /*
                    Text(
                        text = "View Image",
                        style = TextStyle(
                            fontStyle = MaterialTheme.typography.body1.fontStyle,
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp,
                        )
                    )
                    */
                    Icon(
                        imageVector = Icons.Filled.Preview,
                        contentDescription = "View Image",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .size(20.dp)
                            .background(color = MaterialTheme.colors.primary)
                    )
                }
                
                Button(
                    onClick = {
                        scope.launch {
                            scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()
                            scaffoldState.snackbarHostState.showSnackbar("This Number Checks Audio Levels! 🤩🤩🤩")
                        }
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    //enabled = false,
                ) {
                    Text(
                        text = "$audioLevels",
                        style = TextStyle(
                            fontStyle = MaterialTheme.typography.body1.fontStyle,
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp,
                        ),
                        modifier = Modifier.size(20.dp).background(color = MaterialTheme.colors.primary),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = {
                        if (pickedPhoto == null && (entryUiState.imageUrl == "")) {
                            scope.apply {
                                launch {
                                    entryViewModel?.onImageChangeUrl("https://cdn11.bigcommerce.com/s-3uewkq06zr/images/stencil/1280x1280/products/258/543/fluorescent_pink__88610.1492541080.png?c=2")
                                    entryViewModel?.addEntryUrl(diaryId = diaryId)
                                    entryViewModel?.resetEntryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("New Entry Created")
                                }
                            }
                        } else if (pickedPhoto == null && (entryUiState.imageUrl != "") ) {
                            scope.apply {
                                launch {
                                    entryViewModel?.onImageChangeUrl(entryUiState.imageUrl)
                                    entryViewModel?.updateEntry(entryId = entryId, diaryId = diaryId)
                                    entryViewModel?.resetEntryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Entry Updated")
                                }
                            }
                        } else if (pickedPhoto != null && (entryUiState.imageUrl == "") ) {
                            scope.apply {
                                launch {
                                    entryViewModel?.onImageChange(pickedPhoto)
                                    entryViewModel?.addEntry(diaryId = diaryId)
                                    entryViewModel?.resetEntryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("New Entry Created")
                                }
                            }
                        } else if (pickedPhoto != null && (entryUiState.imageUrl != "") ) {
                            scope.apply {
                                launch {
                                    entryViewModel?.onImageChange(pickedPhoto)
                                    entryViewModel?.updateEntry(entryId = entryId, diaryId = diaryId)
                                    entryViewModel?.resetEntryAddedStatus()
                                    scaffoldState.snackbarHostState.showSnackbar("Entry Updated")
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp)
                ) {
                    /*
                    Text(
                        text = saveOrUpdate,
                        style = TextStyle(
                            fontStyle = MaterialTheme.typography.body1.fontStyle,
                            color = MaterialTheme.colors.onSurface,
                            fontSize = 16.sp,
                        )
                    )

                     */
                    Icon(
                        imageVector = saveOrUpdate,
                        contentDescription = "Save or Update Entry",
                        tint = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .size(20.dp)
                            .background(color = MaterialTheme.colors.primary)
                    )
                }
            }
        }
    }
}

/*
@Preview(widthDp = 360, heightDp = 640)
@Composable
fun EntryScreenPreview() {
    JotDiaryTheme() {
        EntryScreen(entryViewModel = EntryViewModel(), entryId = "", diaryId = "", navController = NavHostController(LocalContext.current)
        )
    }
}
*/
