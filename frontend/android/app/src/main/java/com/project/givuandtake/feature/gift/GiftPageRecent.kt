package com.project.givuandtake.feature.gift

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.core.data.GiftDetailData

@Composable
fun RecentGiftPage(
    navController: NavController,
    viewModel: GiftViewModel = viewModel(),
    token: String
) {
    val recentGifts by viewModel.recentGifts.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState() // 찜 목록 가져오기

    // wishlistItems를 Set<String>으로 변환 (giftIdx를 string으로 변환)
    val wishlistItemsSet = wishlistItems.map { it.giftIdx.toString() }.toSet()

    LaunchedEffect(Unit) {
        viewModel.fetchRecentGifts(token) // API로 최근 상품 불러오기
        viewModel.fetchWishlist(token) // API로 찜 목록 불러오기
    }

    ProductGrid(
        navController = navController,
        products = recentGifts,
        wishlistItems = wishlistItemsSet, // 찜한 상품의 giftIdx를 전달
        onFavoriteToggle = { gift ->
            viewModel.addToWishlist(token, gift.giftIdx) // 찜 추가/삭제
        }
    )
}




