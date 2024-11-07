package com.project.givuandtake.core.data.Gift

data class MyGiftData(
    val success: Boolean,
    val data: List<GiftData>
)

data class GiftData(
    val orderIdx: Int,
    val userIdx: Int,
    val regionName: String,
    val giftIdx: Int,
    val giftName: String,
    val giftThumbnail: String?,
    val paymentMethod: String,
    val amount: Int,
    val price: Int,
    val isWrite: Boolean,
    val status: String,
    val createdDate: String
)
