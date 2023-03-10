package gcp.global.jotdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import gcp.global.jotdiary.controller.EntryViewmodel
import gcp.global.jotdiary.controller.HomeViewModel
import gcp.global.jotdiary.controller.LoginViewModel
import gcp.global.jotdiary.view.theme.JotDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val loginViewModel = viewModel(modelClass = LoginViewModel::class.java)
            val homeViewModel = viewModel(modelClass = HomeViewModel::class.java)
            val entryViewModel = viewModel(modelClass = EntryViewmodel::class.java)
            JotDiaryTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    Navigation(
                        loginViewModel = loginViewModel,
                        entryViewModel = entryViewModel,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }
}
