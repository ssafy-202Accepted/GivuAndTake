package com.project.givuandtake.feature.gift

import android.app.Application
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.givuandtake.R
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import coil.size.Scale
import com.project.givuandtake.core.data.CartItemData

import com.project.givuandtake.core.data.GiftDetail
import com.project.givuandtake.core.datastore.TokenDataStore
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.gift.GiftViewModel
import com.project.givuandtake.feature.gift.addToFavorites
//import com.project.givuandtake.feature.mypage.MyDonation.WishlistViewModel
import com.project.givuandtake.ui.theme.CustomTypography
import com.project.givuandtake.ui.theme.GivuAndTakeTheme
import com.project.givuandtake.ui.theme.gmarketSans
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.format.TextStyle


@Composable
fun GiftPage(
    navController: NavController,
    viewModel: GiftViewModel = viewModel(),
//    wishlistViewModel: WishlistViewModel = viewModel()
) {
    val context = LocalContext.current
    val allProducts by viewModel.allGiftDetails.collectAsState()
//    val wishlistItems by viewModel.wishlistItemsIds.collectAsState()
    val wishlistItems by viewModel.wishlistItems.collectAsState()

    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.deleteAllGiftDetails() // 모든 데이터를 삭제
            viewModel.fetchGiftsFromApi(accessToken) // 데이터를 다시 불러오기
            isRefreshing = false // 새로고침 완료
        }
    }

    // API에서 데이터를 불러오는 로직 추가
    LaunchedEffect(Unit) {
        viewModel.fetchGiftsFromApi(accessToken) // API 호출
        Log.d("ApiCall", "Authorization 토큰:  $accessToken")
    }

    // 장바구니 항목을 API에서 불러오기
    var cartItems by remember { mutableStateOf<List<CartItemData>>(emptyList()) }
    LaunchedEffect(Unit) {
        val result = fetchCartList(accessToken)
        if (result != null) {
            cartItems = result // API에서 불러온 장바구니 데이터로 갱신
        } else {
            Log.d("CartPage", "장바구니 데이터를 불러오는데 실패했습니다.")
        }
    }

    // 스크롤 상태를 추적하기 위한 rememberLazyListState
    val scrollState = rememberLazyListState()
    var topBarVisible by remember { mutableStateOf(true) }
    var previousScrollOffset by remember { mutableStateOf(0) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFDAEBFD)), // 페이지 기본 배경색 설정
        state = scrollState
    ) {
        item { TopBar(
            navController = navController,
            cartItemCount = cartItems.size,
            onRefresh = { isRefreshing = true} )
        }

        item {
            MiddleContent(
                navController = navController,
                products = allProducts,
                wishlistItems = wishlistItems.map { it.giftIdx.toString() }.toSet(), // wishlistItems를 Set<String>으로 변환
                onFavoriteToggle = { product ->
                    if (wishlistItems.map { it.giftIdx }.contains(product.giftIdx)) {
                        viewModel.removeFromWishlist(accessToken, wishlistItems.first { it.giftIdx == product.giftIdx }.wishIdx) // 찜 상태에서 제거
                    } else {
                        viewModel.addToWishlist(accessToken, product.giftIdx) // 찜 상태로 추가
                    }
                },
                token = accessToken
            )
        }
    }
}




@Composable
fun TopBar(
    navController: NavController,
    cartItemCount: Int,
    onRefresh: () -> Unit
) {
    // 검색어 상태를 TopBar 내부에서 관리
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDAEBFD))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "고향사랑기부몰",
                fontWeight = FontWeight.Medium,
                fontSize = 23.sp,
                style = CustomTypography.bodyLarge,
                modifier = Modifier.padding(start = 5.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onRefresh() }) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                }
                IconButton(onClick = {
                    navController.navigate("wishlist")
                }) {
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "WishList")
                }
                CartIcon(cartItemCount = cartItemCount, onCartClick = {
                    navController.navigate("cart_page")
                })
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        SearchBar(
            searchText = searchText,
            onSearchTextChange = { newText -> searchText = newText }
        )
        Spacer(modifier = Modifier.height(12.dp))
        CategoryScreen(navController)
    }
}


@Composable
fun MiddleContent(
    navController: NavController,
    products: List<GiftDetail>,
    wishlistItems: Set<String>,
    onFavoriteToggle: (GiftDetail) -> Unit,
    token: String
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // 맞춤 추천상품 텍스트
            Text(
                text = "🎁 신상품 🎁",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                style = CustomTypography.bodyLarge,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            )

            ProductGrid(
                navController = navController,
                products = products,
                wishlistItems = wishlistItems,
                onFavoriteToggle = onFavoriteToggle
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "💡 최근 구매한 지역상품 💡",
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                style = CustomTypography.bodyLarge,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)
            )
            RecentGiftPage(navController = navController, token = token )
            
        }
    }
}



