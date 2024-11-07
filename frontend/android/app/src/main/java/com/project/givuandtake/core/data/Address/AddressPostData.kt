package com.project.givuandtake.core.data.Address

data class AddressPostData(
    val zoneCode: String,
    val addressName: String,
    val address: String,
    val roadAddress: String,
    val jibunAddress: String,
    val detailAddress: String,
    val buildingName: String,
    val isApartment: Boolean,
    val sido: String,
    val sigungu: String,
    val bname: String,
    val bname1: String,
    val isRepresentative: Boolean
)