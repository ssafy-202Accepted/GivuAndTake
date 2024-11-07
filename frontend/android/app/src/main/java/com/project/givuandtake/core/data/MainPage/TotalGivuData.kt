package com.project.givuandtake.core.data.MainPage

data class TotalGivuData(
    val success: Boolean,
    val data: TotalGivu
)

data class TotalGivu(
    val price: Long
)