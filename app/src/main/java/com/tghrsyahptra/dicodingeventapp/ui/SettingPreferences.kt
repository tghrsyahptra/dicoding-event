package com.tghrsyahptra.dicodingeventapp.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Membuat DataStore untuk Preferences
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val themePreferenceKey = booleanPreferencesKey("theme_setting")
    private val notificationPreferenceKey = booleanPreferencesKey("notification_setting")

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[themePreferenceKey] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[themePreferenceKey] = isDarkModeEnabled
        }
    }

    fun getNotificationSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[notificationPreferenceKey] ?: false
        }
    }

    suspend fun saveNotificationSetting(isNotificationEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[notificationPreferenceKey] = isNotificationEnabled
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}