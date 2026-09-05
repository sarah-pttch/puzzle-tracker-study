package com.study.puzzletracker.ui.puzzledetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.data.local.entity.AttemptMode
import com.study.puzzletracker.data.local.entity.Difficulty
import com.study.puzzletracker.navigation.Screen
import com.study.puzzletracker.ui.statistics.CompletionTimeChart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleDetailsScreen(puzzleId: String, navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: PuzzleDetailsViewModel = viewModel(factory = AppViewModelFactory(app, puzzleId = puzzleId))
    val scope = rememberCoroutineScope()

    val puzzleCache by viewModel.puzzleCache.collectAsState()
    val details by viewModel.details.collectAsState()
    val completionTimes by viewModel.completionTimes.collectAsState()
    var difficulty by remember(details) { mutableStateOf(details?.difficulty) }
    var tags by remember(details) { mutableStateOf(details?.tags ?: "") }
    var notes by remember(details) { mutableStateOf(details?.notes ?: "") }
    var showAddAttemptDialog by remember {mutableStateOf(false)}

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(puzzleCache?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to collection"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "${puzzleCache?.manufacturer} - ${puzzleCache?.pieceCount} pcs",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(24.dp))

            Text("Difficulty", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow {
                Difficulty.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = difficulty == option,
                        onClick = { difficulty = option },
                        shape = SegmentedButtonDefaults.itemShape(index, Difficulty.entries.size)
                    ) {
                        Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveDetails(difficulty, tags.ifBlank { null }, notes.ifBlank { null }) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        val attemptId = viewModel.createAttempt(puzzleId)
                        navController.navigate(Screen.AttemptTracking.createRoute(attemptId))
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start attempt")
            }
            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showAddAttemptDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add attempt manually")
            }
            Spacer(Modifier.height(24.dp))

            Text("Completion times", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            CompletionTimeChart(completionSeconds = completionTimes)
        }

        if (showAddAttemptDialog) {
            AddAttemptDialog(
                onDismiss = { showAddAttemptDialog = false },
                onConfirm = { startedAt, completionTimeSeconds, mode, numberOfPuzzlers ->
                    viewModel.addAttemptManually(startedAt, completionTimeSeconds, mode, numberOfPuzzlers)
                    showAddAttemptDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAttemptDialog(
    onDismiss: () -> Unit,
    onConfirm: (startedAt: Long, completionTimeSeconds: Long, mode: AttemptMode?, numberOfPuzzlers: Int?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var hours by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("") }
    var seconds by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf<AttemptMode?>(null) }
    var numberOfPuzzlers by remember { mutableStateOf<Int?>(null) }

    val dateLabel = datePickerState.selectedDateMillis?.let {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
    } ?: "Select date"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add attempt manually") },
        text = {
            Column {
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(dateLabel)
                }
                Spacer(Modifier.height(16.dp))

                Text("Completion time", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = hours,
                        onValueChange = { hours = it },
                        label = { Text("h") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = { minutes = it },
                        label = { Text("m") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = seconds,
                        onValueChange = { seconds = it },
                        label = { Text("s") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(16.dp))

                Text("Mode", style = MaterialTheme.typography.titleSmall)
                SingleChoiceSegmentedButtonRow {
                    AttemptMode.entries.forEachIndexed { index, option ->
                        SegmentedButton(
                            selected = mode == option,
                            onClick = { mode = option },
                            shape = SegmentedButtonDefaults.itemShape(index, AttemptMode.entries.size)
                        ) {
                            Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = numberOfPuzzlers?.toString() ?: "",
                    onValueChange = { numberOfPuzzlers = it.toIntOrNull() },
                    label = { Text("Number of puzzlers") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val totalSeconds = (hours.toLongOrNull() ?: 0) * 3600 +
                            (minutes.toLongOrNull() ?: 0) * 60 +
                            (seconds.toLongOrNull() ?: 0)
                    val startedAt = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    onConfirm(startedAt, totalSeconds, mode, numberOfPuzzlers)
                },
                enabled = totalSecondsValid(hours, minutes, seconds)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun totalSecondsValid(hours: String, minutes: String, seconds: String): Boolean {
    val total = (hours.toLongOrNull() ?: 0) + (minutes.toLongOrNull() ?: 0) + (seconds.toLongOrNull() ?: 0)
    return total > 0
}