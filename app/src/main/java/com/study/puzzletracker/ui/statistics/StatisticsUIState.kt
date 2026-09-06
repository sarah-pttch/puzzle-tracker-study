package com.study.puzzletracker.ui.statistics

data class FastestAttempt(val puzzleName: String, val completionTimeSeconds: Long)

data class StatisticsUIState(
    val collectionCount: Int = 0,
    val puzzleCount: Int = 0,
    val attemptCount: Int = 0,
    val fastest500: FastestAttempt? = null,
    val fastest1000: FastestAttempt? = null,
    val fastest500FirstAttempt: FastestAttempt? = null
)
