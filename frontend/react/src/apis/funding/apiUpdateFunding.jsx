import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 펀딩 수정 요청을 보내는 함수
export const apiUpdateFunding = async (fundingIdx, fundingData, accessToken) => {
  try {
    // API 요청
    const response = await axios.patch(
      `${apiUrl}/government-fundings/${fundingIdx}`,
      {
        fundingTitle: fundingData.fundingTitle,
        fundingContent: fundingData.fundingContent,
        goalMoney: fundingData.goalMoney,
        startDate: fundingData.startDate,
        endDate: fundingData.endDate,
        fundingThumbnail: fundingData.fundingThumbnail,
        fundingType: fundingData.fundingType,
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
      console.log("펀딩 수정 성공:", response.data.data);
      return response.data.data; // 수정된 펀딩 데이터 반환
    } else {
      throw new Error("펀딩 수정에 실패했습니다.");
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      // 401 에러일 경우
      console.error("인증 오류: 유효하지 않은 토큰입니다. 다시 로그인하세요.");
      alert("세션이 만료되었거나 유효하지 않은 토큰입니다. 다시 로그인해주세요.");
    } else {
      console.error(
        "펀딩 수정 실패:",
        error.response ? error.response.data : error.message
      );
    }
    throw error;
  }
};

export default { apiUpdateFunding };
