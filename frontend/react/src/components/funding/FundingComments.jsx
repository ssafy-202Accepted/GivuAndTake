import React, { useEffect, useState } from "react";
import { apiFundingComments } from "../../apis/funding/apiFundingComments"; // API function import
import "./FundingComments.css"; // Import the corresponding CSS file
import defaultProfile from "../../assets/student.png"; // Import the default profile image

const FundingComments = ({ fundingIdx }) => {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1); // 현재 페이지 상태
  const commentsPerPage = 5; // 한 페이지에 표시할 댓글 수

  // Fetch comments when the component loads
  useEffect(() => {
    const fetchComments = async () => {
      try {
        const fetchedComments = await apiFundingComments(fundingIdx); // Call the API
        setComments(fetchedComments); // Set the comments to state
      } catch (error) {
        console.error("응원댓글 불러오기 실패:", error);
      } finally {
        setLoading(false); // Turn off loading after the fetch is done
      }
    };

    fetchComments();
  }, [fundingIdx]);

  // 페이지네이션 처리
  const indexOfLastComment = currentPage * commentsPerPage;
  const indexOfFirstComment = indexOfLastComment - commentsPerPage;
  const currentComments = comments.slice(indexOfFirstComment, indexOfLastComment);

  const handleNextPage = () => {
    setCurrentPage((prevPage) => prevPage + 1);
  };

  const handlePrevPage = () => {
    setCurrentPage((prevPage) => prevPage - 1);
  };

  if (loading) {
    return <p>로딩 중...</p>;
  }

  return (
    <div className="funding-comments">
      <h2>응원댓글</h2>
      <div className="comments-grid"> {/* Flexbox로 댓글 그리드 설정 */}
        {currentComments.length === 0 ? (
          <p>아직 등록된 응원댓글이 없습니다.</p>
        ) : (
          currentComments.map((comment) => (
            <div key={comment.id} className="comment"> {/* 각 댓글을 그리드 항목으로 설정 */}
              <img src={defaultProfile} alt="프로필" className="profile-img" />
              <div className="comment-content">
                <span className="comment-name">{comment.name}</span>
                <p className="comment-text">{comment.content}</p>
                <span className="comment-date">
                  {new Date(comment.createdDate).toLocaleDateString()}
                </span>
              </div>
            </div>
          ))
        )}
      </div>
      
      {/* 페이지네이션 버튼 */}
      <div className="pagination">
        <button 
          onClick={handlePrevPage} 
          disabled={currentPage === 1} // 첫 페이지에서는 이전 버튼 비활성화
        >
          이전
        </button>
        <button 
          onClick={handleNextPage} 
          disabled={indexOfLastComment >= comments.length} // 마지막 페이지에서는 다음 버튼 비활성화
        >
          다음
        </button>
      </div>
    </div>
  );
};

export default FundingComments;
