package com.project.givuandtake.core.data

data class TraditionalMarketData(
    val response: ResponseData
)

data class ResponseData(
    val status: String,
    val result: MarketResult
)

data class MarketResult(
    val featureCollection: FeatureCollection
)

data class FeatureCollection(
    val features: List<Feature>
)

data class Feature(
    val properties: MarketProperties,
)

data class MarketProperties(
    val name: String,           // 시장 이름
    val category: String,       // 시장 종류
    val adr_road: String,       // 도로명 주소
    val adr_jibun: String,      // 지번 주소
    val market: String,         // 시장 고유 번호
    val items: String,          // 판매 품목
    val giftcard: String?,      // 상품권 여부
    val toilet: String,         // 화장실 여부
    val park: String,           // 주차장 여부
    val opn_per: String,        // 개장 기간
    val tel_num: String         // 전화번호
)