package com.project.givuandtake.core.apis

import com.project.givuandtake.core.data.TripIdData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TourismIdApiService {
    @GET("B551011/KorService1/areaBasedList1")
    fun getTourismIdData(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int = 999,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") mobileApp: String = "GivuAndTake",
        @Query("_type") type: String = "json",
        @Query("contentTypeId") contentTypeId: Int = 12,
        @Query("areaCode") areaCode: Int,
        @Query("sigunguCode") sigunguCode: Int
    ): Call<TripIdData>
}