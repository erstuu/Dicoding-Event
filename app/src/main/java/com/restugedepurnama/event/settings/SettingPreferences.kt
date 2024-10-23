package com.restugedepurnama.event.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>){

    private val THEME_KEY = booleanPreferencesKey("theme_setting")
    private val REMINDER_KEY = booleanPreferencesKey("reminder_setting")

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    fun getThemeSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    fun getReminderSetting(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[REMINDER_KEY] ?: false
        }
    }

    suspend fun saveReminderSetting(isReminderActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[REMINDER_KEY] = isReminderActive
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingPreferences(dataStore).also {
                    INSTANCE = it
                }
            }

    }

}