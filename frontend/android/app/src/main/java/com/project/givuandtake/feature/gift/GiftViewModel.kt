package com.project.givuandtake.feature.gift

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.project.givuandtake.core.apis.RetrofitClient
import com.project.givuandtake.core.data.Gift.WishlistItem
import com.project.givuandtake.core.data.GiftDetail
import com.project.givuandtake.core.data.GiftDetailData
import com.project.givuandtake.core.datastore.GiftRepository
import com.project.givuandtake.core.datastore.WishlistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class GiftViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val giftRepository = GiftRepository(context)
    private val wishlistRepository = WishlistRepository

    // 모든 상품 목록
    open val allGiftDetails: StateFlow<List<GiftDetail>> = giftRepository.getAllGiftDetails()
        .map { it.reversed() } // 리스트를 역순으로 변환
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 모든 데이터를 삭제하는 함수
    fun deleteAllGiftDetails() {
        viewModelScope.launch {
            giftRepository.deleteAllGiftDetails()
        }
    }


//    // 찜한 상품 ID 목록
//    val wishlistItemsIds: StateFlow<Set<String>> = wishlistRepository.getWishlist(context)
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())
//
//    // 찜한 상품 목록
//    val wishlistItems: StateFlow<List<GiftDetail>> = combine(
//        allGiftDetails,
//        wishlistItemsIds
//    ) { giftDetails, favoriteIds ->
//        giftDetails.filter { gift ->
//            favoriteIds.contains(gift.giftIdx.toString())
//        }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
// 찜한 상품 목록
//    private val _wishlistItems = MutableStateFlow<Set<String>>(emptySet())
//    val wishlistItems: StateFlow<Set<String>> = _wishlistItems
//
//    // 찜한 상품 목록과 전체 상품을 결합하여 실제 상품 목록 반환
//    val favoriteGifts: StateFlow<List<GiftDetail>> = combine(
//        allGiftDetails, wishlistItems
//    ) { gifts, favorites ->
//        gifts.filter { gift -> favorites.contains(gift.giftIdx.toString()) }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    // 찜 기능 처리
//    fun toggleWishlistItem(gift: GiftDetail) {
//        viewModelScope.launch {
//            val currentWishlist = _wishlistItems.value.toMutableSet()
//            if (currentWishlist.contains(gift.giftIdx.toString())) {
//                currentWishlist.remove(gift.giftIdx.toString())
//            } else {
//                currentWishlist.add(gift.giftIdx.toString())
//            }
//            _wishlistItems.value = currentWishlist
//            Log.d("wishlist", "Updated wishlist items: ${_wishlistItems.value}")
//
//        }
//    }

    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> get() = _wishlistItems

    // 찜 목록을 API에서 가져오는 메서드
    fun fetchWishlist(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.giftApiService.getWishlist(token)
                if (response.isSuccessful) {
                    val wishlistResponse = response.body()
                    if (wishlistResponse != null && wishlistResponse.success) {
                        _wishlistItems.value = wishlistResponse.data // data 필드에서 가져옴
                        Log.d("wishlist", "wishlistItems : ${_wishlistItems.value}")
                    } else {
                        Log.e("GiftViewModel", "Error fetching wishlist: Data is null or unsuccessful")
                    }
                } else {
                    Log.e("GiftViewModel", "Error fetching wishlist: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GiftViewModel", "Unknown error: ${e.message}")
            }
        }
    }


    // 찜 추가 API 호출
    fun addToWishlist(token: String, giftIdx: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = mapOf("giftIdx" to giftIdx)
                val response = RetrofitClient.giftApiService.addToWishlist(token, body)
                if (response.isSuccessful) {
                    fetchWishlist(token) // 추가 후 최신 찜 목록 다시 불러옴
                } else {
                    Log.e("GiftViewModel", "Error adding to wishlist: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GiftViewModel", "Unknown error: ${e.message}")
            }
        }
    }

    // 찜 삭제 API 호출
    fun removeFromWishlist(token: String, wishIdx: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.giftApiService.removeFromWishlist(token, wishIdx)
                if (response.isSuccessful) {
                    fetchWishlist(token) // 삭제 후 최신 찜 목록 다시 불러옴
                } else {
                    Log.e("GiftViewModel", "Error removing from wishlist: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GiftViewModel", "Unknown error: ${e.message}")
            }
        }
    }

