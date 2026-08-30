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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.study.puzzletracker.R
import com.study.puzzletracker.ui.collectiondetails.CollectionDetailsScreen
import com.study.puzzletracker.ui.collections.CollectionsScreen
import com.study.puzzletracker.ui.home.HomeScreen
import com.study.puzzletracker.ui.puzzledetails.PuzzleDetailsScreen
import com.study.puzzletracker.ui.search.SearchScreen
import com.study.puzzletracker.ui.timetracker.TimeTrackerScreen

sealed class IconSource {
    data class Vector(val icon: ImageVector) : IconSource()
    data class Drawable(@DrawableRes val resId: Int) : IconSource()
}
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Collections : Screen("collections")
    object TimeTracker : Screen("timeTracker")
    object CollectionDetail : Screen("collectionDetail/{collectionId}") {
        fun createRoute(collectionId: Long) = "collectionDetail/$collectionId"
    }
    object Search : Screen("search?collectionId={collectionId}") {
        fun createRoute(collectionId: Long? = null) =
            if (collectionId != null) "search?collectionId=$collectionId" else "search"
    }

    object PuzzleDetails : Screen("puzzleDetails/{puzzleId}") {
        fun createRoute(puzzleId: String) = "puzzleDetails/$puzzleId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: IconSource
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "Home", IconSource.Vector(Icons.Default.Home)),
    BottomNavItem(Screen.Collections, "Collections", IconSource.Drawable(R.drawable.outline_folder_24)),
    BottomNavItem(Screen.TimeTracker, "Time Tracker", IconSource.Drawable(R.drawable.outline_timer_24))
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavItems.forEach { navItem ->
                    NavigationBarItem(
                        icon = {
                            when (val src = navItem.icon) {
                                is IconSource.Vector -> Icon(src.icon, contentDescription = navItem.label)
                                is IconSource.Drawable -> Icon(painterResource(src.resId), contentDescription = navItem.label)
                            }
                        },
                        label = { Text(navItem.label) },
                        selected = currentRoute == navItem.screen.route,
                        onClick = {
                            navController.navigate(navItem.screen.route) {
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
            composable(Screen.Collections.route) { CollectionsScreen(navController) }
            composable(Screen.TimeTracker.route) { TimeTrackerScreen() }
            composable(
                route = Screen.Search.route,
                arguments = listOf(
                    navArgument("collectionId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val collectionId = backStackEntry.arguments?.getLong("collectionId")?.takeIf { it != -1L }
                SearchScreen(collectionId = collectionId, navController = navController)
            }
            composable(
                route = Screen.CollectionDetail.route,
                arguments = listOf(navArgument("collectionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val collectionId = backStackEntry.arguments?.getLong("collectionId") ?: return@composable
                CollectionDetailsScreen(collectionId = collectionId, navController = navController)
            }
            composable(
                route = Screen.PuzzleDetails.route,
                arguments = listOf(navArgument("puzzleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val puzzleId = backStackEntry.arguments?.getString("puzzleId") ?: return@composable
                PuzzleDetailsScreen(puzzleId = puzzleId)
            }
        }
    }
}