package com.project.givuandtake.core.data

import com.project.givuandtake.feature.attraction.FestivalItemData


data class FestivalMainData(
    val response: FestivalResponse
)

data class FestivalResponse(
    val body: FestivalBody
)

data class FestivalBody(
    val items: List<FestivalItemData>
)