package com.project.givuandtake.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore 초기화
val Context.dataStore by preferencesDataStore("favorites")

object FavoriteKeys {
    val FAVORITES = stringSetPreferencesKey("favorite_products")
}

