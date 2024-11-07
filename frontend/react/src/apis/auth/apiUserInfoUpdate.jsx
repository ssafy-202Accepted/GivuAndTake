import axios from "axios";
import TokenManager from "../../utils/TokenManager"; // TokenManager에서 토큰 가져오기

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 회원정보 수정 요청을 보내는 함수
export const updateUserInfo = async (userInfo) => {
  try {
    // TokenManager를 사용해 accessToken 가져오기
    const accessToken = TokenManager.getAccessToken();

    // PATCH 요청
    const response = await axios.patch(`${apiUrl}/users`, {
      name: userInfo.name,
      mobilePhone: userInfo.mobilePhone,
      landlinePhone: userInfo.landlinePhone,
      profileImageUrl: userInfo.profileImageUrl,
    }, {
      headers: {
        Authorization: `Bearer ${accessToken}`, // Authorization 헤더 추가
      },
    });

    // 응답 확인
    console.log("회원정보 수정 성공:", response.data);
    return response.data; // 응답 데이터 반환
  } catch (error) {
    console.error("회원정보 수정 실패:", error.response ? error.response.data : error.message);
    throw error; // 에러를 상위 함수로 전달
  }
};

export default { updateUserInfo };
