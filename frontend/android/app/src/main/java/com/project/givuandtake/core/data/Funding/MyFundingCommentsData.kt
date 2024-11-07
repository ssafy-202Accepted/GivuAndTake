package com.project.givuandtake.core.data.Funding

data class MyFundingCommentsData(
    val success: Boolean,
    val data: List<FundingCommentData>
)

data class FundingCommentData(
    val commentIdx: Int,
    val commentContent: String,
    val createdDate: String,
    val fundingIdx: Int,
    val fundingTitle: String,
    val fundingThumbnail: String
)