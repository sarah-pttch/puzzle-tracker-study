package com.study.puzzletracker.ui.collectiondetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(collectionId: Long, navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: CollectionDetailsViewModel = viewModel(factory = AppViewModelFactory(app, collectionId = collectionId))

    val collection by viewModel.collection.collectAsState()
    val puzzles by viewModel.puzzles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(collection?.name ?: "") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.Search.createRoute(collectionId = collectionId))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add puzzle")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (puzzles.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No puzzles in collection yet", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap the + button to add a puzzle to this collection.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(puzzles, key = { it.id }) { puzzle ->
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