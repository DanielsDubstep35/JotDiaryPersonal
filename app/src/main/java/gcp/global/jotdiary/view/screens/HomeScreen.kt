package gcp.global.jotdiary.view.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import gcp.global.jotdiary.controller.HomeUiState
import gcp.global.jotdiary.controller.HomeViewModel
import gcp.global.jotdiary.model.models.Entries
import gcp.global.jotdiary.model.repository.Resources

@Composable
fun Home(
    homeViewModel: HomeViewModel?,
    onEntryClick: (id: String) -> Unit,
    navToEntryPage: () -> Unit,
    navToLoginPage: () -> Unit,
) {
    val homeUiState = homeViewModel?.homeUiState ?: HomeUiState()

    var openDialog by remember {
        mutableStateOf(false)
    }
    var selectedEntry: Entries? by remember {
        mutableStateOf(null)
    }

    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(key1 = Unit){
        homeViewModel?.loadEntries()
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = { navToEntryPage.invoke() }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = {},
                actions = {
                    IconButton(onClick = {
                        homeViewModel?.signOut()
                        navToLoginPage.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = null,
                        )
                    }
                },
                title = {
                    Text(text = "Home")
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            when (homeUiState.entriesList) {
                is Resources.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(align = Alignment.Center)
                    )
                }
                is Resources.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        items(
                            homeUiState.entriesList.data ?: emptyList()
                        ) { entry ->
                            EntryItem(
                                entries = entry,
                                onLongClick = {
                                    openDialog = true
                                    selectedEntry = entry
                                },
                            ) {
                                onEntryClick.invoke(entry.entryID)
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
                                        selectedEntry?.entryID?.let {
                                            homeViewModel?.deleteEntry(it)
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
                        text = homeUiState
                            .entriesList.throwable?.localizedMessage ?: "Unknown Error",
                        color = Color.Black
                    )

                    Log.d("HomeScreen", "Error: ${homeUiState.entriesList.throwable?.localizedMessage}")
                }
            }
        }
    }
    LaunchedEffect(key1 = homeViewModel?.hasUser){
        if (homeViewModel?.hasUser == false){
            navToLoginPage.invoke()
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
    Card(
        modifier = Modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke() },
                onClick = { onClick.invoke() }
            )
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = MaterialTheme.colors.onBackground,
    ) {
        Column {
            Text(
                text = entries.diaryTitle,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(4.dp)
            )
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = entries.entryName,
                    //style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp),
                    maxLines = 4
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            CompositionLocalProvider(
                LocalContentAlpha provides ContentAlpha.disabled
            ) {
                Text(
                    text = entries.entryDate,
                    //style = MaterialTheme.typography.body1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.End),
                    maxLines = 4
                )
            }
        }
    }
}