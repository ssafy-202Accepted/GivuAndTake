package com.project.givuandtake.core.apis.Gift

import com.project.givuandtake.core.apis.RetrofitClient
import com.project.givuandtake.core.data.CartItemData
import com.project.givuandtake.core.data.CartItemDataResponse
import com.project.givuandtake.core.data.CartRequest
import com.project.givuandtake.core.data.CartResponse
import retrofit2.Response
import retrofit2.http.*

interface CartApiService {

    // 장바구니 목록 불러오기 API
    @GET("/api/users/shopping-cart")
    suspend fun getCartList(
        @Header("Authorization") token: String,
    ): Response<CartItemDataResponse>  // 서버에서 받은 응답


    // 장바구니에 아이템 추가 (POST)
    @POST("/api/users/shopping-cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,  // 토큰을 헤더에 포함
        @Body cartRequest: CartRequest           // 장바구니 추가 요청 Body
    ): Response<CartResponse>

    // 장바구니 수량 변경 (PATCH)
    @PATCH("/api/users/shopping-cart/{cartIdx}")
    suspend fun updateCartItemQuantity(
        @Header("Authorization") token: String,  // 토큰을 헤더에 포함
        @Path("cartIdx") cartIdx: Int,           // 변경할 상품의 ID
        @Body cartRequest: CartRequest           // 변경할 수량
    ): Response<CartResponse>

    // 장바구니 아이템 삭제 (DELETE)
    @DELETE("/api/users/shopping-cart/{cartIdx}")
    suspend fun deleteCartItem(
        @Header("Authorization") token: String,  // 토큰을 헤더에 포함
        @Path("cartIdx") cartIdx: Int            // 삭제할 상품의 ID
    ): Response<CartResponse>
}
