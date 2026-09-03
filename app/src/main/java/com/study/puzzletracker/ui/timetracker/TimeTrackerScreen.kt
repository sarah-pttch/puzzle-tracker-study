package com.study.puzzletracker.ui.timetracker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.data.local.entity.PuzzleCacheEntity
import com.study.puzzletracker.navigation.Screen
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TimeTrackerScreen(navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: TimeTrackerViewModel = viewModel(factory = AppViewModelFactory(app))
    val scope = rememberCoroutineScope()

    val inProgressAttempts by viewModel.inProgressAttempts.collectAsState()
    val selectablePuzzles by viewModel.selectablePuzzles.collectAsState()
    var showPuzzlePicker by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {showPuzzlePicker = true }) {
                Icon(Icons.Default.Add, contentDescription = "Start new attempt")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (inProgressAttempts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No attempts currently in progress", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Tap the + button to start tracking a new attempt.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(inProgressAttempts, key = { it.attempt.id }) { item ->
                        ListItem(
                            headlineContent = { Text(item.puzzleName) },
                            supportingContent = {
                                Text("${item.puzzleManufacturer} - ${item.puzzlePieceCount} pcs")
                            },
                            trailingContent = {
                                Text("Started: ${formatStartedAt(item.attempt.startedAt)}")
                            },
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.AttemptTracking.createRoute(item.attempt.id))
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }

        if (showPuzzlePicker) {
            PuzzlePickerSheet(
                puzzles = selectablePuzzles,
                onPuzzleChosen = { puzzleId ->
                    scope.launch {
                        val attemptId = viewModel.createAttempt(puzzleId)
                        showPuzzlePicker = false
                        navController.navigate(Screen.AttemptTracking.createRoute(attemptId))
                    }
                },
                onDismiss = { showPuzzlePicker = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzlePickerSheet(
    puzzles: List<PuzzleCacheEntity>,
    onPuzzleChosen: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(
            "Start an attempt for:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        if (puzzles.isEmpty()) {
            Text(
                "No puzzles available yet for tracking - add one to a collection first.",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(puzzles, key = { it.id }) { puzzle ->
                    ListItem(
                        headlineContent = { Text(puzzle.name) },
                        supportingContent = {
                            Text("${puzzle.manufacturer} - ${puzzle.pieceCount} pcs")
                        },
                        modifier = Modifier.clickable { onPuzzleChosen(puzzle.id) }
                    )
                }
            }
        }
    }
}

fun formatStartedAt(startedAtMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    return Instant.ofEpochMilli(startedAtMillis)
        .atZone(ZoneId.systemDefault())
        .format(formatter)
}