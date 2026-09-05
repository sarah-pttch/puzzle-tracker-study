package com.study.puzzletracker.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://cwdsbuxbzjutnuylrgdv.supabase.co",
    supabaseKey = "sb_publishable_XaGwsX7Y5ZMumnmD-KGHAA_H7oKFsVY"
) {
    install(Auth)
    install(Postgrest)
}