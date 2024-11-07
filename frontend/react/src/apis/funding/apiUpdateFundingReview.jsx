import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 펀딩 후기 수정 요청을 보내는 함수
export const apiUpdateFundingReview = async (fundingIdx, reviewContent, accessToken) => {
  try {
    // API 요청
    const response = await axios.patch(
      `${apiUrl}/government-fundings/${fundingIdx}/review`,
      {
        reviewContent, // 수정된 후기 내용
      },
      {
        headers: {
          Authorization: `Bearer ${accessToken}`, // 인증 토큰 추가
          "Content-Type": "application/json", // JSON 형식으로 요청
        },
      }
    );

    // 응답 확인
    if (response.data.success) {
      console.log("후기 수정 성공:", response.data.data);
      return response.data.data; // 수정된 후기 반환
    } else {
      throw new Error("후기 수정에 실패했습니다.");
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      // 401 에러일 경우
      console.error("인증 오류: 유효하지 않은 토큰입니다. 다시 로그인하세요.");
      alert("세션이 만료되었거나 유효하지 않은 토큰입니다. 다시 로그인해주세요.");
      // 필요 시 재로그인 로직을 추가할 수 있습니다.
    } else {
      console.error(
        "후기 수정 실패:",
        error.response ? error.response.data : error.message
      );
    }
    throw error;
  }
};

export default { apiUpdateFundingReview };
