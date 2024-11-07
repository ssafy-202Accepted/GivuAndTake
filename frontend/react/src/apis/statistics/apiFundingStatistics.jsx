// src/apis/statistics/apiFundingStatistics.jsx
import axios from "axios";
import TokenManager from "../../utils/TokenManager";

const apiFundingStatistics = async (fundingIdx) => {
  const accessToken = TokenManager.getAccessToken();  // 토큰 가져오기
  try {
    const response = await axios.get(
      `https://j11e202.p.ssafy.io/api/government-fundings/statistics/${fundingIdx}`, 
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,  // Bearer 토큰 설정
        },
      }
    );

    // 응답 데이터 확인 및 로그 출력
    if (response.data.success) {
      const statisticsData = response.data.data;

      // 응답 데이터 형식 정의
      const formattedData = {
        fundingDayStatistic: statisticsData.fundingDayStatistic.arr,  // 하루별 통계 배열
        fundingParticipants: statisticsData.fundingParticipate.participants.map(participant => ({
          name: participant.name,  // 참여자 이름
          price: participant.price, // 기부 금액
        })),
        fundingStatsByAgeAndGender: {
          maleData: statisticsData.fundingStatsByAgeAndGender.maleData,  // 남성 연령별 통계
          femaleData: statisticsData.fundingStatsByAgeAndGender.femaleData,  // 여성 연령별 통계
        },
      };

      console.log("펀딩 통계 조회 성공:", formattedData);
      return formattedData;  // 변환된 통계 데이터 반환
    } else {
      throw new Error("펀딩 통계 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("펀딩 통계 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export { apiFundingStatistics };  // apiFundingStatistics 내보내기
