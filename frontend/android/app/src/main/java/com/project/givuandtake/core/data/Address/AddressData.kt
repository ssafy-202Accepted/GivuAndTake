package com.project.givuandtake.core.data.Address

data class AddressData(
    val success: Boolean,
    val data: List<UserAddress>
)

data class UserAddress(
    val addressIdx: Int,
    val addressName: String,
    val roadAddress: String,
    val jibunAddress: String,
    val detailAddress: String,
    val representative: Boolean
)
