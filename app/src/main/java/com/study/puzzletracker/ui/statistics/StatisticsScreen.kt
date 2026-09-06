package com.study.puzzletracker.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.study.puzzletracker.AppViewModelFactory
import com.study.puzzletracker.PuzzleTrackerApp

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
    val viewModel: StatisticsViewModel = viewModel(factory = AppViewModelFactory(app))
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        StatRow("Collections", state.collectionCount.toString())
        StatRow("Puzzles", state.puzzleCount.toString())
        StatRow("Attempts", state.attemptCount.toString())
        FastestRow("Fastest 500-piece puzzle", state.fastest500)
        FastestRow("Fastest 1000-piece puzzle", state.fastest1000)
        FastestRow("Fastest 500-piece first attempt", state.fastest500FirstAttempt)
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FastestRow(label: String, fastest: FastestAttempt?) {
    Spacer(Modifier.height(16.dp))
    Text(label)
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            fastest?.puzzleName ?: "",
            fontWeight = FontWeight.Bold
        )
        Text(
            fastest?.let { formatDuration(it.completionTimeSeconds) } ?: "",
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%02d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}