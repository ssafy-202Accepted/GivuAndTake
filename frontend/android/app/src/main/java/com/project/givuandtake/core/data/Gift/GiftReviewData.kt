package com.project.givuandtake.core.data.Gift

data class GiftReviewData(
    val success: Boolean,
    val data: List<ReviewData>
)

data class ReviewData(
    val reviewIdx: Int,
    val reviewImage: String,
    val reviewContent: String,
    val giftIdx: Int,
    val giftName: String,
    val giftThumbnail: String,
    val corporationName: String,
    val userIdx: Int,
    val userName: String,
    val userProfileImage: String?,
    val orderIdx: Int,
    val likedCount: Int,
    val createdDate: String,
    val modifiedDate: String
)
