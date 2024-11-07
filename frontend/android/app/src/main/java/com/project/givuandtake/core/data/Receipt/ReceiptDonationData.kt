package com.project.givuandtake.core.data.Receipt

data class ReceiptDonationData(
    val success: Boolean,
    val data: List<DonationDetail>
)

data class DonationDetail(
    val type: String,
    val name: String,
    val date: String,
    val price: Int,
    val ref: String
)