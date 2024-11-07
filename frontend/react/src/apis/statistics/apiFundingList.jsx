import axios from "axios";
import TokenManager from "../../utils/TokenManager";

const apiFundingList = async () => {
  const accessToken = TokenManager.getAccessToken();  // 토큰 가져오기
  try {
    const response = await axios.get(
      "https://j11e202.p.ssafy.io/api/government-fundings/my-fundings", 
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,  // Bearer 토큰 설정
        },
        params: {
          pageNo: 0,
          pageSize: 10,
          state: 2,
        },
      }
    );

    // 응답 데이터 확인 및 로그 출력
    if (response.data.success) {
      const fundingData = response.data.data.map(funding => ({
        fundingIdx: funding.fundingIdx,
        sido: funding.sido,
        sigungu: funding.sigungu,
        fundingTitle: funding.fundingTitle,
        goalMoney: funding.goalMoney,
        totalMoney: funding.totalMoney,
        startDate: funding.startDate,
        endDate: funding.endDate,
        fundingThumbnail: funding.fundingThumbnail,
        fundingType: funding.fundingType === "D" ? "재난재해" : "지역기부",
      }));

      console.log("펀딩 조회 성공:", fundingData);
      return fundingData;  // 변환된 펀딩 데이터 반환
    } else {
      throw new Error("펀딩 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("펀딩 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export { apiFundingList };  // apiFundingList 내보내기
