package com.project.givuandtake.core.data.GiftComment

data class GiftCommentPostData(
    val reviewContent: String,
    val giftIdx: Int,
    val orderIdx: Int
)