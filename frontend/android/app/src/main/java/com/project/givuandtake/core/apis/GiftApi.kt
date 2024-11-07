package com.project.givuandtake.core.apis

import com.project.givuandtake.core.data.CartRequest
import com.project.givuandtake.core.data.CartResponse
import com.project.givuandtake.core.data.Gift.PurchaseRequest
import com.project.givuandtake.core.data.Gift.PurchaseResponse
import com.project.givuandtake.core.data.Gift.WishlistResponse
import com.project.givuandtake.core.data.GiftDetailResponse
import com.project.givuandtake.core.data.GiftResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GiftApiService {

    // 코루틴을 사용한 비동기 API 호출
    @GET("/api/gifts")
    suspend fun getGifts(
        @Header("Authorization") token: String
    ): Response<GiftResponse> // suspend 함수로 변경하여 코루틴에서 사용 가능

    @GET("/api/gifts/{giftIdx}")
    suspend fun getGiftDetail(
        @Header("Authorization") token: String,
        @Path("giftIdx") giftIdx: Int
    ): Response<GiftDetailResponse> // suspend 함수로 변경하여 코루틴에서 사용 가능


    // 장바구니에 상품 추가 API
    @POST("/api/users/shopping-cart")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Body cartRequest: CartRequest  // POST 요청 바디에 들어갈 데이터
    ): Response<CartResponse>  // 서버에서 받은 응답



    // 특정 카테고리 상품 검색 API
    @GET("/api/gifts")
    suspend fun getGiftsByCategory(
        @Query("categoryIdx") categoryIdx: Int
    ): GiftResponse


    // 최신 상품 검색 API
    @GET("/api/gifts/recent")
    suspend fun getRecentGifts(
        @Header("Authorization") token: String
    ) : Response<GiftResponse>


    // GET 요청: 현재 사용자의 찜 목록을 가져옴
    @GET("/api/users/wish")
    suspend fun getWishlist(
        @Header("Authorization") token: String
    ): Response<WishlistResponse>

    // POST 요청: 찜 목록에 추가
    @POST("/api/users/wish")
    suspend fun addToWishlist(
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): Response<Unit>

    // DELETE 요청: 찜 목록에서 제거
    @DELETE("/api/users/wish")
    suspend fun removeFromWishlist(
        @Header("Authorization") token: String,
        @Query("wishIdx") wishIdx: Int
    ): Response<Unit>


    @POST("/api/purchases")
    suspend fun postPurchase(
        @Header("Authorization") token: String,
        @Body purchaseRequest: PurchaseRequest
    ): Response<PurchaseResponse>

}

