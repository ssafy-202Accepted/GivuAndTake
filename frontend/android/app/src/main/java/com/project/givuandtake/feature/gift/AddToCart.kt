package com.project.givuandtake.feature.gift

import android.content.Context
import android.util.Log

import com.project.givuandtake.core.apis.RetrofitClient
import com.project.givuandtake.core.data.CartItemData
import com.project.givuandtake.core.data.CartRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import com.project.givuandtake.core.datastore.TokenDataStore
import com.project.givuandtake.core.datastore.TokenManager


// DataStore에서 cartItems를 관리하는 함수 (GiftDetail을 추가할 수 있도록 수정)
//suspend fun addToCart(context: Context, giftDetail: GiftDetail, quantity: Int) {
//    // 현재 장바구니 항목을 DataStore에서 불러옴
//    val currentCartItems = withContext(Dispatchers.IO) {
//        getCartItems(context).first()
//    }
//
//    // GiftDetail을 CartItem으로 변환
//    val newItem = CartItem(
//        name = giftDetail.giftName,
//        price = giftDetail.price,
//        quantity = quantity,
//        location = giftDetail.location
//    )
//
//    // 장바구니 업데이트 로직
//    val updatedCartItems = currentCartItems.toMutableList().apply {
//        val existingItemIndex = indexOfFirst { it.name == newItem.name && it.location == newItem.location }
//
//        if (existingItemIndex != -1) {
//            // 기존 아이템이 있다면 수량을 업데이트
//            val existingItem = this[existingItemIndex]
//            this[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + newItem.quantity)
//        } else {
//            // 기존 아이템이 없다면 새 아이템 추가
//            add(newItem)
//        }
//    }
//
//    // DataStore에 업데이트된 장바구니 저장
//    saveCartItems(context, updatedCartItems)
//}
suspend fun fetchCartList(token: String): List<CartItemData>? {
    return try {
        val response = RetrofitClient.cartApiService.getCartList(token)
        if (response.isSuccessful) {
            response.body()?.data  // 성공 시 장바구니 데이터를 반환
        } else {
            println("HTTP 에러: ${response.code()} - ${response.message()}")
            null
        }
    } catch (e: Exception) {
        println("Error: ${e.localizedMessage}")
        null
    }
}

suspend fun addToCartApi(context: Context, giftIdx: Int, amount: Int): Boolean {
    val tokenDataStore = TokenDataStore(context)

    return try {
        // 토큰 가져오기 (Flow에서 첫 번째 값만 가져옴)
        val storedAccessToken = TokenManager.getAccessToken(context)
        val token = "Bearer ${TokenManager.getAccessToken(context)}"
        Log.d("Cart", "token : ${token}")
        Log.d("Cart", "storedAccessToken : ${storedAccessToken}")

        // 토큰이 null인 경우 처리
        if (token.isNullOrEmpty()) {
            println("토큰이 없습니다. 로그인 필요.")
            return false
        }

        // 네트워크 호출은 IO 스레드에서 수행
        val cartRequest = CartRequest(giftIdx, amount)
        Log.d("Cart", "request : ${cartRequest}")
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.cartApiService.addToCart("$token", cartRequest)
        }
        Log.d("Cart","response : ${response}")

        // 응답 처리
        if (response.isSuccessful) {
            val cartResponse = response.body()
            cartResponse?.let {
                return it.success
            }
            return false
        } else {
            println("HTTP 에러: ${response.code()} - ${response.message()}")
            return false
        }
    } catch (e: Exception) {
        println("Error: ${e.localizedMessage}")
        return false
    }
}

// 수량 변경
suspend fun updateCartItemQuantity(context: Context, cartIdx: Int, newAmount: Int): Boolean {
    val token = TokenManager.getAccessToken(context)
    return try {
        if (token.isNullOrEmpty()) {
            Log.e("Cart", "토큰이 없습니다. 로그인 필요.")
            return false
        }

        val cartRequest = CartRequest(cartIdx, newAmount)
        val response = withContext(Dispatchers.IO) {
            RetrofitClient.cartApiService.updateCartItemQuantity("Bearer $token", cartIdx, cartRequest)
        }

        if (response.isSuccessful) {
            Log.d("Cart", "수량 업데이트 성공: ${response.body()}")
            response.body()?.success == true
        } else {
            Log.e("Cart", "수량 업데이트 실패 - 코드: ${response.code()}, 오류: ${response.errorBody()?.string()}")
            false
        }
    } catch (e: Exception) {
        Log.e("Cart", "수량 업데이트 실패: ${e.localizedMessage}")
        false
    }
}


// 장바구니 삭제
suspend fun deleteCartItem(context: Context, cartIdx: Int): Boolean {
    val token = TokenManager.getAccessToken(context)
    return try {
        if (token.isNullOrEmpty()) {
            Log.e("Cart", "토큰이 없습니다. 로그인 필요.")
            return false
        }

        val response = withContext(Dispatchers.IO) {
            RetrofitClient.cartApiService.deleteCartItem("Bearer $token", cartIdx)
        }

        Log.d("Cart","delete : ${response}")
        if (response.isSuccessful) {
            Log.d("Cart", "삭제 성공: ${response.body()}")
            response.body()?.success == true
        } else {
            Log.e("Cart", "삭제 실패 - 코드: ${response.code()}, 오류: ${response.errorBody()?.string()}")
            false
        }
    } catch (e: Exception) {
        Log.e("Cart", "삭제 실패: ${e.localizedMessage}")
        false
    }
}





