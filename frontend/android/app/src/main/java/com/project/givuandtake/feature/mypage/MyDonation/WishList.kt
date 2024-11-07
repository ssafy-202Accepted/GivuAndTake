package com.project.givuandtake.feature.mypage.MyDonation

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.project.givuandtake.R
import com.project.givuandtake.core.data.CartItemData
import com.project.givuandtake.core.data.Gift.WishlistItem
import com.project.givuandtake.core.data.GiftDetail
import com.project.givuandtake.core.data.GiftDetailData
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.gift.CartIcon
import com.project.givuandtake.feature.gift.GiftViewModel
import com.project.givuandtake.feature.gift.addToCartApi
import com.project.givuandtake.feature.gift.fetchCartList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// Price formatting helper function
fun formatPrice(price: Int): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
    return numberFormat.format(price)
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Wishlist(
    navController: NavController,
    viewModel: GiftViewModel = viewModel()
) {
    val wishlistItems by viewModel.wishlistItems.collectAsState()
    var cartItems by remember { mutableStateOf<List<CartItemData>>(emptyList()) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<GiftDetailData?>(null) }

    // 찜한 상품의 상세 정보를 불러오는 로직 추가
    LaunchedEffect(Unit) {
        viewModel.fetchWishlist(accessToken) // 찜 목록 가져오기
        val result = fetchCartList(accessToken)
        if (result != null) {
            cartItems = result
        } else {
            Toast.makeText(context, "장바구니 데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Custom TopBar
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                Text(
                    text = "찜 목록",
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
                Box(
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    CartIcon(cartItemCount = cartItems.size, onCartClick = {
                        navController.navigate("cart_page")
                    })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Wishlist content
            if (wishlistItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "찜 목록에 아이템이 없습니다.", fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "총 ${wishlistItems.size}개",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // 각 wishlistItem의 상세 정보를 불러와 레이아웃에 표시
                    items(wishlistItems) { wishlistItem ->
                        // giftDetail 값은 remember가 필요하지 않으므로 바로 collectAsState로 사용
                        val giftDetail by viewModel.getGiftDetailForItem(wishlistItem.giftIdx).collectAsState(initial = null)

                        LaunchedEffect(wishlistItem.giftIdx) {
                            viewModel.fetchGiftDetails(accessToken, wishlistItem.giftIdx)
                        }

                        // giftDetail이 로드되면 WishListItem으로 표시
                        if (giftDetail != null) {
                            WishListItem(
                                product = giftDetail!!,
                                onRemove = { viewModel.removeFromWishlist(accessToken, wishlistItem.wishIdx) },
                                onAddToCartPopUp = {
                                    selectedProduct = giftDetail
                                    showBottomSheet = true
                                },
                                cartItems = cartItems, // 추가된 인자
                                coroutineScope = coroutineScope, // 추가된 인자
                                accessToken = accessToken // 추가된 인자
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        // Add to Cart Bottom Sheet
        if (showBottomSheet && selectedProduct != null) {
            AddToCartBottomSheet(
                product = selectedProduct!!,
                showBottomSheet = true,
                onDismiss = { showBottomSheet = false },
                onAddToCart = { quantity ->
                    coroutineScope.launch {
                        val success = addToCartApi(context, selectedProduct!!.giftIdx, quantity)
                        if (success) {
                            Toast.makeText(context, "장바구니에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                            showBottomSheet = false
                        } else {
                            Toast.makeText(context, "장바구니 추가에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun WishListItem(
    product: GiftDetailData,
    onRemove: (GiftDetailData) -> Unit,
    onAddToCartPopUp: (GiftDetailData) -> Unit,
    cartItems: List<CartItemData>,
    coroutineScope: CoroutineScope,
    accessToken: String
) {
    val context = LocalContext.current // 함수 내부에서 LocalContext 사용
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp),
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 상품 이미지
            Image(
                painter = rememberImagePainter(data = product.giftThumbnail),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Transparent),
                contentScale = ContentScale.FillBounds
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 상품 정보
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically // 아이콘과 텍스트를 세로로 중앙 정렬
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = "Icon",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = product.location, fontSize = 13.sp)
                    }

                    IconButton(
                        onClick = { onRemove(product) },
                        modifier = Modifier
//                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Remove",
                            tint = Color(0xFFFF6F6F)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = product.giftName, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "${formatPrice(product.price)}원", fontSize = 17.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(70.dp)
                            .clickable { onAddToCartPopUp(product) }
                            .border(1.dp, Color(0xFFA093DE), shape = RoundedCornerShape(8.dp)),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Add to Cart",
                                tint = Color(0xFFA093DE),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("담기")
                        }
                    }
                }
            }
        }


    }
}

@Composable
fun AddToCartBottomSheet(
    product: GiftDetailData,
    showBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x20000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.Gray, shape = RoundedCornerShape(2.dp))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = rememberImagePainter(data = product.giftThumbnail),
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = product.giftName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quantity selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(0.dp))
                Row {
                    Text(text = "${formatPrice(product.price)}원", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(60.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (quantity > 1) quantity-- }) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "수량 감소")
                    }
                    Text(text = "$quantity", fontSize = 18.sp) // 기본 수량 1
                    IconButton(onClick = { quantity++ }) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "수량 증가")
                    }
                }
                Spacer(modifier = Modifier.width(0.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onAddToCart(quantity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD9E3FF)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "${formatPrice(product.price * quantity)}원 장바구니 담기", fontSize = 16.sp)
            }
        }
    }
}
