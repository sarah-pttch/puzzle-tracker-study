package com.study.puzzletracker.data.repository

import com.study.puzzletracker.data.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {
    suspend fun signUp(email: String, password: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signIn(email: String, password: String) {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        supabase.auth.signOut()
    }

    val sessionStatus = supabase.auth.sessionStatus
    val currentUserId: String?
        get() = supabase.auth.currentUserOrNull()?.id
}