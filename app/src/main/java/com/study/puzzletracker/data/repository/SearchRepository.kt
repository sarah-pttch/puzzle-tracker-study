package com.study.puzzletracker.data.repository

import com.study.puzzletracker.data.remote.dto.PuzzleDto
import com.study.puzzletracker.data.remote.supabase
import io.github.jan.supabase.postgrest.postgrest

class SearchRepository {
    suspend fun searchPuzzles(query: String): List<PuzzleDto> {
        if (query.isBlank()) return emptyList()
        return supabase.postgrest["puzzles"]
            .select {
                filter {
                    or {
                        ilike("name", "%$query%")
                        ilike("barcode", "%$query%")
                    }
                }
            }
            .decodeList<PuzzleDto>()
    }
}