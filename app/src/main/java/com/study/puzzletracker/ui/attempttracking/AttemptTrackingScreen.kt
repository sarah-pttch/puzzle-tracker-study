package com.study.puzzletracker.ui.attempttracking

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
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
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.R
import com.study.puzzletracker.data.local.entity.AttemptMode
import com.study.puzzletracker.data.local.entity.AttemptStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttemptTrackingScreen(attemptId: Long, navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: AttemptTrackingViewModel = viewModel(factory = AppViewModelFactory(app, attemptId = attemptId))
    val scope = rememberCoroutineScope()

    val attemptWithPuzzle by viewModel.attempt.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val attempt = attemptWithPuzzle?.attempt
    var numberOfPuzzlers by remember(attempt) { mutableStateOf(attempt?.numberOfPuzzlers) }
    var mode by remember(attempt) { mutableStateOf(attempt?.mode) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(attemptWithPuzzle?.puzzleName ?: "") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val displaySeconds = if (attempt?.status == AttemptStatus.FINISHED) {
                attempt.completionTimeSeconds
            } else elapsedSeconds

            Text(
                text = formatDuration(displaySeconds),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(24.dp))

            when {
                attempt?.status == AttemptStatus.IN_PROGRESS && attempt.runningSince != null -> {
                    FilledIconButton(onClick = { viewModel.pause() }, modifier = Modifier.size(144.dp)) {
                        Icon(painterResource(
                            R.drawable.baseline_pause_24),
                            contentDescription = "Pause/Stop",
                            modifier = Modifier.size(72.dp)
                        )
                    }
                }
                attempt?.status == AttemptStatus.IN_PROGRESS && attempt.runningSince == null -> {
                    FilledIconButton(onClick = { viewModel.resume() }, modifier = Modifier.size(144.dp)) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Resume",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                else -> {}
            }
            Spacer(Modifier.height(48.dp))

            Text("Mode", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow {
                AttemptMode.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = mode == option,
                        onClick = {
                            mode = option
                            viewModel.editDetails(option, numberOfPuzzlers)
                        },
                        shape = SegmentedButtonDefaults.itemShape(index, AttemptMode.entries.size)
                    ) {
                        Text(option.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = numberOfPuzzlers?.toString() ?: "",
                onValueChange = {
                    val value = it.toIntOrNull()
                    numberOfPuzzlers = value
                    viewModel.editDetails(mode, value)
                },
                label = { Text("Number of puzzlers") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(32.dp))

            when (attempt?.status) {
                AttemptStatus.IN_PROGRESS -> {
                    Button(onClick = { viewModel.finish() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Finish")
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                viewModel.deleteAttempt()
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Delete attempt")
                    }
                }

                AttemptStatus.FINISHED -> {
                    OutlinedButton(onClick = { viewModel.reopen() }, modifier = Modifier.fillMaxWidth()) {
                        Text("Reopen")
                    }
                }
                null -> {}
            }
        }
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}