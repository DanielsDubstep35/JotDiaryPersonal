package gcp.global.jotdiary

import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import gcp.global.jotdiary.controller.EntryViewmodel
import gcp.global.jotdiary.controller.HomeViewModel
import gcp.global.jotdiary.controller.LoginViewModel
import gcp.global.jotdiary.view.screens.EntryScreen
import gcp.global.jotdiary.view.screens.Home
import gcp.global.jotdiary.view.screens.LoginScreen
import gcp.global.jotdiary.view.screens.SignUpScreen


enum class LoginRoutes {
    Signup,
    SignIn
}

enum class HomeRoutes {
    Home,
    Entry
}

enum class NestedRoutes {
    Main,
    Login
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    entryViewModel: EntryViewmodel,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = NestedRoutes.Main.name
    ) {
        authGraph(navController, loginViewModel)
        homeGraph(
            navController = navController,
            entryViewModel,
            homeViewModel
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
) {
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ) {
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    launchSingleTop = true
                    popUpTo(route = LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.Signup.name) {
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            }
        }
        composable(route = LoginRoutes.Signup.name) {
            SignUpScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    popUpTo(LoginRoutes.Signup.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel
            ) {
                navController.navigate(LoginRoutes.SignIn.name)
            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    entryViewModel: EntryViewmodel,
    homeViewModel: HomeViewModel
){
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name,
    ){
        composable(HomeRoutes.Home.name){
            Home(
                homeViewModel = homeViewModel,
                onEntryClick = { entryId ->
                    navController.navigate(
                        HomeRoutes.Entry.name + "?id=$entryId"
                    ){
                        launchSingleTop = true
                    }
                },
                navToEntryPage = {
                    navController.navigate(HomeRoutes.Entry.name)
                }
            ) {
                navController.navigate(NestedRoutes.Login.name){
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
        }
        composable(
            route = HomeRoutes.Entry.name + "?id={id}",
            arguments = listOf(navArgument("id"){
                type = NavType.StringType
                defaultValue = ""
            })
        ){ entry ->
            EntryScreen(
                entryViewModel = entryViewModel,
                entryId = entry.arguments?.getString("id") as String,
            ) {
                navController.navigateUp()
            }
        }
    }
}






















