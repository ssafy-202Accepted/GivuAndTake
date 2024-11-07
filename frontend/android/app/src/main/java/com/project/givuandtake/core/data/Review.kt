package com.project.givuandtake.core.data

import com.project.givuandtake.R

data class Review(
    val userProfileUrl: Int,     // 유저 프로필 이미지 S3 URL
    val reviewerName: String,
    val reviewCreateTime: String,  // LocalDateTime 형태로 받아올 수 있음
    val reviewText: String,        // 후기 내용
    val imageUrl: Int,          // 후기 이미지 S3 URL
)




