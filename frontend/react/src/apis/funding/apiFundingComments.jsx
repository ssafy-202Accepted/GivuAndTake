import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 펀딩 응원댓글 조회 요청을 보내는 함수
export const apiFundingComments = async (fundingIdx) => {
  try {
    // API 요청
    const response = await axios.get(`${apiUrl}/government-fundings/${fundingIdx}/comments`);

    // 응답 확인
    if (response.data.success) {
      const comments = response.data.data;

      // 데이터를 구조화하여 반환
      const formattedComments = comments.map((comment) => ({
        id: comment.commentIdx,
        name: comment.name,
        content: comment.commentContent,
        createdDate: comment.createdDate,
      }));

      console.log("응원댓글 조회 성공:", formattedComments);
      return formattedComments; // 변환된 댓글 데이터 반환
    } else {
      throw new Error("응원댓글 조회에 실패했습니다.");
    }
  } catch (error) {
    console.error("응원댓글 조회 실패:", error.response ? error.response.data : error.message);
    throw error;
  }
};

export default { apiFundingComments };
