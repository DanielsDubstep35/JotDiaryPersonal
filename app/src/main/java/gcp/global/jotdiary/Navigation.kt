package gcp.global.jotdiary

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gcp.global.jotdiary.controller.*
import gcp.global.jotdiary.view.screens.*

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    entryViewModel: EntryViewmodel,
    homeViewModel: HomeViewModel,
    diaryViewmodel: DiaryViewmodel,
    diariesViewmodel: DiariesViewmodel,
) {
    NavHost(
        navController = navController,
        startDestination = "main"
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
        route = "login"
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
        route = "main",
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
                    navController.navigate("diary") // this triggers route = "diary?id={id}" without any id. The id is created in firebase when the diary is saved, and it is assigned in the StorageRepository (diaryId = diariesRef.document().id)
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


// all routes above are defined here
// login -> login ->
// login -> signup ->
// main -> home ->
// main -> entry ->