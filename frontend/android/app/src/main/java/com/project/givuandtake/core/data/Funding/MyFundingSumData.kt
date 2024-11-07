package com.project.givuandtake.core.data.Funding

data class MyFundingSumData(
    val success: Boolean,
    val data: FundingSumData // The 'data' field is not a list but a single object containing 'price'
)

data class FundingSumData(
    val count: Long
)