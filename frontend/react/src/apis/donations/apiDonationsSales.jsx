import axios from "axios";
import TokenManager from "../../utils/TokenManager"; // TokenManager를 사용하여 accessToken 가져오기

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 특정 기부품 판매량 조회 요청을 보내는 함수
export const apiDonationsSales = async (giftId) => {
  try {
    const accessToken = TokenManager.getAccessToken(); // TokenManager에서 accessToken 가져오기
    const response = await axios.get(`${apiUrl}/purchases/statistics/${giftId}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`, // Authorization 헤더 추가
        "Content-Type": "application/json", // JSON 형식으로 요청
      },
    });
    console.log("API 응답:", response.data); // 응답 메시지 출력
    if (response.data.success) {
      return response.data.data; // 판매량 반환
    } else {
      throw new Error("판매량 정보를 가져오는 데 실패했습니다.");
    }
  } catch (error) {
    console.error("판매량 조회 실패:", error);
    throw error;
  }
};

export default { apiDonationsSales };
