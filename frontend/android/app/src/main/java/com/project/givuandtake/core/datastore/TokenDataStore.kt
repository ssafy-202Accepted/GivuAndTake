package com.project.givuandtake.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore 초기화 (중복을 피하기 위해 이름 변경)
val Context.tokenDataStore by preferencesDataStore(name = "token_prefs")

class TokenDataStore(context: Context) {
    private val dataStore = context.tokenDataStore

    companion object {
        val TOKEN_KEY = stringPreferencesKey("bearer_token")
    }

    // 토큰을 저장하는 함수
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // 토큰을 불러오는 함수
    val token: Flow<String?> = dataStore.data
        .map { preferences ->
            preferences[TOKEN_KEY]
        }
}


