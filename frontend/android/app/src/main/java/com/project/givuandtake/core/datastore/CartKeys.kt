package com.project.givuandtake.core.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.givuandtake.core.data.CartItemData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

// DataStore 초기화
val Context.cartDataStore by preferencesDataStore("cart")

object CartKeys {
    val CART_ITEMS = stringSetPreferencesKey("cart_items")
}

// CartItem을 문자열로 변환하는 함수
// CartItemData를 저장 가능한 문자열로 변환하는 함수
private fun CartItemData.toStorageString(): String {
    return "${this.cartIdx}|${this.giftIdx}|${this.giftName}|${this.giftThumbnail}|${this.userIdx}|${this.amount}|${this.price}|${this.location}"
}


// 문자열을 CartItemData로 변환하는 함수
private fun String.toCartItemData(): CartItemData {
    val parts = this.split("|")
    Log.d("cart", "parts : $parts")
    return CartItemData(
        cartIdx = parts.getOrNull(0)?.toIntOrNull() ?: 0,  // cartIdx가 없거나 변환에 실패하면 기본값 0 사용
        giftIdx = parts.getOrNull(1)?.toIntOrNull() ?: 0,  // giftIdx가 없거나 변환에 실패하면 기본값 0 사용
        giftName = parts.getOrNull(2) ?: "Unknown",        // giftName이 없으면 기본값 사용
        giftThumbnail = parts.getOrNull(3) ?: "",          // giftThumbnail이 없으면 빈 문자열 사용
        userIdx = parts.getOrNull(4)?.toIntOrNull() ?: 0,  // userIdx가 없으면 기본값 0 사용
        amount = parts.getOrNull(5)?.toIntOrNull() ?: 1,   // amount가 없으면 기본값 1 사용
        price = parts.getOrNull(6)?.toIntOrNull() ?: 0,    // price가 없으면 기본값 0 사용
        sido = parts.getOrNull(7) ?: "Unknown",            // sido가 없으면 기본값 사용
        sigungu = parts.getOrNull(8) ?: "Unknown"          // sigungu가 없으면 기본값 사용
    )
}

 //장바구니 항목 저장
//suspend fun saveCartItems(context: Context, cartItems: List<CartItemData>) {
//    val cartStrings = cartItems.map { it.toStorageString() }.toSet() // List<CartItem>을 Set<String>으로 변환
//    context.cartDataStore.edit { preferences ->
//        preferences[CartKeys.CART_ITEMS] = cartStrings
//    }
//}
//
// 장바구니 항목 불러오기
//fun getCartItems(context: Context): Flow<List<CartItemData>> {
//    return context.cartDataStore.data.map { preferences ->
//        val cartStrings = preferences[CartKeys.CART_ITEMS] ?: emptySet()
//        cartStrings.map { it.toCartItem() } // Set<String>을 List<CartItem>으로 변환
//    }
//}