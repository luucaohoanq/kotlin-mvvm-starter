package com.example.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvvm.ui.screen.changelanguage.ChangeLanguageScreen
import com.example.mvvm.ui.screen.detail.DetailScreen
import com.example.mvvm.ui.screen.home.HomeScreen
import com.example.mvvm.ui.screen.home.HomeViewModel
import com.example.mvvm.utils.CurrencyManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface CurrencyManagerEntryPoint {
    fun currencyManager(): CurrencyManager
}

sealed class Screen(val route: String) {  //enum
    object Home : Screen("home")
    object Detail : Screen("detail")

    object ChangeLanguage : Screen("changeLanguage")
}

//https://developer.android.com/topic/architecture
//https://developer.android.com/topic/libraries/architecture/viewmodel
//https://developer.android.com/training/dependency-injection
//https://developer.android.com/develop/ui/compose/libraries#hilt
//https://github.com/android/architecture-samples

@Composable
fun Navigation() {
    val navController = rememberNavController()
    //val viewModel = hiltViewModel<MainViewModel>()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController, hiltViewModel())
        }
        composable(Screen.Detail.route) {
            val parentEntry = remember(it) {
                navController.getBackStackEntry(Screen.Home.route)
            }
            val homeViewModel = hiltViewModel<HomeViewModel>(parentEntry)
            DetailScreen(navController, hiltViewModel())
        }

        composable(Screen.ChangeLanguage.route) {
            // Get CurrencyManager from the calling activity through the NavBackStackEntry
            val activity = LocalContext.current as? MainActivity
            val currencyManager = activity?.currencyManager
            if (currencyManager != null) {
                ChangeLanguageScreen(navController, currencyManager)
            } else {
                // Fallback - try to get from Hilt container directly
                val fallbackManager: CurrencyManager = EntryPointAccessors.fromApplication(
                    LocalContext.current.applicationContext,
                    CurrencyManagerEntryPoint::class.java
                ).currencyManager()
                ChangeLanguageScreen(navController, fallbackManager)
            }
        }

    }
}