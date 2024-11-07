package com.project.givuandtake.core.data

data class TripIdData(
    val response: ResponseTripData
)

data class ResponseTripData(
    val body: BodyData
)

data class BodyData(
    val items: ItemsData,
)

data class ItemsData(
    val item: List<TourismItem>
)

data class TourismItem(
    val contentid: String,  // JSON에서 확인된 키 이름이 소문자 'contentid'였음
    val addr1: String?,
    val addr2: String?,
    val areacode: String?,
    val booktour: String?,
    val cat1: String?,
    val cat2: String?,
    val cat3: String?,
    val contenttypeid: String?,
    val createdtime: String?,
    val firstimage: String?,
    val firstimage2: String?,
    val cpyrhtDivCd: String?,
    val mapx: String?,
    val mapy: String?,
    val mlevel: String?,
    val modifiedtime: String?,
    val sigungucode: String?,
    val tel: String?,
    val title: String?,
    val zipcode: String?
)