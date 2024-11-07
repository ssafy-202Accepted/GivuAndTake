package com.project.givuandtake.core.datastore

import android.content.Context
import android.util.Log
import com.project.givuandtake.core.apis.RetrofitClient
import com.project.givuandtake.core.data.DatabaseProvider
import com.project.givuandtake.core.data.GiftDetail
import com.project.givuandtake.core.data.GiftDetailData
import com.project.givuandtake.core.data.GiftResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GiftRepository(private val context: Context) {
    private val giftDetailDao = DatabaseProvider.getDatabase(context).giftDetailDao()

    // Room에서 모든 상품 데이터를 가져옴
    fun getAllGiftDetails(): Flow<List<GiftDetail>> {
        return giftDetailDao.getAllGiftDetails()
    }

    // Room에서 특정 상품 ID로 데이터를 가져옴
    fun getGiftDetailsByIds(ids: List<Int>): Flow<List<GiftDetail>> {
        return giftDetailDao.getGiftDetailsByIds(ids)
    }

    // API에서 데이터를 불러와 Room에 저장 (코루틴 사용)
    suspend fun fetchGiftsFromApi(token: String) {
        withContext(Dispatchers.IO) {
            try {
                // 코루틴 기반 API 호출
                val response = RetrofitClient.giftApiService.getGifts(token)

                if (response.isSuccessful) {
                    response.body()?.let { giftResponse: GiftResponse ->
                        if (giftResponse.success) {
                            // API 데이터를 GiftDetail 엔티티에 매핑
                            val giftDetails = giftResponse.data.map { giftItem ->
                                GiftDetail(
                                    giftIdx = giftItem.giftIdx,
                                    giftName = giftItem.giftName,
                                    corporationIdx = giftItem.corporationIdx,
                                    corporationName = giftItem.corporationName,
                                    corporationSido = giftItem.corporationSido,
                                    corporationSigungu = giftItem.corporationSigungu,
                                    categoryIdx = giftItem.categoryIdx,
                                    categoryName = giftItem.categoryName,
                                    giftThumbnail = giftItem.giftThumbnail,
                                    giftContentImage = giftItem.giftContentImage, // 추가된 필드
                                    giftContent = giftItem.giftContent,
                                    price = giftItem.price,
                                    createdDate = giftItem.createdDate,
                                    modifiedDate = giftItem.modifiedDate
                                )
                            }
                            // Room에 데이터를 저장
                            insertGiftDetails(giftDetails)
                        }
                    }
                } else {
                    throw Exception("Failed to fetch gifts: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GiftRepository", "Error fetching gifts: ${e.message}", e)
            }
        }
    }

    // 상품 상세 정보 API에서 가져오기 (코루틴 사용)
    suspend fun fetchGiftDetailFromApi(token: String, giftIdx: Int): GiftDetailData? {
        return withContext(Dispatchers.IO) {
            try {
                // 코루틴 기반 API 호출
                val response = RetrofitClient.giftApiService.getGiftDetail(token, giftIdx)

                if (response.isSuccessful) {
                    response.body()?.data
                } else {
                    Log.e("GiftRepository", "API 호출 실패: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("GiftRepository", "API 호출 오류: ${e.message}", e)
                null
            }
        }
    }

    suspend fun fetchRecentGiftsFromApi(token: String): List<GiftDetail>? {
        return withContext(Dispatchers.IO) {
            try {
                // 코루틴 기반 API 호출
                val response = RetrofitClient.giftApiService.getRecentGifts(token)

                if (response.isSuccessful) {
                    response.body()?.data // `data`는 List<GiftDetailData>
                } else {
                    Log.e("GiftRepository", "API 호출 실패: ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("GiftRepository", "API 호출 오류: ${e.message}", e)
                null
            }
        }
    }




    // Room에 데이터를 저장
    private suspend fun insertGiftDetails(giftDetails: List<GiftDetail>) {
        withContext(Dispatchers.IO) {
            giftDetailDao.insertGiftDetails(giftDetails)
        }
    }

    // 모든 데이터를 삭제
    suspend fun deleteAllGiftDetails() {
        withContext(Dispatchers.IO) {
            giftDetailDao.deleteAll()
        }
    }
}
