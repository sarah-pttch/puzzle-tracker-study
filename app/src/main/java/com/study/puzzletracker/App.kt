package com.study.puzzletracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.study.puzzletracker.navigation.MainScreen
import com.study.puzzletracker.ui.auth.LoginScreen
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun App() {
    val context = LocalContext.current
    val app = context.applicationContext as PuzzleTrackerApp
        val sessionStatus by app.container.authRepository.sessionStatus.collectAsState()

    when (sessionStatus) {
        is SessionStatus.Authenticated -> MainScreen()
        is SessionStatus.NotAuthenticated -> LoginScreen()
        is SessionStatus.Initializing -> LoadingScreen()
        is SessionStatus.RefreshFailure -> LoginScreen()
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}