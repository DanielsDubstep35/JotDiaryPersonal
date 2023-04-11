package gcp.global.jotdiary

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gcp.global.jotdiary.view.navigation.Route
import gcp.global.jotdiary.view.screens.*
import gcp.global.jotdiary.viewmodel.*

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    entryViewModel: EntryViewmodel,
    homeViewModel: HomeViewModel,
    diaryViewmodel: DiaryViewmodel,
    diariesViewmodel: DiariesViewmodel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.login.name
    ) {
        authGraph(navController, loginViewModel)
        homeGraph(
            navController = navController,
            entryViewModel,
            homeViewModel,
            diaryViewmodel,
            diariesViewmodel
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
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
            ) {
                navController.navigate("signup") {
                    launchSingleTop = true
                    popUpTo("signin") {
                        inclusive = true
                    }
                }
            }
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
                loginViewModel = loginViewModel
            ) {
                navController.navigate("signin")
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    entryViewModel: EntryViewmodel,
    homeViewModel: HomeViewModel,
    diaryViewmodel: DiaryViewmodel,
    diariesViewmodel: DiariesViewmodel
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
                navToDiaryPage = {
                    navController.navigate("diary")
                },
                navToDiaryEditPage = { diaryId ->
                    navController.navigate(
                        "diaryEdit?id=$diaryId"
                    ) {
                        launchSingleTop = true
                    }
                },
            ) {
                navController.navigate("signin") {
                    launchSingleTop = true
                    popUpTo(0) {
                        inclusive = true
                    }
                }
            }
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
                    navToEntryPage = {
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
    }
}