package com.project.givuandtake.core.data.Gift

data class PurchaseRequest(
    val giftIdx: Int,
    val paymentMethod: String,
    val amount: Int
)

data class PurchaseResponse(
    val success: Boolean,
    val message: String
)