@Composable
fun CartIcon(cartItemCount: Int, onCartClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.TopEnd, // 배지를 아이콘의 우측 상단에 배치
        modifier = Modifier.size(40.dp)
    ) {
        IconButton(onClick = { onCartClick() }) {
            Icon(
                imageVector = Icons.Default.ShoppingCart, // 장바구니 아이콘
                contentDescription = "Cart Icon",
                tint = Color.Black
            )
        }

        // 장바구니 아이템 개수 표시 (배지)
        if (cartItemCount > 0) {
            Box(
                modifier = Modifier
                    .size(16.dp) // 배지 크기
                    .background(Color.Red, shape = CircleShape) // 배지 모양과 색상
                    .align(Alignment.TopEnd) // 배지를 우측 상단에 배치
            ) {
                Text(
                    text = cartItemCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.Center) // 텍스트를 배지 중앙에 배치
                )
            }
        }
    }
}



@Composable
fun ProductGrid(
    navController: NavController,
    products: List<GiftDetail>,
    wishlistItems: Set<String>,
    onFavoriteToggle: (GiftDetail) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp) // 카드 간격 설정
    ) {
        // n개씩 묶어서 슬라이드 되도록 설정
        items(products.chunked(1)) { rowProducts ->
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp) // 세로 간격 설정
            ) {
                rowProducts.forEach { product ->
                    val isFavorite =  wishlistItems.contains(product.giftIdx.toString()) // 찜 상태 확인
                    ProductCard(
                        product = product,
                        isFavorite = isFavorite,
                        onFavoriteToggle = onFavoriteToggle,
                        navController = navController
                    )
                }
            }
        }
    }
}


@Composable
fun ProductCard(
    product: GiftDetail,
    navController: NavController,
    isFavorite: Boolean,
    onFavoriteToggle: (GiftDetail) -> Unit,
    modifier: Modifier = Modifier // modifier 추가
) {
    val location = "${product.corporationSido} ${product.corporationSigungu}"
//    Log.d("product", "${product}")

    Card(
        shape = RoundedCornerShape(16.dp), // 카드 모서리를 둥글게 설정
        modifier = modifier
            .padding(8.dp)
            .width(180.dp)
            .height(300.dp)
            .clickable {
                navController.navigate("gift_page_detail/${product.giftIdx}")
            }
            .border(
                width = 1.dp,
                color = Color(0x60FBAFFF),
                shape = RoundedCornerShape(12.dp)
            ),
        elevation = 4.dp,
        backgroundColor = Color(0xFFFBFAFF)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = rememberImagePainter(product.giftThumbnail),  // 실제 이미지 경로 사용
                contentDescription = "상품 이미지",
                modifier = Modifier
                    .size(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(10.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.giftName,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(1.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location Icon",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = location,
                        style = CustomTypography.bodyLarge,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(1.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.priceFormatted} ₩",
                        style = CustomTypography.bodyLarge,
                        fontSize = 17.sp,
                        color = Color.Black // 텍스트 색상 설정
                    )

                    // 찜 아이콘
                    IconButton(
                        onClick = {
//                            Log.d("ProductCard", "Favorite button clicked for product: ${product.giftName}, isFavorite: $isFavorite")
                            onFavoriteToggle(product) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color(0xFFDC143C) else Color(0xFFB3B3B3), // 아이콘 색상을 변경
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryScreen(navController: NavController) {
    FilterButtons_category { categoryIdx ->
        Log.d("category", "Navigating to category/$categoryIdx")
        navController.navigate("category/$categoryIdx") // 네비게이션 호출
    }
}

@Composable
fun FilterButtons_category(onCategorySelected: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CategoryButton(text = "지역상품권", icon = painterResource(id = R.drawable.local_product)) {
            onCategorySelected(1) // 선택된 카테고리 인덱스를 전달
        }
        CategoryButton(text = "농축산물", icon = painterResource(id = R.drawable.agriculture_product)) {
            onCategorySelected(2)
        }
        CategoryButton(text = "수산물", icon = painterResource(id = R.drawable.seafood_product)) {
            onCategorySelected(3)
        }
        CategoryButton(text = "가공식품", icon = painterResource(id = R.drawable.processed_food)) {
            onCategorySelected(4)
        }
        CategoryButton(text = "공예품", icon = painterResource(id = R.drawable.craft_product)) {
            onCategorySelected(5)
        }
    }
}

@Composable
fun CategoryButton(text: String, icon: Painter, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
    ) {

        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(65.dp),
            tint = Color.Unspecified
        )
        Text(
            text = text,
        )

    }
}






@Composable
fun FavoriteProductList(navController: NavController, favoriteProducts: List<GiftDetail>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(favoriteProducts) { product ->
            ProductCard(
                product = product,
                navController = navController,
                isFavorite = true, // 찜한 목록이므로 모두 찜 상태
                onFavoriteToggle = { /* TODO: 구현: 찜 목록에서 제거 */ }
            )
        }
    }
}



