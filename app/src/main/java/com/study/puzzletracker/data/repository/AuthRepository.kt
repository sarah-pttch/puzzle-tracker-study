package com.study.puzzletracker.data.repository

import android.content.Context
import com.study.puzzletracker.data.remote.supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("auth", Context.MODE_PRIVATE)

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
        get() = supabase.auth.currentUserOrNull()?.id ?: prefs.getString(KEY_USER_ID, null)

    fun rememberUser() {
        supabase.auth.currentUserOrNull()?.id?.let {
            prefs.edit().putString(KEY_USER_ID, it).apply()
        }
    }

    fun forgetUser() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    private companion object {
        const val KEY_USER_ID = "user_id"
    }
}