package com.project.givuandtake.core.apis.Viliage

import com.project.givuandtake.core.apis.Address.AddressApiService
import com.project.givuandtake.core.data.Viliage.VillageData
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ViliageApiService {
    @GET("experience-village")
    suspend fun getExperienceVillage(
        @Query("sido") sido: String,
        @Query("sigungu") sigungu: String,
        @Query("division") division: String? = null,
        @Query("pageNo") pageNo: Int? = null,
        @Query("pageSize") pageSize: Int? = null
    ): Response<VillageData>
}


// Retrofit 인스턴스 생성
object ViliageApi {
    private const val BASE_URL = "https://j11e202.p.ssafy.io/api/"

    val api: ViliageApiService by lazy {
        // HttpLoggingInterceptor 추가
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)  // OkHttpClient 설정
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViliageApiService::class.java)
    }
}