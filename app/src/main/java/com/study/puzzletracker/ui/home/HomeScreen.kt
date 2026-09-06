package com.study.puzzletracker.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: HomeViewModel = viewModel(factory = AppViewModelFactory(app))

    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Search your puzzles") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        if (query.isNotBlank()) {
            if (results.isEmpty()) {
                Text("No puzzles found for \"$query\"", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn {
                    items(results, key = { it.id }) { puzzle ->
                        ListItem(
                            headlineContent = { Text(puzzle.name) },
                            supportingContent = {
                                Text("${puzzle.manufacturer} - ${puzzle.pieceCount} pcs")
                            },
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.PuzzleDetails.createRoute(puzzle.id))
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}