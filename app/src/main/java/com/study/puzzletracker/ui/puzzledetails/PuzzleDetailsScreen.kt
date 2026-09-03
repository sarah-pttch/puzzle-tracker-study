package com.study.puzzletracker.ui.puzzledetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp
import com.study.puzzletracker.data.local.entity.Difficulty
import com.study.puzzletracker.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleDetailsScreen(puzzleId: String, navController: NavController) {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: PuzzleDetailsViewModel = viewModel(factory = AppViewModelFactory(app, puzzleId = puzzleId))
    val scope = rememberCoroutineScope()

    val puzzleCache by viewModel.puzzleCache.collectAsState()
    val details by viewModel.details.collectAsState()
    var difficulty by remember(details) { mutableStateOf(details?.difficulty) }
    var tags by remember(details) { mutableStateOf(details?.tags ?: "") }
    var notes by remember(details) { mutableStateOf(details?.notes ?: "") }

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

            Button(onClick = {
                scope.launch {
                    val attemptId = viewModel.createAttempt(puzzleId)
                    navController.navigate(Screen.AttemptTracking.createRoute(attemptId))
                }
            }) {
                Text("Start attempt")
            }
        }
    }
}