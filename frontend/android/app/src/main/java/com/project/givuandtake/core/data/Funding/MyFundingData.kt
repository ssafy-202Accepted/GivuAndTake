package com.project.givuandtake.core.data.Funding

data class MyFundingData(
    val success: Boolean,
    val data: List<FundingData>
)

data class FundingData(
    val fundingIdx: Int,
    val fundingThumbnail: String?,
    val fundingTitle: String,
    val fundingFee: Int,
    val fundingType: String,
    val createdDate: String
)
