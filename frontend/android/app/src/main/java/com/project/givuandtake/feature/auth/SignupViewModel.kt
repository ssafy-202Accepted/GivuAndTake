package com.project.givuandtake.auth

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.project.givuandtake.core.data.AddressDto
import com.project.givuandtake.core.data.SignupDto

class SignupViewModel : ViewModel() {
    // 회원가입 정보 관련 필드 (SignupDto)
    var signupInfo = mutableStateOf(SignupDto())
        private set // 외부에서 직접 값을 설정하지 못하도록

    // 주소 관련 필드 (AddressDto)
    var addressInfo = mutableStateOf(AddressDto())
        private set

    // 이름 업데이트 함수
    fun updateName(newName: String) {
        signupInfo.value = signupInfo.value.copy(name = newName)
    }

    // 이메일 업데이트 함수
    fun updateEmail(newEmail: String) {
        signupInfo.value = signupInfo.value.copy(email = newEmail)
    }

    // 성별 업데이트 함수
    fun updateGender(isMale: Boolean) {
        signupInfo.value = signupInfo.value.copy(isMale = isMale)
    }

    // 비밀번호 업데이트 함수
    fun updatePassword(newPassword: String) {
        signupInfo.value = signupInfo.value.copy(password = newPassword)
    }

    // 전화번호 업데이트 함수
    fun updateMobilePhone(newPhone: String) {
        signupInfo.value = signupInfo.value.copy(mobilePhone = newPhone)
    }

    // 성별 업데이트 함수
    fun updateIsMale(isMale: Boolean) {
        signupInfo.value = signupInfo.value.copy(isMale = isMale)
    }

    // 생년월일 업데이트 함수
    fun updateBirth(newBirth: String) {
        signupInfo.value = signupInfo.value.copy(birth = newBirth)
    }

    // 주소 업데이트 함수
    fun updateAddress(newAddress: String) {
        addressInfo.value = addressInfo.value.copy(address = newAddress)
    }

    // 상세주소 업데이트 함수
    fun updateDetailAddress(newDetailAddress: String) {
        addressInfo.value = addressInfo.value.copy(detailAddress = newDetailAddress)
    }

    fun updateJibunAddress(newjibunAddress: String) {
        addressInfo.value = addressInfo.value.copy(jibunAddress = newjibunAddress)
    }

    // 주소 이름 업데이트 함수
    fun updateAddressName(newAddressName: String) {
        addressInfo.value = addressInfo.value.copy(addressName = newAddressName)
    }

    // 추가된 메서드들
    fun updateZoneCode(newZoneCode: String) {
        addressInfo.value = addressInfo.value.copy(zoneCode = newZoneCode)
    }

    fun updateAutoRoadAddress(newAutoRoadAddress: String) {
        addressInfo.value = addressInfo.value.copy(autoRoadAddress = newAutoRoadAddress)
    }

    fun updateAutoJibunAddress(newAutoJibunAddress: String) {
        addressInfo.value = addressInfo.value.copy(autoJibunAddress = newAutoJibunAddress)
    }

    fun updateRoadAddress(newRoadAddress: String){
        addressInfo.value = addressInfo.value.copy(roadAddress = newRoadAddress)
    }

    fun updateOtherFields(
        buildingCode: String,
        buildingName: String,
        sido: String,
        sigungu: String,
        sigunguCode: String,
        roadNameCode: String,
        bcode: String,
        roadName: String,
    ) {
        // Update the additional fields in AddressInfo or however you're storing them
        addressInfo.value = addressInfo.value.copy(
            buildingCode = buildingCode,
            buildingName = buildingName,
            sido = sido,
            sigungu = sigungu,
            sigunguCode = sigunguCode,
            roadNameCode = roadNameCode,
            bcode = bcode,
            roadName = roadName,
        )
    }
}