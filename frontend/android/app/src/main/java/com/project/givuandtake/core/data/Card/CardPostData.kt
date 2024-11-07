package com.project.givuandtake.core.data.Card

data class CardPostData(
    val cardCompany: String,
    val cardNumber: String,
    val cardCVC: String,
    val cardExpiredDate: String,
    val cardPassword: String,
    val isRepresentative: Boolean
)
