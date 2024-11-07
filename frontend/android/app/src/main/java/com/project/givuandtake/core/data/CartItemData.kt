package com.project.givuandtake.core.data

import kotlinx.serialization.Serializable

@Serializable
data class CartData(
    val cartIdx: Int,   // 장바구니 항목 ID
    val giftIdx: Int,   // giftIdx 추가
    val name: String,
    val price: Int,
    val quantity: Int,
    val location: String
)

@Serializable
data class CartRequest(
    val giftIdx: Int,
    val amount: Int
)

@Serializable
data class CartResponse(
    val success: Boolean,
    val data: Any?,  // 성공 시 data는 null
    val code: String?,  // 실패 시 코드 (성공 시에는 null)
    val message: String?  // 실패 시 메시지 (성공 시에는 null)
)

@Serializable
data class CartItemDataResponse(
    val success: Boolean,
    val data: List<CartItemData>
)

@Serializable
data class CartItemData(
    val cartIdx: Int,
    val giftIdx: Int,
    val giftName: String,
    val giftThumbnail: String?,
    val sido: String,
    val sigungu: String,
    val userIdx: Int,
    val amount: Int,
    val price: Int,
) {
    // sido와 sigungu를 결합한 location 변수를 추가
    val location: String
        get() = "$sido $sigungu"
}




