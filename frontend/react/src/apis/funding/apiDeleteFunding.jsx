import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 펀딩 삭제 요청을 보내는 함수
export const apiDeleteFunding = async (fundingIdx, accessToken) => {
  try {
    // API 요청 (DELETE 메서드 사용)
    const response = await axios.delete(
      `${apiUrl}/government-fundings/${fundingIdx}`,
      {
        headers: {
          Authorization: `Bearer ${accessToken}`, // 인증 토큰 추가
        },
      }
    );

    // 응답 확인
    if (response.data.success) {
      console.log("펀딩 삭제 성공:", response.data);
      return response.data; // 성공 메시지 반환
    } else {
      throw new Error("펀딩 삭제에 실패했습니다.");
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      // 401 에러일 경우
      console.error("인증 오류: 유효하지 않은 토큰입니다. 다시 로그인하세요.");
      alert("세션이 만료되었거나 유효하지 않은 토큰입니다. 다시 로그인해주세요.");
    } else {
      console.error(
        "펀딩 삭제 실패:",
        error.response ? error.response.data : error.message
      );
    }
    throw error;
  }
};

export default { apiDeleteFunding };
