import axios from "axios";
import TokenManager from "../../utils/TokenManager"; // TokenManager 추가

const apiUrl = "https://j11e202.p.ssafy.io/api";
// const apiUrl = "/dev/api";

// 로그인 요청을 보내는 함수
export const login = async (formData) => {
  const loginData = {
    email: formData.email,
    password: formData.password,
  };

  console.log("로그인 요청 데이터:", loginData);

  try {
    const response = await axios.post(`${apiUrl}/auth`, loginData, {
      withCredentials: true, // 쿠키 포함을 위한 옵션
  }); // 로그인 POST 요청


    // 응답 확인
    if (response.data.success) {
      const { accessToken, refreshToken } = response.data.data;

      // Token을 저장 (TokenManager 사용)
      TokenManager.setAccessToken(accessToken);
      TokenManager.setRefreshToken(refreshToken);

      console.log("로그인 성공, Access Token:", accessToken);
      return response.data;
    } else {
      throw new Error("로그인에 실패했습니다.");
    }
  } catch (error) {
    console.error("로그인 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { login };
