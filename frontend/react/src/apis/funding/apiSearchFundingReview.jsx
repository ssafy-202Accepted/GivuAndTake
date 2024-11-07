import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 펀딩 후기를 조회하는 함수
export const apiSearchFundingReview = async (fundingIdx) => {
  try {
    // API 요청
    const response = await axios.get(`${apiUrl}/government-fundings/${fundingIdx}/review`);

    // 응답 확인
    if (response.data.success) {
      console.log("펀딩 후기 조회 성공:", response.data.data);
      const { reviewContent } = response.data.data;
      
      return {
        success: true,
        reviewContent,
      }; // 후기 내용 반환
    } else {
      throw new Error("펀딩 후기 조회에 실패했습니다.");
    }
  } catch (error) {
    // 에러 처리
    if (error.response && error.response.data.code === "EF4010") {
      console.warn("후기가 작성되지 않았습니다.");
      return {
        success: false,
        message: "후기가 작성되지 않았습니다.",
      };
    } else {
      console.error("펀딩 후기 조회 실패:", error.response ? error.response.data : error.message);
      throw error;
    }
  }
};

export default { apiSearchFundingReview };
