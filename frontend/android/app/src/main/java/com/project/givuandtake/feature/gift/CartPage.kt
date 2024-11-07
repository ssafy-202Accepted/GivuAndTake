package com.project.givuandtake.feature.gift

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.project.givuandtake.R
import com.project.givuandtake.core.data.CartItemData
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun CartPage(navController: NavController, context: Context, cartViewModel: CartViewModel = viewModel()) {
    val coroutineScope = rememberCoroutineScope()
    var cartItems by remember { mutableStateOf(emptyList<CartItemData>()) }
    val scaffoldState = rememberScaffoldState()
    var selectedCartIdxList by remember { mutableStateOf(setOf<Int>()) } // 선택된 항목 ID 목록

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val token = TokenManager.getAccessToken(context)
            if (token != null) {
                val result = fetchCartList("Bearer $token")
                if (result != null) {
                    cartItems = result.map { cartItemData ->
                        CartItemData(
                            cartIdx = cartItemData.cartIdx,
                            giftIdx = cartItemData.giftIdx,
                            giftName = cartItemData.giftName,
                            giftThumbnail = cartItemData.giftThumbnail,
                            userIdx = cartItemData.userIdx,
                            price = cartItemData.price,
                            amount = cartItemData.amount,
                            sido = cartItemData.sido,
                            sigungu = cartItemData.sigungu
                        )
                    }
                    cartViewModel.setCartItems(cartItems)
                    Log.d("cart", "cartItemData : $cartItems")
                } else {
                    scaffoldState.snackbarHostState.showSnackbar("Failed to load cart items")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CartTopBar(navController = navController, selectedCartIdxList = selectedCartIdxList) // 뒤로가기 추가
        },
        bottomBar = { CartBottomBar(navController, cartItems) },
        scaffoldState = scaffoldState
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(cartItems) { cartItem ->
                CartItemView(
                    cartItem = cartItem,
                    selectedCartIdxList = selectedCartIdxList,
                    onQuantityChange = { item, newQuantity ->
                        coroutineScope.launch {
                            val result = updateCartItemQuantity(context, item.cartIdx, newQuantity)
                            if (result) {
                                val updatedItems = cartItems.map {
                                    if (it == item) it.copy(amount = newQuantity) else it
                                }
                                cartItems = updatedItems
                                cartViewModel.setCartItems(updatedItems)
                            }
                        }
                    },
                    onSelectItem = { selectedIdx ->
                        selectedCartIdxList = if (selectedIdx in selectedCartIdxList) {
                            selectedCartIdxList - selectedIdx
                        } else {
                            selectedCartIdxList + selectedIdx
                        }
                    },
                    onDeleteItem = { item ->
                        coroutineScope.launch {
                            val result = deleteCartItem(context, item.cartIdx)
                            if (result) {
                                cartItems = cartItems.filter { it != item }
                                cartViewModel.setCartItems(cartItems)
                            }
                        }
                    },
                    context = context
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CartItemView(
    cartItem: CartItemData,
    selectedCartIdxList: Set<Int>, // 선택된 항목 ID 목록
    onQuantityChange: (CartItemData, Int) -> Unit,
    onSelectItem: (Int) -> Unit,
    onDeleteItem: (CartItemData) -> Unit,
    context: Context
) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 4.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            // 삭제 버튼을 오른쪽 상단에 배치
            IconButton(
                onClick = { onDeleteItem(cartItem) },
                modifier = Modifier
                    .align(Alignment.TopEnd) // 오른쪽 상단에 배치
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_delete_24),
                    contentDescription = "Delete Item",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = rememberImagePainter(data = cartItem.giftThumbnail),
                        contentDescription = "상품 이미지",
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = cartItem.giftName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "수량: ${cartItem.amount}", fontSize = 14.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_location_on_24),
                                contentDescription = "원산지",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = cartItem.location ?: "위치 정보 없음",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (cartItem.amount > 1) {
                                coroutineScope.launch {
                                    val result = updateCartItemQuantity(context, cartItem.cartIdx, cartItem.amount - 1)
                                    if (result) {
                                        onQuantityChange(cartItem, cartItem.amount - 1)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
                    ) {
                        Text("-", color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${cartItem.amount}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val result = updateCartItemQuantity(context, cartItem.cartIdx, cartItem.amount + 1)
                                if (result) {
                                    onQuantityChange(cartItem, cartItem.amount + 1)
                                }
                            }
                        },
                        modifier = Modifier.size(40.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
                    ) {
                        Text("+", color = Color.White, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Text(text = "₩${String.format("%,d", cartItem.price * cartItem.amount)}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CartTopBar(navController: NavController, selectedCartIdxList: Set<Int>) {
    TopAppBar(
        title = { Text("장바구니", color = Color.Black) },
        backgroundColor = Color(0xFFDAEBFD),
        navigationIcon = { // 뒤로가기 버튼 추가
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, // 뒤로 가기 화살표 아이콘
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        },
    )
}

@Composable
fun CartBottomBar(navController: NavController, cartItems: List<CartItemData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val totalAmount = cartItems.sumOf { it.price * it.amount }

        Text(text = "결제 총 금액", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = "₩${String.format("%,d", totalAmount)}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4099E9))

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (cartItems.isNotEmpty()) {
                    val firstItem = cartItems[0]
                    val name = "${firstItem.giftName}..포함 ${cartItems.size}개"
                    navController.navigate(
                        "payment_page_gift?name=${name}&location=${firstItem.location}&price=${totalAmount}&quantity=${firstItem.amount}&thumbnailUrl=${firstItem.giftThumbnail}&giftIdx=${firstItem.giftIdx}"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
        ) {
            Text(text = "결제하기", color = Color.White)
        }
    }
}