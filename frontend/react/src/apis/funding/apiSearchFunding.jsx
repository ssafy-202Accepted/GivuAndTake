import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 전체 펀딩 조회 요청을 보내는 함수
export const apiSearchFunding = async (type, state) => {
  try {
    // API 요청
    const response = await axios.get(`${apiUrl}/government-fundings`, {
      params: {
        type,  // D (재난재해), R (지역기부)
        state, // 0 (진행 예정), 1 (진행 중), 2 (완료)
      },
    });

    // 응답 확인
    if (response.data.success) {
      console.log("펀딩 조회 성공:", response.data.data);

      // 데이터 구조 변환
      const formattedData = response.data.data.map((funding) => ({
        fundingIdx: funding.fundingIdx,
        fundingTitle: funding.fundingTitle, // 제목
        startDate: funding.startDate,
        endDate: funding.endDate,
        totalMoney: funding.totalMoney,
        goalMoney: funding.goalMoney,
        thumbnail: funding.fundingThumbnail, // 썸네일 이미지
        location: `${funding.sido} ${funding.sigungu}`, // 시도와 시군구 정보
      }));

      return formattedData; // 변환된 펀딩 데이터 반환
    } else {
      throw new Error("펀딩 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("펀딩 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { apiSearchFunding };
