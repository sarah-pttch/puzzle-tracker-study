package com.study.puzzletracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PuzzleDto(
    val id: String,
    val name: String,
    val barcode: String? = null,
    @SerialName("piece_count") val pieceCount: Int,
    val manufacturer: String,
    val status: String
)
