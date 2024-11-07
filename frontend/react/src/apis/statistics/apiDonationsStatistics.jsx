import axios from "axios";
import TokenManager from "../../utils/TokenManager";

const apiDonationsStatistics = async (donationIdx) => {
  const accessToken = TokenManager.getAccessToken();  // Get the access token
  try {
    const response = await axios.get(
      `https://j11e202.p.ssafy.io/api/gifts/statistics?giftIdx=${donationIdx}`, // 쿼리 파라미터로 전달
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,  // Set Bearer token
        },
      }
    );

    // Check the response and log data
    if (response.data.success) {
      const statisticsData = response.data.data;

      // Format the data structure
      const formattedData = {
        giftYearStatistics: statisticsData.giftYearStatisticsDto.arr,  // Yearly statistics array
        giftPercentageByGender: statisticsData.giftPercentageDto.statistics.gender,  // Gender-based stats
        giftPercentageByCategory: statisticsData.giftPercentageDto.statistics.category,  // Category-based stats
        giftPercentageByAge: statisticsData.giftPercentageDto.statistics.age,  // Age-based stats
        giftPurchasers: statisticsData.giftPurchaserDto.purchasers.map(purchaser => ({
          name: purchaser.name,  // Purchaser name
          price: purchaser.price, // Purchase amount
        })),
      };

      console.log("기부 통계 조회 성공:", formattedData);
      return formattedData;  // Return the formatted statistics data
    } else {
      throw new Error("기부 통계 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("기부 통계 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export { apiDonationsStatistics };
