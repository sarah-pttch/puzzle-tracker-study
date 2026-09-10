package com.study.puzzletracker.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewPuzzleDto(
    val name: String,
    val barcode: String? = null,
    @SerialName("piece_count") val pieceCount: Int,
    val manufacturer: String,
    @SerialName("publication_year") val publicationYear: Int? = null,
    val status: String,
    @SerialName("submitted_by") val submittedBy: String? = null
)