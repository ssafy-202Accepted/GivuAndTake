import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 펀딩 등록 요청을 보내는 함수
export const apiCreateFunding = async (fundingData, accessToken) => {
  try {
    // FormData 객체 생성
    const formData = new FormData();

    // JSON 데이터를 Blob으로 변환하여 추가
    const fundingAddDto = {
      fundingTitle: fundingData.fundingTitle,
      fundingContent: fundingData.fundingContent,
      goalMoney: fundingData.goalMoney,
      startDate: fundingData.startDate,
      endDate: fundingData.endDate,
      fundingType: fundingData.fundingType,
    };

    formData.append(
      "fundingAddDto",
      new Blob([JSON.stringify(fundingAddDto)], { type: "application/json" })
    );

    // fundingThumbnail 이미지 파일 추가 (multipart/form-data)
    if (fundingData.fundingThumbnail) {
      formData.append("fundingThumbnail", fundingData.fundingThumbnail);
    }

    // contentImage 이미지 파일 추가 (multipart/form-data)
    if (fundingData.contentImage) {
      formData.append("contentImage", fundingData.contentImage);
    }

    // API 요청
    const response = await axios.post(`${apiUrl}/government-fundings`, formData, {
      headers: {
        Authorization: `Bearer ${accessToken}`, // 인증 토큰 추가
        "Content-Type": "multipart/form-data", // 파일 업로드를 위한 헤더
      },
    });

    // 응답 확인
    if (response.data.success) {
      console.log("펀딩 등록 성공:", response.data.data);
      return response.data.data; // 등록된 펀딩 데이터 반환
    } else {
      throw new Error("펀딩 등록에 실패했습니다.");
    }
  } catch (error) {
    if (error.response && error.response.status === 401) {
      // 401 에러일 경우
      console.error("인증 오류: 유효하지 않은 토큰입니다. 다시 로그인하세요.");
      alert("세션이 만료되었거나 유효하지 않은 토큰입니다. 다시 로그인해주세요.");
    } else {
      console.error(
        "펀딩 등록 실패:",
        error.response ? error.response.data : error.message
      );
    }
    throw error;
  }
};

export default { apiCreateFunding };
