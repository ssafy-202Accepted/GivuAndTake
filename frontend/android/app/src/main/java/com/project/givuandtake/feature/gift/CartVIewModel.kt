package com.project.givuandtake.feature.gift

import androidx.lifecycle.ViewModel
import com.project.givuandtake.core.data.CartItemData

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItemData>>(emptyList())
    val cartItems: StateFlow<List<CartItemData>> get() = _cartItems

    fun setCartItems(items: List<CartItemData>) {
        _cartItems.value = items
    }
}

