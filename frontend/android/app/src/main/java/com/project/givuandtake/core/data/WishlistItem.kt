package com.project.givuandtake.core.data

// 사용안함
data class WishlistItem(
    val id: Int,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val location: String,
    // 위시리스트 아이템에 필요한 추가 속성들
    val addedDate: Long = System.currentTimeMillis()
)

