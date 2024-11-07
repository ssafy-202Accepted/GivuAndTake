package com.project.givuandtake.core.data.MainPage


data class TopGivuData(
    val success: Boolean,
    val data: TopGivuDataResponse
)

data class TopGivuDataResponse(
    val top10Gifts: List<Gift>?,
    val recentGifts: List<Gift>?, // 최근 선물이 없을 수 있어 nullable 처리
    val deadlineImminentFundings: List<Funding>?
)

data class Gift(
    val giftIdx: Int,
    val giftName: String,
    val corporationIdx: Int,
    val corporationName: String,
    val corporationSido: String,
    val corporationSigungu: String,
    val categoryIdx: Int,
    val categoryName: String,
    val giftThumbnail: String?,
    val giftContentImage: String?,
    val giftContent: String?,
    val price: Int,
    val createdDate: String?,
    val modifiedDate: String?
)

// 펀딩 데이터 클래스
data class Funding(
    val fundingIdx: Int,
    val sido: String,
    val sigungu: String,
    val fundingTitle: String,
    val goalMoney: Int,
    val totalMoney: Int,
    val startDate: String,
    val endDate: String,
    val fundingThumbnail: String?,
    val fundingType: String
)