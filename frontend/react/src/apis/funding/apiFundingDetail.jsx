import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 펀딩 상세 조회 요청을 보내는 함수
export const apiFundingDetail = async (fundingIdx) => {
  try {
    // API 요청
    const response = await axios.get(`${apiUrl}/government-fundings/${fundingIdx}`);

    // 응답 확인
    if (response.data.success) {
      const fundingDetail = response.data.data;
      
      // 데이터를 구조화하여 반환
      const formattedDetail = {
        id: fundingDetail.fundingIdx,
        fundingTitle: fundingDetail.fundingTitle,
        goalMoney: fundingDetail.goalMoney,
        totalMoney: fundingDetail.totalMoney,
        startDate: fundingDetail.startDate,
        endDate: fundingDetail.endDate,
        thumbnail: fundingDetail.fundingThumbnail, // 썸네일 이미지
        location: `${fundingDetail.sido} ${fundingDetail.sigungu}`, // 시도와 시군구 정보
        fundingContent: fundingDetail.fundingContent, // 상세 내용
        fundingContentImage: fundingDetail.fundingContentImage, // 내용 이미지 추가
        fundingType: fundingDetail.fundingType, // 펀딩 유형
        state: fundingDetail.state // 펀딩 상태
      };

      console.log("펀딩 상세 조회 성공:", formattedDetail);
      return formattedDetail; // 변환된 펀딩 상세 데이터 반환
    } else {
      throw new Error("펀딩 상세 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("펀딩 상세 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { apiFundingDetail };
