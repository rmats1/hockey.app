package com.example.hockey_app.di

import android.content.Context
import io.github.jan.supabase.auth.SessionManager
import io.github.jan.supabase.auth.user.UserSession
import io.github.jan.supabase.auth.exception.NoSessionFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

class AndroidSessionManager(context: Context) : SessionManager {
    private val prefs = context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun saveSession(session: UserSession) = withContext(Dispatchers.IO) {
        try {
            val sessionJson = json.encodeToString(session)
            // Usamos commit() para asegurar escritura física inmediata antes de cerrar la app
            val success = prefs.edit().putString("session", sessionJson).commit()
            if (success) {
                Timber.d("Session saved successfully to disk.")
            } else {
                Timber.e("Failed to commit session to disk.")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error saving session.")
        }
    }

    override suspend fun loadSession(): UserSession = withContext(Dispatchers.IO) {
        val data = prefs.getString("session", null)
        if (data.isNullOrBlank()) {
            Timber.d("No session found in storage.")
            throw NoSessionFoundException()
        }
        try {
            val session = json.decodeFromString<UserSession>(data)
            Timber.d("Session loaded successfully from disk.")
            session
        } catch (e: Exception) {
            Timber.e(e, "Error decoding session.")
            throw NoSessionFoundException()
        }
    }

    override suspend fun deleteSession() = withContext(Dispatchers.IO) {
        prefs.edit().remove("session").commit()
        Timber.d("Session deleted from disk.")
    }
}
