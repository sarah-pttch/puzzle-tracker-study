package com.study.puzzletracker.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.data.remote.dto.PuzzleDto
import com.study.puzzletracker.ui.collections.CollectionsViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(collectionId: Long?, navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: SearchViewModel = viewModel(factory = AppViewModelFactory(app))
    val scope = rememberCoroutineScope()

    val query by viewModel.query.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val results by viewModel.results.collectAsState()
    var showCollectionPicker by remember { mutableStateOf<PuzzleDto?>(null) }
    var showNewPuzzleDialog by remember { mutableStateOf(false) }

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
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No puzzles found for \"$query\"",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedButton(onClick = { showNewPuzzleDialog = true }) {
                        Text("Add as a new puzzle")
                    }
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

    if (showNewPuzzleDialog) {
        NewPuzzleDialog(
            onDismiss = { showNewPuzzleDialog = false },
            onConfirm = { name, barcode, pieceCount, manufacturer, publicationYear, toGlobalCollection ->
                scope.launch {
                    val createdPuzzle = viewModel.createPuzzle(name, barcode, pieceCount, manufacturer, publicationYear, toGlobalCollection)
                    showNewPuzzleDialog = false
                    if (collectionId != null) {
                        viewModel.addPuzzleToCollection(collectionId, createdPuzzle)
                        navController.popBackStack()
                    } else {
                        showCollectionPicker = createdPuzzle
                    }
                }
                 }
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

@Composable
private fun NewPuzzleDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, barcode: String?, pieceCount: Int, manufacturer: String, publicationYear: Int?, toGlobalCollection: Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var pieceCount by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var publicationYear by remember { mutableStateOf<Int?>(null) }
    var toGlobalCollection by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add new puzzle") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Puzzle name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("Barcode") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = pieceCount,
                    onValueChange = { pieceCount = it },
                    label = { Text("Piece count") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = manufacturer,
                    onValueChange = { manufacturer = it },
                    label = { Text("Brand") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = publicationYear?.toString() ?: "",
                    onValueChange = { publicationYear = it.toIntOrNull() },
                    label = { Text("Publication year") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth()
                        .toggleable(
                            value = toGlobalCollection,
                            onValueChange = {toGlobalCollection = it },
                            role = Role.Checkbox,
                            indication = null,
                            interactionSource = interactionSource
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = toGlobalCollection,
                        interactionSource = interactionSource,
                        onCheckedChange = { toGlobalCollection = it }
                    )
                    Text(
                        "Create entry in global puzzle collection for other puzzlers to use",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, barcode, pieceCount.toInt(), manufacturer, publicationYear, toGlobalCollection) },
                enabled = name.isNotBlank() && pieceCount.isNotBlank() && manufacturer.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}