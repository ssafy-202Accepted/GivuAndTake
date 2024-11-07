package com.project.givuandtake.feature.gift

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.project.givuandtake.core.data.GiftDetail
import com.project.givuandtake.ui.theme.CustomTypography


@Composable
fun GiftListScreen(
    categoryIdx: Int,
    navController: NavController,
    viewModel: GiftViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val categoryGiftsState = viewModel.categoryGiftDetails.collectAsState()
    val categoryGifts = categoryGiftsState.value

    val loadingState = viewModel.loading.collectAsState()
    val loading = loadingState.value

    val errorState = viewModel.error.collectAsState()
    val error = errorState.value


    // 카테고리 인덱스에 따른 카테고리 이름 매핑
    val categoryName = when (categoryIdx) {
        1 -> "🎫지역상품권🎫"
        2 -> "🌾농축산물🌾"
        3 -> "🐟수산물🐟"
        4 -> "🥫가공식품🥫"
        5 -> "🧵공예품🧵"
        else -> "알 수 없는 카테고리"
    }

    LaunchedEffect(categoryIdx) {
        viewModel.fetchGiftsByCategory(categoryIdx)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDAEBFD)) // 상단 파란색 배경
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically, // Row 내 수직 정렬
            horizontalArrangement = Arrangement.SpaceBetween // 좌우 정렬
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
                    .weight(0.3f)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            Text(
                text = categoryName,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(0.5f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    navController.navigate("wishlist")
                }) {
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "WishList")
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .padding(horizontal = 10.dp)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text("Error: $error")
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2), // 2열 설정
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp), // 세로 간격 설정
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // 가로 간격 설정
                    ) {
                        items(categoryGifts) { gift ->
                            GiftItem(gift = gift, navController = navController) // 상품 아이템
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun GiftItem(gift: GiftDetail, navController: NavController) {
    val location = "${gift.corporationSido} ${gift.corporationSigungu}"

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
            .height(300.dp)
            .clickable {
                navController.navigate("gift_page_detail/${gift.giftIdx}")
            }
            .border(
                width = 1.dp,
                color = Color(0x60FBAFFF),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = 4.dp,
        backgroundColor = Color(0xFFFBFAFF)

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(10.dp)
            ) {
                Image(
                    painter = rememberImagePainter(gift.giftThumbnail),
                    contentDescription = "상품 이미지",
                    modifier = Modifier.size(180.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = gift.giftName,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     Text(
                         text = "${gift.priceFormatted} ₩",
                         style = CustomTypography.bodyLarge,
                         fontSize = 17.sp,
                         color = Color.Black
                     )
                }
            }
        }
    }
}