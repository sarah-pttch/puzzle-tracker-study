package com.study.puzzletracker.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.study.puzzletracker.R
import com.study.puzzletracker.ui.collections.CollectionsScreen
import com.study.puzzletracker.ui.home.HomeScreen
import com.study.puzzletracker.ui.timetracker.TimeTrackerScreen

sealed class IconSource {
    data class Vector(val icon: ImageVector) : IconSource()
    data class Drawable(@DrawableRes val resId: Int) : IconSource()
}
sealed class Screen(val route: String, val label: String, val icon: IconSource) {
    object Home : Screen("home", "Home", IconSource.Vector(Icons.Default.Home))
    object Collections : Screen("collections", "Collections", IconSource.Drawable(R.drawable.outline_folder_24))
    object TimeTracker : Screen("timetracker", "Time Tracker", IconSource.Drawable(R.drawable.outline_timer_24))
}

val bottomNavItems = listOf(Screen.Home, Screen.Collections, Screen.TimeTracker)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            when (val src = screen.icon) {
                                is IconSource.Vector -> Icon(src.icon, contentDescription = screen.label)
                                is IconSource.Drawable -> Icon(painterResource(src.resId), contentDescription = screen.label)
                            }
                        },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Collections.route) { CollectionsScreen() }
            composable(Screen.TimeTracker.route) { TimeTrackerScreen() }
        }
    }
}