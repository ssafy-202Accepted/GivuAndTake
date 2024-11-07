package com.project.givuandtake.core.data

data class AddressDto(
    var zoneCode: String = "",
    var addressName: String = "",
    var address: String = "",
    var userSelectedType: String = "R",
    var roadAddress: String = "",
    var jibunAddress: String = "",
    var detailAddress: String = "",
    var autoRoadAddress: String = "",
    var autoJibunAddress: String = "",
    var buildingCode: String = "",
    var buildingName: String = "",
    var isApartment: Boolean = true,
    var sido: String = "",
    var sigungu: String = "",
    var sigunguCode: String = "",
    var roadNameCode: String = "",
    var bcode: String = "",
    var roadName: String = "",
    var bname: String = "",
    var bname1: String = "",
    var isRepresentative: Boolean = true
)