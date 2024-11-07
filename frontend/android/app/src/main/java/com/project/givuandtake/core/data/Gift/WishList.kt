package com.project.givuandtake.core.data.Gift

data class WishlistResponse(
    val success: Boolean,
    val data: List<WishlistItem>
)

data class WishlistItem(
    val wishIdx: Int,
    val giftIdx: Int,
    val giftName: String,
    val giftThumbnail: String,
    val userIdx: Int
)
