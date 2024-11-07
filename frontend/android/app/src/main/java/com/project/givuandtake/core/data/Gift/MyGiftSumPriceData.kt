package com.project.givuandtake.core.data.Gift

data class MyGiftSumPriceData(
    val success: Boolean,
    val data: GiftSumPriceData // The 'data' field is not a list but a single object containing 'price'
)

data class GiftSumPriceData(
    val price: Long
)