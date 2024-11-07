import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 특정 기부품 상세 조회 요청을 보내는 함수
export const apiDonationsDetail = async (giftIdx) => {
  try {
    // API 요청
    const response = await axios.get(`${apiUrl}/gifts/${giftIdx}`);

    // 응답 확인
    if (response.data.success) {
      const giftDetail = response.data.data;

      // 데이터를 구조화하여 반환
      const formattedDetail = {
        id: giftDetail.giftIdx,
        giftName: giftDetail.giftName,
        corporationName: giftDetail.corporationName,
        corporationSido: giftDetail.corporationSido,
        corporationSigungu: giftDetail.corporationSigungu,
        categoryName: giftDetail.categoryName,
        giftThumbnail: giftDetail.giftThumbnail, // 썸네일 이미지
        giftContent: giftDetail.giftContent, // 기부품 상세 내용
        giftContentImage: giftDetail.giftContentImage, // 추가된 내용 이미지
        price: giftDetail.price, // 가격
        createdDate: giftDetail.createdDate, // 생성일
        modifiedDate: giftDetail.modifiedDate // 수정일
      };

      console.log("기부품 상세 조회 성공:", formattedDetail);
      return formattedDetail; // 변환된 기부품 상세 데이터 반환
    } else {
      throw new Error("기부품 상세 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("기부품 상세 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { apiDonationsDetail };
