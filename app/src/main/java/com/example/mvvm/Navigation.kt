package com.example.mvvm

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mvvm.components.navigation.MainBottomNavigation
import com.example.mvvm.preview.PlaceholderScreen
import com.example.mvvm.ui.screen.cameras.CamerasScreen
import com.example.mvvm.ui.screen.cameradetail.CameraDetailScreen
import com.example.mvvm.ui.screen.changelanguage.ChangeLanguageScreen
import com.example.mvvm.ui.screen.favorites.FavoritesScreen
import com.example.mvvm.ui.screen.settings.SettingsScreen
import com.example.mvvm.utils.CurrencyManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

sealed class Screen(val route: String) {  //enum
    object Cameras : Screen("cameras")
    object CameraDetail : Screen("camera_detail/{cameraId}") {
        fun createRoute(cameraId: Int) = "camera_detail/$cameraId"
    }
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
    object ChangeLanguage : Screen("changeLanguage")

    object Profile:  Screen("my_profile")

    // Legacy routes for backward compatibility
    object Home : Screen("home")
}

//https://developer.android.com/topic/architecture
//https://developer.android.com/topic/libraries/architecture/viewmodel
//https://developer.android.com/training/dependency-injection
//https://developer.android.com/develop/ui/compose/libraries#hilt
//https://github.com/android/architecture-samples

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MainCurrencyManagerEntryPoint {
    fun currencyManager(): CurrencyManager
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Routes that should show bottom navigation
    val bottomNavRoutes = setOf(
        Screen.Cameras.route,
        Screen.Favorites.route,
        Screen.Settings.route
    )
    
    val shouldShowBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (shouldShowBottomNav) {
                MainBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            // Pop up to the start destination to avoid building up a large stack
                            popUpTo(Screen.Cameras.route) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Cameras.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main screens with bottom navigation
            composable(Screen.Cameras.route) {
                val activity = context as? MainActivity
                val currencyManager = activity?.currencyManager
                
                CamerasScreen(
                    onCameraClick = { camera ->
                        navController.navigate(Screen.CameraDetail.createRoute(camera.id))
                    },
                    currencyManager = currencyManager
                )
            }
            
            composable(Screen.Favorites.route) {
                FavoritesScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onLanguageClick = {
                        navController.navigate(Screen.ChangeLanguage.route)
                    }
                )
            }
            
            // Detail screens without bottom navigation
            composable(
                route = Screen.CameraDetail.route,
                arguments = listOf(navArgument("cameraId") { type = NavType.IntType })
            ) { backStackEntry ->
                val cameraId = backStackEntry.arguments?.getInt("cameraId") ?: 0
                val activity = context as? MainActivity
                val currencyManager = activity?.currencyManager
                
                CameraDetailScreen(
                    cameraId = cameraId,
                    onBackClick = { navController.popBackStack() },
                    currencyManager = currencyManager
                )
            }
            
            composable(Screen.ChangeLanguage.route) {
                val activity = LocalContext.current as? MainActivity
                val currencyManager = activity?.currencyManager
                if (currencyManager != null) {
                    ChangeLanguageScreen(navController, currencyManager)
                } else {
                    // Fallback - try to get from Hilt container directly
                    val fallbackManager: CurrencyManager = EntryPointAccessors.fromApplication(
                        LocalContext.current.applicationContext,
                        MainCurrencyManagerEntryPoint::class.java
                    ).currencyManager()
                    ChangeLanguageScreen(navController, fallbackManager)
                }
            }

            composable(Screen.Profile.route){
                PlaceholderScreen(navController)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen()
    }
}
