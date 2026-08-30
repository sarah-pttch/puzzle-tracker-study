package com.study.puzzletracker.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.data.remote.dto.PuzzleDto
import com.study.puzzletracker.ui.collections.CollectionsViewModel

@Composable
fun SearchScreen(collectionId: Long?, navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: SearchViewModel = viewModel(factory = AppViewModelFactory(app))

    val query by viewModel.query.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val results by viewModel.results.collectAsState()
    var showCollectionPicker by remember { mutableStateOf<PuzzleDto?>(null) }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            label = { Text("Search puzzles") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            query.length < 3 -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Type at least 3 characters to search",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            results.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No puzzles found for \"$query\"",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            else -> {
                LazyColumn {
                    items(results, key = { it.id }) { puzzle ->
                        ListItem(
                            headlineContent = { Text(puzzle.name) },
                            supportingContent = {Text("${puzzle.manufacturer} - ${puzzle.pieceCount} pcs") },
                            modifier = Modifier.clickable {
                                if (collectionId != null) {
                                    viewModel.addPuzzleToCollection(collectionId, puzzle)
                                    navController.popBackStack()
                                } else {
                                    showCollectionPicker = puzzle
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    showCollectionPicker?.let { puzzle ->
        CollectionPickerSheet(
            puzzleName = puzzle.name,
            onCollectionChosen = {chosenId ->
                viewModel.addPuzzleToCollection(chosenId, puzzle)
                showCollectionPicker = null
                navController.popBackStack()
            },
            onDismiss = { showCollectionPicker = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionPickerSheet(
    puzzleName: String,
    onCollectionChosen: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: CollectionsViewModel = viewModel(factory = AppViewModelFactory(app))

    val collections by viewModel.collections.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text("Add \"$puzzleName\" to:", modifier = Modifier.padding(16.dp))
        LazyColumn {
            items(collections, key = { it.id }) { collection ->
                ListItem(
                    headlineContent = { Text(collection.name) },
                    modifier = Modifier.clickable { onCollectionChosen(collection.id) }
                )
            }
        }
    }
}