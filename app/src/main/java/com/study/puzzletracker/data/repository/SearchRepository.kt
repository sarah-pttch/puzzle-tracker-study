package com.study.puzzletracker.data.repository

import com.study.puzzletracker.data.remote.dto.NewPuzzleDto
import com.study.puzzletracker.data.remote.dto.PuzzleDto
import com.study.puzzletracker.data.remote.supabase
import io.github.jan.supabase.postgrest.postgrest

class SearchRepository {
    suspend fun searchPuzzles(query: String, userId: String?): List<PuzzleDto> {
        if (query.isBlank()) return emptyList()
        return supabase.postgrest["puzzles"]
            .select {
                filter {
                    and {
                        or {
                            ilike("name", "%$query%")
                            ilike("barcode", "%$query%")
                        }
                        or {
                            isIn("status", listOf("verified", "pending_verification"))
                            if (userId != null) {
                                and {
                                    eq("status", "personal")
                                    eq("submitted_by", userId)
                                }
                            }
                        }
                    }
                }
            }
            .decodeList<PuzzleDto>()
    }

    suspend fun createPuzzle(
        name: String,
        barcode: String?,
        pieceCount: Int,
        manufacturer: String,
        publicationYear: Int?,
        toGlobalCollection: Boolean,
        submittedBy: String?
    ): PuzzleDto {
        val status = if (toGlobalCollection) "pending_verification" else "personal"
        return supabase.postgrest["puzzles"]
            .insert(
                NewPuzzleDto(
                    name = name,
                    barcode = barcode,
                    pieceCount = pieceCount,
                    manufacturer = manufacturer,
                    publicationYear = publicationYear,
                    status = status,
                    submittedBy = submittedBy
                )
            ) {
                select()
            }
            .decodeSingle<PuzzleDto>()
    }
}