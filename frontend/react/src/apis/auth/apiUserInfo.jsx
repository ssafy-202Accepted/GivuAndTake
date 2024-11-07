import axios from "axios";
import TokenManager from "../../utils/TokenManager"; // TokenManager 추가

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 회원정보 조회 요청을 보내는 함수
export const getUserInfo = async () => {
  try {
    // TokenManager에서 액세스 토큰 가져오기
    const accessToken = TokenManager.getAccessToken();

    // API 요청
    const response = await axios.get(`${apiUrl}/users`, {
      headers: {
        Authorization: `Bearer ${accessToken}`, // Authorization 헤더 추가
      },
    });

    // 응답 확인
    if (response.data.success) {
      const userInfo = {
        email: response.data.data.email,
        name: response.data.data.name,
        mobilePhone: response.data.data.mobilePhone,
        landlinePhone: response.data.data.landlinePhone,
        sido: response.data.data.sido,
        sigungu: response.data.data.sigungu,
        profileImageUrl: response.data.data.profileImageUrl,
      };

      console.log("회원정보 조회 성공:", userInfo);
      return userInfo; // 회원정보 데이터 반환
    } else {
      throw new Error("회원정보 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("회원정보 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { getUserInfo };
