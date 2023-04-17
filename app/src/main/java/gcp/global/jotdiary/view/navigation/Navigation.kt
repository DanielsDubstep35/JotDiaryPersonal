package gcp.global.jotdiary.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gcp.global.jotdiary.view.screens.*
import gcp.global.jotdiary.viewmodel.*

@Composable
fun Navigation(
    loginViewModel: LoginViewModel,
    entryViewModel: EntryViewModel,
    homeViewModel: HomeViewModel,
    diaryViewModel: DiaryViewModel,
    diariesViewModel: DiariesViewModel,
    settingsViewModel: SettingsViewModel,
    calenderViewModel: CalenderViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.login.name
    ) {
        authGraph(navController, loginViewModel, settingsViewModel)
        homeGraph(
            navController = navController,
            entryViewModel,
            homeViewModel,
            diaryViewModel,
            diariesViewModel,
            settingsViewModel,
            calenderViewModel
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    settingsViewmodel: SettingsViewModel
) {
    navigation(
        startDestination = "signin",
        route = Route.login.name
    ) {
        composable(route = "signin") {
            LoginScreen(
                onNavToHomePage = {
                    navController.navigate("main") {
                        launchSingleTop = true
                        popUpTo(route = "signin") {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel,
                onNavToSignUpPage = {
                    navController.navigate("signup") {
                        launchSingleTop = true
                        popUpTo("signin") {
                            inclusive = true
                        }
                    }
                },
                preferences = settingsViewmodel
            )
        }
        composable(route = "signup") {
            SignUpScreen(
                onNavToHomePage = {
                    navController.navigate("main") {
                        popUpTo("signup") {
                            inclusive = true
                        }
                    }
                },
                loginViewModel = loginViewModel,
                onNavToLoginPage = {
                    navController.navigate("signin")
                }
            )
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    entryViewModel: EntryViewModel,
    homeViewModel: HomeViewModel,
    diaryViewmodel: DiaryViewModel,
    diariesViewmodel: DiariesViewModel,
    settingsViewmodel: SettingsViewModel,
    calenderViewModel: CalenderViewModel
) {
    navigation(
        startDestination = "home",
        route = Route.main.name
    ) {
        composable("home") {
            Home(
                homeViewModel = homeViewModel,
                onDiaryClick = { diaryId ->
                    navController.navigate(
                        "diary?id=$diaryId"
                    ) {
                        launchSingleTop = true
                    }
                },
                onNavToDiaryPage = {
                    navController.navigate("diary")
                },
                onNavToDiaryEditPage = { diaryId ->
                    navController.navigate(
                        "diaryEdit?id=$diaryId"
                    ) {
                        launchSingleTop = true
                    }
                },
                onNavToSettingsPage = {
                    navController.navigate("settings")
                },
                onNavToLoginPage = {
                    navController.navigate("signin") {
                        launchSingleTop = true
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onNavToCalenderPage = {
                    navController.navigate("calender")
                },
            )
        }
        composable(
            route = "diaryEdit?id={diaryId}",
            arguments = listOf(navArgument("diaryId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { diary ->
            DiaryScreen(
                diaryViewmodel = diaryViewmodel,
                diaryId = diary.arguments?.getString("diaryId") as String,
                navController = navController
            )
        }
        composable(
            route = "diary?id={diaryId}",
            arguments = listOf(navArgument("diaryId") {
                type = NavType.StringType
                defaultValue = ""
            })
        ) { diary ->
            if ((diary.arguments?.getString("diaryId"))?.isNotEmpty() == true) {
                DiariesScreen(
                    diariesViewmodel = diariesViewmodel,
                    onEntryClick = { entryId, diaryId ->
                        navController.navigate(
                            "entry?id=$diaryId/$entryId"
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onNavToEntryPage = {
                        navController.navigate(
                            route = "entry?id=${diary.arguments?.getString("diaryId")}/"
                        )
                    },
                    diaryId = diary.arguments?.getString("diaryId") as String,
                    navController = navController
                )
            } else {
                DiaryScreen(
                    navController = navController,
                    diaryViewmodel = diaryViewmodel,
                    diaryId = diary.arguments?.getString("diaryId") as String
                )
            }
        }
        composable(
            route = "entry?id={diaryId}/{entryId}",
            arguments = listOf(
                navArgument("entryId") {
                type = NavType.StringType
                defaultValue = ""
                },

                navArgument("diaryId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            EntryScreen(
                entryViewModel = entryViewModel,
                entryId = entry.arguments?.getString("entryId") as String,
                diaryId = entry.arguments?.getString("diaryId") as String,
                navController = navController
            )
        }
        composable(
            route = "settings",
        ) {
            SettingsScreen(
                settingsViewmodel = settingsViewmodel,
                onNavToCalenderPage = {
                    navController.navigate("calender")
                }
            ) {
                navController.navigate("home")
            }
        }
        composable(
            route = "calender",
        ) {
            CalenderScreen(
                calenderViewModel = calenderViewModel,
                onNavToHomePage = {
                    navController.navigate("home")
                },
                onNavToSettingsPage = {
                    navController.navigate("settings")
                },
                onDiaryClick = { diaryId ->
                    navController.navigate(
                        "diary?id=$diaryId"
                    ) {
                        launchSingleTop = true
                    }
                },
                onNavToDiaryEditPage = { diaryId ->
                    navController.navigate(
                        "diaryEdit?id=$diaryId"
                    ) {
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}