package com.project.givuandtake.core.data.Address

data class JusoResponse(val results: JusoResults)

data class JusoResults(val juso: List<Juso>)

data class Juso(
    val roadAddr: String,
    val jibunAddr: String,
    val zipNo: String,
    val bdNm: String,
    val bdKdcd: String,
    val siNm: String,
    val sggNm: String,
    val emdNm: String,
    val liNm: String
)
