package com.study.puzzletracker.data.local.entity

import androidx.room.Embedded

data class AttemptWithPuzzle(
    @Embedded val attempt: AttemptEntity,
    val puzzleName: String,
    val puzzleManufacturer: String,
    val puzzlePieceCount: Int
)
