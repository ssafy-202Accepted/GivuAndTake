package com.project.givuandtake.feature.gift

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.project.givuandtake.R
import com.project.givuandtake.core.data.CartItemData
import com.project.givuandtake.core.data.Review
import kotlinx.coroutines.launch
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.givuandtake.core.apis.Gift.GiftReviewApi
import com.project.givuandtake.core.apis.Gift.GiftReviewLikeDeleteApi
import com.project.givuandtake.core.apis.Gift.GiftReviewLikeGetApi
import com.project.givuandtake.core.apis.Gift.GiftReviewLikePostApi
import com.project.givuandtake.core.data.Gift.GiftReviewData
import com.project.givuandtake.core.data.Gift.ReviewData
import com.project.givuandtake.core.data.GiftDetail
import com.project.givuandtake.core.data.GiftDetailData
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Response

class GiftReviewModel : ViewModel() {

    private val _reviews = mutableStateOf<List<ReviewData>>(emptyList())
    val reviews: State<List<ReviewData>> = _reviews

    private val _isLiked = mutableStateOf(false)
    val isLiked: State<Boolean> = _isLiked

    fun fetchGiftReviews(giftIdx: Int, pageNo: Int = 1, pageSize: Int = 50, isOrderLiked: Boolean = true) {
        viewModelScope.launch {
            try {
                val response: Response<GiftReviewData> = GiftReviewApi.api.getGiftReviewData(
                    giftIdx = giftIdx,
                    pageNo = pageNo,
                    pageSize = pageSize,
                    isOrderLiked = isOrderLiked
                )
                if (response.isSuccessful) {
                    val reviews = response.body()?.data
                    reviews?.let {
                        _reviews.value = it
                    }
                } else {
                    Log.e("GiftReviews", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("GiftReviews", "Exception: ${e.message}")
            }
        }
    }

    fun checkIfLiked(reviewIdx: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = GiftReviewLikeGetApi.api.GiftReviewLikeGetData(
                    reviewIdx = reviewIdx,
                    authToken = token
                )
                if (response.isSuccessful) {
                    _isLiked.value = response.body()?.data ?: false
                    Log.d("CheckIfLiked", "Review liked status: ${_isLiked.value}")
                } else {
                    Log.e("CheckIfLiked", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("CheckIfLiked", "Exception: ${e.message}")
            }
        }
    }

    fun likeReview(reviewIdx: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = GiftReviewLikePostApi.api.getGiftReviewLikePostData(
                    reviewIdx = reviewIdx,
                    authToken = token
                )
                if (response.isSuccessful) {
                    Log.d("LikeReview", "Review liked successfully")
                    // 성공 시 필요한 추가 처리(예: UI 업데이트)를 여기에 작성
                } else {
                    Log.e("LikeReview", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("LikeReview", "Exception: ${e.message}")
            }
        }
    }

    fun unlikeReview(reviewIdx: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = GiftReviewLikeDeleteApi.api.GiftReviewLikeDeleteData(
                    reviewIdx = reviewIdx,
                    authToken = token
                )
                if (response.isSuccessful) {
                    Log.d("UnlikeReview", "Review unliked successfully")
                    // You can update the UI here after unliking the review, if needed
                } else {
                    Log.e("UnlikeReview", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UnlikeReview", "Exception: ${e.message}")
            }
        }
    }
}

fun GiftDetailData.toGiftDetail(): GiftDetail {
    return GiftDetail(
        giftIdx = this.giftIdx,
        giftName = this.giftName,
        corporationIdx = this.corporationIdx,
        corporationName = this.corporationName,
        corporationSido = this.corporationSido,
        corporationSigungu = this.corporationSigungu,
        categoryIdx = this.categoryIdx,
        categoryName = this.categoryName,
        giftThumbnail = this.giftThumbnail,
        giftContentImage = this.giftContentImage,
        giftContent = this.giftContent,
        price = this.price,
        createdDate = this.createdDate,
        modifiedDate = this.modifiedDate
    )
}
object TabState {
    var selectedTabIndex by mutableStateOf(0) // Compose가 상태 변화를 감지할 수 있도록 mutableStateOf 사용
}

@Composable
fun GiftPageDetail(
    giftIdx: Int,  // 상품 ID
    cartItems: MutableState<List<CartItemData>>,  // 장바구니 항목 상태
    navController: NavController,  // 네비게이션 컨트롤러
    giftViewModel: GiftViewModel = viewModel(),  // 상품 뷰 모델
) {
    // 상품 상세 정보 상태
    val giftDetail by giftViewModel.giftDetail.collectAsState()

    val wishlistItems by giftViewModel.wishlistItems.collectAsState()

    // 리뷰 상태
    val viewModel : GiftReviewModel = viewModel()
    LaunchedEffect(Unit) {
        viewModel.fetchGiftReviews(giftIdx)
    }

    val reviews by viewModel.reviews



    // 코루틴 스코프
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    LaunchedEffect(giftIdx) {
        giftViewModel.fetchGiftDetail(token = accessToken, giftIdx = giftIdx)
        giftViewModel.fetchWishlist(accessToken)  // 추가: wishlist 데이터를 로드

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
    Log.d("cartItems", "cartItems : ${cartItems}")


    Scaffold(

        bottomBar = {
            giftDetail?.let { detail ->
                val isFavorite = wishlistItems.any { it.giftIdx == detail.giftIdx }
                Log.d("wishlistItems","wishlistItems : ${wishlistItems}")
                GiftBottomBar(
                    onAddToCart = {
                        scope.launch {
                            // 장바구니에 아이템 추가하는 API 호출
                            val success = addToCartApi(context, detail.giftIdx, 1)
                            if (success) {
                                // 장바구니 데이터를 갱신하기 위한 함수 호출
                                val updatedCartItems = fetchCartList(accessToken)
                                // cartItems에 새로운 장바구니 데이터를 추가
                                if (updatedCartItems != null) {
                                    Log.d("cartItems","cartItems : ${cartItems}")
                                    Log.d("cartItems","updatedCartItems : ${updatedCartItems}")
                                    cartItems = updatedCartItems // 리스트를 합치는 방식으로 갱신
                                }
                                Log.d("GiftPageDetail", "상품이 장바구니에 성공적으로 추가되었습니다.")
                            } else {
                                Log.e("GiftPageDetail", "장바구니 추가에 실패했습니다.")
                            }
                        }
                    },
                    onFavoriteToggle = { product ->
                        if (wishlistItems.map { it.giftIdx }.contains(product.giftIdx)) {
                            giftViewModel.removeFromWishlist(accessToken, wishlistItems.first { it.giftIdx == product.giftIdx }.wishIdx) // 찜 상태에서 제거
                        } else {
                            giftViewModel.addToWishlist(accessToken, product.giftIdx) // 찜 상태로 추가
                        }
                    },
                    navController = navController,
                    giftDetail = detail,
                    isFavorite = isFavorite,
                    cartItems = cartItems
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            giftDetail?.let { detail ->
                // 상품 이미지 및 상단 바
                item {
                    Box {
                        Image(
                            painter = rememberImagePainter(detail.giftThumbnail),
                            contentDescription = "상품 이미지",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            contentScale = ContentScale.Crop
                        )
                        // 검은색 반투명 그라디언트 효과
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.5f)
                                        ),
                                        startY = 250f,
                                        endY = 0f
                                    )
                                )
                        )
                        GiftTopBar(
                            cartItemCount = cartItems.size,
                            onCartClick = {
                                navController.navigate("cart_page")
                            },
                            navController = navController
                        )
                    }
                }

                // 상품 정보
                item {
                    GiftInformation(giftDetail = detail)
                }

                // 탭 UI
                item {
                    GiftTabs(
                        selectedTabIndex = TabState.selectedTabIndex,
                        onTabSelected = { TabState.selectedTabIndex = it }
                    )
                }

                // 선택된 탭에 따른 내용 표시
                item {
                    when (TabState.selectedTabIndex) {
                        0 -> ProductIntroduction(giftDetail = detail)
                        1 -> ProductReview(reviews = reviews, viewModel = viewModel, token = accessToken)  // 리뷰 탭
                        2 -> RelatedRecommendations(navController = navController, location = detail.location)  // 연관 추천 탭
                    }
                }
            } ?: run {
                item {
                    Text("Loading...", Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun GiftBottomBar(
    onAddToCart: () -> Unit,
    navController: NavController,
    isFavorite: Boolean,
    onFavoriteToggle: (GiftDetail) -> Unit,
    giftDetail: GiftDetailData,
    cartItems: List<CartItemData>, // 장바구니 상태를 전달받음
) {
    var showAlert by remember { mutableStateOf(false) } // 경고창 상태 관리
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 찜하기 버튼
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        onFavoriteToggle(giftDetail.toGiftDetail())
                    },
                    modifier = Modifier.size(40.dp) // 아이콘 크기 조절
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else Color(0xFFB3B3B3), // 아이콘 색상 설정
                        modifier = Modifier.size(32.dp) // 아이콘 크기 설정

                    )
                }
                Text(
                    text = "찜",
                    fontSize = 12.sp, // 글자 크기 조절
                    fontWeight = FontWeight.Medium,
                    color = Color.Red
                )
            }

            // 장바구니 버튼
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        // 이미 장바구니에 있는지 확인
                        val isAlreadyInCart = cartItems.any { it.giftIdx == giftDetail.giftIdx }
                        if (isAlreadyInCart) {
                            showAlert = true // 경고창 표시
                        } else {
                            onAddToCart() // 장바구니에 추가
                        }
                    },
                    modifier = Modifier.size(40.dp) // 아이콘 크기 조절
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart, // 장바구니 아이콘
                        contentDescription = "Cart Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp) // 아이콘 크기 설정
                    )
                }
                Text(
                    text = "장바구니",
                    fontSize = 12.sp, // 글자 크기 조절
                    fontWeight = FontWeight.Medium
                )
            }

            // 기부하기 버튼
            Button(
                onClick = {
                    navController.navigate(
                        "payment_page_gift?name=${Uri.encode(giftDetail.giftName)}&location=${Uri.encode(giftDetail.location)}&price=${giftDetail.price}&quantity=1&thumbnailUrl=${Uri.encode(giftDetail.giftThumbnail)}&giftIdx=${giftDetail.giftIdx}"
                    )
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4)),
                modifier = Modifier
                    .width(270.dp) // 버튼을 화면 가로 크기만큼 길게
                    .padding(horizontal = 16.dp) // 좌우 여백을 추가
            ) {
                Text("기부하기", color = Color.White)
            }

        }
    }
    // 경고창(AlertDialog)
    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false }, // 닫기 버튼 클릭 시
            title = {
                Text(text = "장바구니 알림")
            },
            text = {
                Text(text = "이미 장바구니에 있는 상품입니다!")
            },
            confirmButton = {
                Button(onClick = { showAlert = false }) {
                    Text("확인")
                }
            }
        )
    }



}




