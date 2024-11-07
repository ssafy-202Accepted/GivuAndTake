import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 내가 참여한 펀딩 조회 요청을 보내는 함수
export const apiMyFunding = async (state, accessToken) => {
  try {
    // API 요청
    const response = await axios.get(`${apiUrl}/government-fundings/my-fundings`, {
      params: {
        pageNo: 0,          // 페이지 번호
        pageSize: 100,      // 페이지 사이즈
        state,              // 상태: 0 (모금 예정), 1 (진행 중), 2 (완료)
      },
      headers: {
        Authorization: `Bearer ${accessToken}`, // 인증 토큰
      },
    });

    // 응답 확인
    if (response.data.success) {
      console.log("펀딩 조회 성공:", response.data.data);

      // 데이터 구조 변환
      const formattedData = response.data.data.map((funding) => ({
        fundingIdx: funding.fundingIdx,
        fundingTitle: funding.fundingTitle,    // 펀딩 제목
        goalMoney: funding.goalMoney,          // 목표 금액
        totalMoney: funding.totalMoney,        // 현재 모금된 금액
        startDate: funding.startDate,          // 시작일
        endDate: funding.endDate,              // 마감일
        thumbnail: funding.fundingThumbnail,   // 펀딩 썸네일 이미지
        location: `${funding.sido} ${funding.sigungu}`, // 시도 및 시군구
        fundingType: funding.fundingType,      // 펀딩 유형 (D: 재난재해, R: 지역기부)
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

export default { apiMyFunding };
