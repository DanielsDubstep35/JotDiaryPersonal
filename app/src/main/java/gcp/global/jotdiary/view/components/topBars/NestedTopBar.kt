package gcp.global.jotdiary.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import gcp.global.jotdiary.viewmodel.CalenderViewModel
import gcp.global.jotdiary.viewmodel.DiaryViewModel
import gcp.global.jotdiary.viewmodel.EntryViewModel
import gcp.global.jotdiary.viewmodel.SearchBarState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NestedTopBar(
    navController: NavController,
    currentScreen: String,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
    )
}

@Composable
fun GeneralTopBar(
    currentScreen: String,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
    )
}

@Composable
fun DiaryNestedTopBar(
    navController: NavController,
    currentScreen: String,
    diaryViewmodel: DiaryViewModel?
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
                diaryViewmodel?.resetState()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
    )
}

@Composable
fun EntryNestedTopBar(
    onImageAdd: () -> Unit,
    onAudioAdd: () -> Unit,
    onBackPress: () -> Unit,
    currentScreen: String,
    entryViewmodel: EntryViewModel?
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        title = {
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                onBackPress()
                entryViewmodel?.resetState()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = {
                onAudioAdd()
            }) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = "Add Audio",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = {
                onImageAdd()
            }) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Add Image",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

@Composable
fun CalenderScreenTopBar(
    currentScreen: String,
    search: () -> Unit,
    calender: () -> Unit,
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSurface,
        modifier = Modifier
            .background(MaterialTheme.colors.primary)
            .padding(vertical = 8.dp),
        navigationIcon = {
            IconButton(onClick = {
                calender()
            }) {
                Icon(
                    imageVector = Icons.Filled.PermContactCalendar,
                    contentDescription = "Calender Icon",
                    tint = MaterialTheme.colors.primary
                )
            }
        },
        title = {
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                BasicText(
                    text = currentScreen,
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        color = MaterialTheme.colors.primary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        actions = {
            IconButton(onClick = {
                search()
            }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colors.primary,
                )
            }
        }
    )
}

@Composable
fun SearchQueryTopBar(
    calenderViewModel: CalenderViewModel?,
) {

    val searchIcon = if (calenderViewModel?.searchQuery!!.isNotEmpty()) {
        Icons.Filled.Clear
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    val scope = rememberCoroutineScope()

    // focus requester
    val focusRequester = FocusRequester()

    TopAppBar(
        actions = {
            IconButton(onClick = {
                if (calenderViewModel.searchQuery.isNotEmpty()) {
                    calenderViewModel.onSearchQueryChange("")
                } else {
                    calenderViewModel.onSearchBarChange(SearchBarState.Closed)
                }
            }) {
                Icon(
                    imageVector = searchIcon,
                    contentDescription = "Clear Query or Close Bar",
                    tint = MaterialTheme.colors.onSurface,
                )
            }
        },
        title = {
            TextField(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "SearchButton"
                    )
                },
                value = calenderViewModel.searchQuery,
                onValueChange = {
                    scope.apply {
                        launch {
                            calenderViewModel.onSearchQueryChange(it)
                            delay(500)
                            calenderViewModel.onSearchQuery()
                        }
                    }
                                },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(100),
                placeholder = {
                    Text(
                        text = "Search",
                        style = TextStyle(
                            fontFamily = MaterialTheme.typography.body1.fontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.onSurface
                        )
                    )
                },
                textStyle = TextStyle(
                    fontFamily = MaterialTheme.typography.body1.fontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colors.onSurface
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        calenderViewModel.onSearchQuery()
                    }
                ),
            )
        },
        modifier = Modifier
            .padding(vertical = 8.dp),
    )

    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
}