package com.upsewa.hub.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed preferences for:
 *  - language (en / hi)
 *  - favourite service ids
 *
 * Nothing else is persisted; the app stores no PII.
 */
private val Context.dataStore by preferencesDataStore(name = "upsewa_prefs")

object Prefs {
    private val KEY_LANG = stringPreferencesKey("lang")
    private val KEY_FAVS = stringSetPreferencesKey("favs")

    fun languageFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LANG] ?: defaultLanguage() }

    fun favoritesFlow(context: Context): Flow<Set<String>> =
        context.dataStore.data.map { it[KEY_FAVS] ?: emptySet() }

    suspend fun setLanguage(context: Context, lang: String) {
        context.dataStore.edit { it[KEY_LANG] = lang }
    }

    suspend fun toggleFavorite(context: Context, serviceId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[KEY_FAVS] ?: emptySet()
            prefs[KEY_FAVS] =
                if (serviceId in current) current - serviceId else current + serviceId
        }
    }

    private fun defaultLanguage(): String {
        val sys = java.util.Locale.getDefault().language
        return if (sys == "hi") "hi" else "en"
    }
}