@Composable
fun GiftInformation(giftDetail: GiftDetailData) {
    Spacer(modifier = Modifier.height(16.dp))

    // 주소 정보
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = "Location icon",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = giftDetail.location, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))

    }

    Spacer(modifier = Modifier.height(8.dp))

    Divider(
        color = Color(0xFFDAEBFD),
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = giftDetail.giftName,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        modifier = Modifier.padding(horizontal = 10.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "${giftDetail.priceFormatted} 원",
        fontSize = 25.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        modifier = Modifier.padding(horizontal = 10.dp)
    )
}

@Composable
fun GiftTopBar(
    cartItemCount: Int, // 장바구니 아이템 수
    onCartClick: () -> Unit, // 장바구니 아이콘 클릭 처리
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    )   {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable { navController.popBackStack() }
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier.size(40.dp)
        ) {
            IconButton(onClick = { onCartClick() }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart, // 장바구니 아이콘
                    contentDescription = "Cart Icon",
                    tint = Color.Black
                )
            }

            Log.d("cartItemCount","cartItemCount : ${cartItemCount}")
            if (cartItemCount > 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color.Red, shape = CircleShape)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = cartItemCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun GiftTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("상품 소개", "상품 후기", "연관 추천")
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = Color.White,
        contentColor = Color(0xFFDAEBFD),
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEachIndexed { index, tabTitle ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    scope.launch {
                        onTabSelected(index)
                    }
                },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Gray,
                text = { Text(tabTitle) }
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun ProductIntroduction(giftDetail: GiftDetailData) {
    Column(modifier = Modifier.padding(16.dp)) {
        val thumbnailUrl = giftDetail.giftContentImage
        val description = giftDetail.giftContent

        if (thumbnailUrl != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 8000.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(data = thumbnailUrl),
                        contentDescription = "상품 썸네일",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }

//            Spacer(modifier = Modifier.height(16.dp))
        }

        // 상품 설명
        if (description != null) {
            Text(
                text = description,
                fontSize = 16.sp
            )
        } else {
            Text(
                text = "",
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun ProductReview(reviews: List<ReviewData>, viewModel: GiftReviewModel, token: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        reviews.forEach { review ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = rememberImagePainter(data = review.userProfileImage),
                        contentDescription = "User profile",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color(0xFFDAEBFD), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(review.userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(review.modifiedDate.substringBefore("T"), fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                Log.d("LikeReview", "$token, ${review.reviewIdx}")
                                viewModel.likeReview(review.reviewIdx, token)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.good),
                            contentDescription = "Example Icon",
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .size(16.dp)
                        )
                        Text(
                            text = "${review.likedCount}",
                            fontSize = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 리뷰 이미지
                Box(
                    modifier = Modifier
                        .fillMaxWidth() // 가로 크기를 채움
                        .height(200.dp) // 높이를 명시적으로 설정
                        .border(
                            width = 1.dp,
                            color = Color(0xFFDAEBFD),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberImagePainter(data = review.reviewImage),
                        contentDescription = "Review image",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(review.reviewContent, fontSize = 20.sp, modifier = Modifier.padding(horizontal =  10.dp))
                Spacer(modifier = Modifier.height(20.dp))

                Divider(
                    color = Color(0xFFDAEBFD),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}







