package com.project.givuandtake.core.data

data class PaymentInfo(
    val selectedGivu: String,   // 기부 종류
    val selectedMethod: String, // 결제 수단
    val amount: Int,            // 결제 금액
    val name: String,           // 상품 이름
    val location: String,       // 상품 위치
    val quantity: Int           // 상품 수량
)

