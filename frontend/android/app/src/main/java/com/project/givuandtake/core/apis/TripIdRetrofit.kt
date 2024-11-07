package com.project.givuandtake.core.apis

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TourismIdRetrofitInstance {
    private const val BASE_URL = "https://apis.data.go.kr/"
    val gson = GsonBuilder()
        .setLenient()  // 이 부분을 추가하여 lenient 모드로 JSON을 파싱하도록 설정합니다.
        .create()
    val api: TourismIdApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TourismIdApiService::class.java)
    }
}