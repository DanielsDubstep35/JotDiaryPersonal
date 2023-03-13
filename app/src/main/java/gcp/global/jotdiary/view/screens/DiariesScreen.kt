package gcp.global.jotdiary.view.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import gcp.global.jotdiary.R
import gcp.global.jotdiary.controller.*
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.Resources
import gcp.global.jotdiary.view.components.NestedTopBar

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun DiariesScreen(
    diariesViewmodel: DiariesViewmodel,
    onEntryClick: (entryId: String, diaryId: String) -> Unit,
    diaryId: String,
    navToEntryPage: () -> Unit,
    navController: NavHostController
) {
    val diariesUiState = diariesViewmodel.diariesUiState

    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedEntry: Entries? by remember {
        mutableStateOf(null)
    }

    // val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val previousScreen = "Home"
    val currentScreen = "Entries"

    LaunchedEffect(key1 = Unit){
        diariesViewmodel.loadEntries(diaryId = diaryId)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navToEntryPage.invoke() },
                backgroundColor = Color.Red,
                contentColor = MaterialTheme.colors.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
        topBar = {
            NestedTopBar(navController = navController, previousScreen = previousScreen, currentScreen = currentScreen)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (diariesUiState.entriesList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                is Resources.Success -> {
                    LazyHorizontalGrid(
                        rows = GridCells.Fixed(1),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        items(
                            diariesUiState.entriesList.data ?: emptyList()
                        ) { entry ->
                            EntryItem(
                                entries = entry,
                                onLongClick = {
                                    openDialog = true
                                    selectedEntry = entry
                                },
                            ) {
                                onEntryClick.invoke(entry.entryId, diaryId)
                            }
                        }
                    }
                    AnimatedVisibility(visible = openDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                openDialog = false
                            },
                            title = { Text(text = "Delete Diary Entry?") },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        selectedEntry?.entryId?.let {
                                            diariesViewmodel.deleteEntry(it, diaryId)
                                        }
                                        openDialog = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = Color.Red
                                    ),
                                ) {
                                    Text(text = "Delete")
                                }
                            },
                            dismissButton = {
                                Button(onClick = { openDialog = false }) {
                                    Text(text = "Cancel")
                                }
                            }
                        )
                    }
                }
                else -> {
                    Text(
                        text = diariesUiState
                            .entriesList.throwable?.localizedMessage ?: "Unknown Error",
                        color = Color.Black
                    )

                    Log.d("HomeScreen", "Error: ${diariesUiState.entriesList.throwable?.localizedMessage}")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EntryItem(
    entries: Entries,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .padding(8.dp)
            .fillMaxHeight()
            .wrapContentSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .height(450.dp)
                .width(300.dp)
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = MaterialTheme.shapes.small.copy(
                        all = CornerSize(0.dp)
                    )
                ),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = entries.entryName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colors.onSurface,
                    style = TextStyle(
                        fontStyle = MaterialTheme.typography.body1.fontStyle,
                        fontSize = 20.sp,
                    )
                )

                // ${entryUiState.date.toDate().date}/${entryUiState.date.toDate().month}/${entryUiState.date.toDate().year.plus(1900)}

                Text(
                    text = "${entries.entryDate.toDate().date}/${entries.entryDate.toDate().month}/${entries.entryDate.toDate().year.plus(1900)}",
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.padding(4.dp),
                    style = TextStyle(
                        fontStyle = MaterialTheme.typography.body1.fontStyle,
                        color = MaterialTheme.colors.onSurface,
                        fontSize = 12.sp,
                    )
                )
            }

            Divider(
                color = MaterialTheme.colors.onSurface,
                thickness = 2.dp,
                modifier = Modifier.padding(8.dp)
            )

            Text(
                text = entries.entryDescription,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colors.onSurface,
                style = TextStyle(
                    fontStyle = MaterialTheme.typography.body1.fontStyle,
                    fontSize = 16.sp,
                )
            )

        }

        Card(
            backgroundColor = MaterialTheme.colors.onSurface,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .wrapContentSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize()
            ) {
                Text(
                    text = "Mood:",
                    color = MaterialTheme.colors.primary,
                    fontFamily = MaterialTheme.typography.body1.fontFamily,
                    fontSize = 40.sp,
                    modifier = Modifier.padding(8.dp)
                )

                Image(painter = painterResource(
                    id = when (entries.entryMood) {
                        1 -> R.drawable.saddest
                        2 -> R.drawable.sadder
                        3 -> R.drawable.sad
                        5 -> R.drawable.happy
                        6 -> R.drawable.happier
                        7 -> R.drawable.happiest
                        else -> R.drawable.ok
                    }),
                    contentDescription = "Mood"
                )
            }
        }
    }
}