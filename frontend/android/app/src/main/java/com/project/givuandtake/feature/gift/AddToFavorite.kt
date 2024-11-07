package com.project.givuandtake.feature.gift

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.project.givuandtake.core.data.GiftDetail
import com.project.givuandtake.core.datastore.FavoriteKeys
import com.project.givuandtake.core.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore에서 찜 관리 함수 (GiftDetail 사용)
suspend fun addToFavorites(context: Context, giftDetail: GiftDetail) {
    context.dataStore.edit { preferences ->
        // 기존 찜 목록을 불러와 MutableSet으로 변환
        val favorites = preferences[FavoriteKeys.FAVORITES]?.toMutableSet() ?: mutableSetOf()

        if (favorites.contains(giftDetail.giftIdx.toString())) {
            // 이미 찜한 상품이면 제거
            favorites.remove(giftDetail.giftIdx.toString())
        } else {
            // 찜한 상품 추가
            favorites.add(giftDetail.giftIdx.toString())
        }

        // 수정된 찜 목록을 다시 저장
        preferences[FavoriteKeys.FAVORITES] = favorites
    }
}

// 찜한 상품 목록을 불러오는 함수
fun getFavoriteProducts(context: Context): Flow<Set<String>> {
    return context.dataStore.data.map { preferences ->
        // 찜 목록이 없을 경우 빈 집합 반환
        preferences[FavoriteKeys.FAVORITES] ?: emptySet()

    }
}
