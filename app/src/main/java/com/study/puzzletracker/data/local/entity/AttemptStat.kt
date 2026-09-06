package com.study.puzzletracker.data.local.entity

data class AttemptStat(
    val puzzleId: String,
    val puzzleName: String,
    val pieceCount: Int,
    val completionTimeSeconds: Long,
    val startedAt: Long
)