//    fun fetchGiftDetailsForWishlist(token: String, wishlistItems: List<WishlistItem>) {
//        viewModelScope.launch {
//            wishlistItems.forEach { wishlistItem ->
//                fetchGiftDetail(token, wishlistItem.giftIdx)
//            }
//        }
//    }

    private val _giftDetails = MutableStateFlow<Map<Int, GiftDetailData>>(emptyMap())
    val giftDetails: StateFlow<Map<Int, GiftDetailData>> get() = _giftDetails

    // 상품 상세 정보를 개별적으로 가져오는 함수
    fun fetchGiftDetails(token: String, giftIdx: Int) {
        viewModelScope.launch {
            try {
                val detail = withContext(Dispatchers.IO) {
                    giftRepository.fetchGiftDetailFromApi(token, giftIdx)
                }
                if (detail != null) {
                    _giftDetails.update { it + (giftIdx to detail) }  // giftIdx에 맞는 상세 정보 저장
                } else {
                    Log.e("GiftViewModel", "API 호출 실패 또는 데이터 없음")
                }
            } catch (e: Exception) {
                Log.e("GiftViewModel", "API 호출 오류: ${e.message}")
            }
        }
    }


    // 특정 상품(giftIdx)의 상세 정보를 반환하는 함수
    fun getGiftDetailForItem(giftIdx: Int): Flow<GiftDetailData?> {
        return giftDetails.map { it[giftIdx] }  // giftIdx에 맞는 상세 정보를 반환
    }

    // 상품 상세 정보 관리 (MutableStateFlow로 관리)
    private val _giftDetail = MutableStateFlow<GiftDetailData?>(null)
    val giftDetail: StateFlow<GiftDetailData?> get() = _giftDetail.asStateFlow()

    // 카테고리별 상품 목록 관리 (MutableStateFlow로 관리)
    private val _categoryGiftDetails = MutableStateFlow<List<GiftDetail>>(emptyList())
    val categoryGiftDetails: StateFlow<List<GiftDetail>> get() = _categoryGiftDetails.asStateFlow()

    // API에서 상품 데이터를 불러와 Room에 저장하는 메서드
    fun fetchGiftsFromApi(token: String) {
        viewModelScope.launch {
            try {
                // withContext로 명시적으로 IO 작업을 처리
                withContext(Dispatchers.IO) {
                    giftRepository.fetchGiftsFromApi(token)
                }
            } catch (e: Exception) {
                Log.e("GiftViewModel", "Error fetching gifts: ${e.message}", e)
            }
        }
    }

    // 카테고리별 상품 목록을 API에서 불러오는 메서드
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchGiftsByCategory(categoryIdx: Int) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.giftApiService.getGiftsByCategory(categoryIdx)
                }
                if (response.success) {
                    _categoryGiftDetails.value = response.data
                } else {
                    _error.value = "Error fetching category gifts"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // 상품 상세 정보 가져오기
    fun fetchGiftDetail(token: String, giftIdx: Int) {
        viewModelScope.launch {
            try {
                // withContext로 명시적으로 IO 작업을 처리
                val detail = withContext(Dispatchers.IO) {
                    giftRepository.fetchGiftDetailFromApi(token, giftIdx)
                }
                if (detail != null) {
                    _giftDetail.value = detail
                } else {
                    Log.e("GiftViewModel", "API 호출 실패 또는 데이터 없음")
                }
            } catch (e: Exception) {
                Log.e("GiftViewModel", "API 호출 오류: ${e.message}")
            }
        }
    }

    private val _recentGifts = MutableStateFlow<List<GiftDetail>>(emptyList())
    val recentGifts: StateFlow<List<GiftDetail>> = _recentGifts

    fun fetchRecentGifts(token: String) {
        viewModelScope.launch {
            val gifts = giftRepository.fetchRecentGiftsFromApi(token)
            if (gifts != null) {
                _recentGifts.value = gifts
            } else {
                Log.e("GiftViewModel", "API 호출 실패 또는 데이터 없음")
            }
        }
    }



    // 장바구니 아이템 개수 (기본 값 0)
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> get() = _cartItemCount.asStateFlow()

    // 장바구니 아이템 개수 업데이트
    fun updateCartItemCount(newCount: Int) {
        _cartItemCount.value = newCount
    }
}

