package com.project.givuandtake.core.data

// 데이터를 감싸는 클래스를 정의합니다.
data class SignUpRequest(
    val signUpDto: SignupDto,
    val addressAddDto: AddressDto
)
