//package com.project.givuandtake.feature.navigation
//
////import GiftPageDetail
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.platform.LocalContext
//import androidx.navigation.NavBackStackEntry
//import androidx.navigation.NavController
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.NavType
//import androidx.navigation.compose.composable
//import androidx.navigation.navArgument
//import com.project.givuandtake.core.data.CartItem
//import com.project.givuandtake.core.data.GiftDetail
//import com.project.givuandtake.feature.gift.addToCart
//import com.project.givuandtake.feature.payment.PaymentSuccessPage
//import kotlinx.coroutines.launch
//
//
//fun NavGraphBuilder.addGiftPageDetailRoute(navController: NavController, cartItems: MutableState<List<CartItem>>) {
//    composable(
//        route = "gift_page_detail/{id}/{name}/{price}/{imageUrl}/{location}",
//        arguments = listOf(
//            navArgument("id") { type = NavType.IntType },
//            navArgument("name") { type = NavType.StringType },
//            navArgument("price") { type = NavType.IntType },
//            navArgument("imageUrl") { type = NavType.StringType },
//            navArgument("location") { type = NavType.StringType }
//        )
//    ) { backStackEntry ->
//        // 전달된 인자를 바탕으로 GiftDetail 객체 생성
////        val giftDetail = GiftDetail(
////            id = backStackEntry.arguments?.getInt("id") ?: 0,
////            name = backStackEntry.arguments?.getString("name") ?: "",
////            price = backStackEntry.arguments?.getInt("price") ?: 0,
////            imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: "",
////            location = backStackEntry.arguments?.getString("location") ?: ""
////        )
//
//        // Composable에서 필요한 Context 및 CoroutineScope 가져오기
//        val context = LocalContext.current
//        val scope = rememberCoroutineScope()
//
////        GiftPageDetail(
////            giftDetail = giftDetail, // GiftDetail 객체 전달
////            cartItems = cartItems,   // MutableState<List<CartItem>>를 직접 전달
////            onAddToCart = {
////                scope.launch {
////                    // addToCart 함수를 호출하여 장바구니에 추가
////                    addToCart(context, giftDetail, 1) // 수량 1로 설정하여 장바구니에 추가
////                }
////            },
////            navController = navController
////        )
//    }
//}
//
////fun NavGraphBuilder.addPaymentSuccessPage(navController: NavController) {
////    composable("payment_success") {
////        PaymentSuccessPage(navController = navController)
////    }
////}
//
//
//
//
