package com.project.givuandtake.core.data

data class SignupDto(
    var name: String = "",
    var isMale: Boolean = false,
    var birth: String = "",
    var email: String = "",
    var password: String = "",
    var mobilePhone: String = "",
    var landlinePhone: String? = null,
    var profileImageUrl: String? = null,
    var roles: String = "ROLE_CLIENT", // 고정된 값
    var isSocial: Boolean = false,
    var socialType: String? = null,
    var socialSerialNum: String? = null
)
