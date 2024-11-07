import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 기부품 조회 요청을 보내는 함수
export const apiSearchDonations = async () => {
  try {
    // API 요청
    const response = await axios.get(`${apiUrl}/gifts`, {

    });

    // 응답 확인
    if (response.data.success) {
      console.log("기부품 조회 성공:", response.data.data);

      // 데이터 구조 변환
      const formattedData = response.data.data.map((gift) => ({
        giftIdx: gift.giftIdx,
        giftName: gift.giftName,          // 기부품 이름
        corporationName: gift.corporationName, // 기업 이름
        categoryName: gift.categoryName,  // 카테고리 이름
        giftThumbnail: gift.giftThumbnail, // 기부품 썸네일 이미지
        price: gift.price,               // 가격
      }));

      return formattedData; // 변환된 기부품 데이터 반환
    } else {
      throw new Error("기부품 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("기부품 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { apiSearchDonations };
