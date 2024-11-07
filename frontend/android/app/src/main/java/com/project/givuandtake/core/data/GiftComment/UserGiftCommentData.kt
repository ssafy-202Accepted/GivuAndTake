package com.project.givuandtake.core.data.GiftComment

data class UserGiftCommentData(
    val success: Boolean,
    val data: List<GiftReview>
)

data class GiftReview(
    val reviewIdx: Int,
    val reviewContent: String,
    val giftIdx: Int,
    val reviewImage: String,
    val giftName: String,
    val giftThumbnail: String,
    val corporationName: String,
    val likedCount: Int,
    val userIdx: Int,
    val userName: String,
    val userProfileImage: String,
    val createdDate: String,
    val modifiedDate: String
)
