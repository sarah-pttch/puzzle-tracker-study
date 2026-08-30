package com.study.puzzletracker.ui.collections

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.study.puzzletracker.navigation.Screen

@Composable
fun CollectionsScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: CollectionsViewModel = viewModel(factory = AppViewModelFactory(app))
    val collections by viewModel.collections.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create collection")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (collections.isEmpty()) {
                EmptyCollectionsPlaceholder()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(collections, key = { it.id }) { collection ->
                        ListItem(
                            headlineContent = { Text(collection.name) },
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.CollectionDetail.createRoute(collection.id))
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        if (showCreateDialog) {
            CreateCollectionDialog(
                onDismiss = { showCreateDialog = false },
                onConfirm = { name ->
                    viewModel.createCollection(name)
                    showCreateDialog = false
                }
            )
        }
    }
}

@Composable
private fun EmptyCollectionsPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("No collections created yet", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Tap the + button to create your first collection.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CreateCollectionDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New collection") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }, enabled = name.isNotBlank()) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}