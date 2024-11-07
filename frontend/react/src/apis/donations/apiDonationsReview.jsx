import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 선택한 기부품의 후기를 조회하는 함수
export const apiDonationsReview = async (giftIdx) => {
  try {
    // 요청 URL 설정
    const url = `${apiUrl}/gifts/${giftIdx}/review?pageNo=1&pageSize=100&isOrderLiked=true`;

    // API 요청
    const response = await axios.get(url);

    // 응답 확인
    if (response.data.success) {
      console.log("후기 조회 성공:", response.data.data);

      // 데이터 구조 변환
      const formattedData = response.data.data.map((review) => ({
        reviewIdx: review.reviewIdx,
        reviewImage: review.reviewImage,            // 리뷰 이미지
        reviewContent: review.reviewContent,        // 리뷰 내용
        giftIdx: review.giftIdx,                    // 기부품 ID
        giftName: review.giftName,                  // 기부품 이름
        giftThumbnail: review.giftThumbnail,        // 기부품 썸네일 이미지
        corporationName: review.corporationName,    // 기업 이름
        userIdx: review.userIdx,                    // 사용자 ID
        userName: review.userName,                  // 사용자 이름
        userProfileImage: review.userProfileImage,  // 사용자 프로필 이미지
        orderIdx: review.orderIdx,                  // 주문 ID
        likedCount: review.likedCount,              // 좋아요 수
        createdDate: review.createdDate,            // 작성일
        modifiedDate: review.modifiedDate           // 수정일
      }));

      return formattedData; // 변환된 리뷰 데이터 반환
    } else {
      throw new Error("후기 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("후기 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { apiDonationsReview };
