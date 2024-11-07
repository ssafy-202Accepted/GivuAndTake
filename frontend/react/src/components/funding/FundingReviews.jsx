import React, { useEffect, useState } from "react";
import { apiSearchFundingReview } from "../../apis/funding/apiSearchFundingReview"; 
import { apiWriteFundingReview } from "../../apis/funding/apiWriteFundingReview"; 
import { apiUpdateFundingReview } from "../../apis/funding/apiUpdateFundingReview"; 
import TokenManager from "../../utils/TokenManager"; 
import Swal from "sweetalert2"; // SweetAlert2 import
import "./FundingReviews.css";

const FundingReviews = ({ fundingIdx }) => {
  const [review, setReview] = useState("");
  const [loading, setLoading] = useState(true);
  const [hasReview, setHasReview] = useState(false);
  const [isEditing, setIsEditing] = useState(false); 
  const [editReview, setEditReview] = useState("");
  const [isEditingReview, setIsEditingReview] = useState(false); 

  const accessToken = TokenManager.getAccessToken(); 

  useEffect(() => {
    const fetchReview = async () => {
      try {
        const response = await apiSearchFundingReview(fundingIdx);
        if (response.success && response.reviewContent) {
          setReview(response.reviewContent);
          setHasReview(true);
        } else {
          setHasReview(false);
        }
      } catch (error) {
        console.error("후기를 가져오는 데 실패했습니다:", error);
        setHasReview(false);
      } finally {
        setLoading(false);
      }
    };

    fetchReview();
  }, [fundingIdx]);

  const handleSubmitReview = async () => {
    if (editReview.trim() === "") {
      alert("후기를 입력해주세요.");
      return;
    }

    try {
      if (isEditingReview) {
        await apiUpdateFundingReview(fundingIdx, editReview, accessToken);
        Swal.fire({
          title: "수정 완료",
          text: "후기 수정이 완료되었습니다",
          icon: "success",
          confirmButtonText: "확인",
        });
      } else {
        await apiWriteFundingReview(fundingIdx, editReview, accessToken);
        Swal.fire({
          title: "작성 완료",
          text: "후기 작성이 완료되었습니다",
          icon: "success",
          confirmButtonText: "확인",
        });
      }

      setReview(editReview);
      setIsEditing(false);
      setIsEditingReview(false);
      setHasReview(true);
    } catch (error) {
      console.error("후기 작성 또는 수정에 실패했습니다:", error);
      Swal.fire({
        title: "오류",
        text: "후기 작성 또는 수정 중 오류가 발생했습니다.",
        icon: "error",
        confirmButtonText: "확인",
      });
    }
  };

  const handleReviewButtonClick = () => {
    if (hasReview) {
      setEditReview(review);
      setIsEditingReview(true); 
    } else {
      setEditReview(""); 
      setIsEditingReview(false); 
    }
    setIsEditing(true); 
  };

  if (loading) {
    return <p>로딩 중...</p>;
  }

  return (
    <div className="funding-reviews">
      <div className="funding-reviews-header">
        <h2>펀딩 후기</h2>
      </div>
      {!isEditing && (
        <div className="write-review-button-container">
          <button className="write-review-button" onClick={handleReviewButtonClick}>
            {hasReview ? "후기 수정" : "후기 작성"}
          </button>
        </div>
      )}

      {isEditing ? (
        <div className="editing-container">
          <textarea
            className="review-textarea"
            value={editReview}
            onChange={(e) => setEditReview(e.target.value)}
            placeholder="후기를 작성하세요"
          />
          <div className="review-buttons">
            <button className="submit-review-button" onClick={handleSubmitReview}>
              {isEditingReview ? "수정 완료" : "작성 완료"}
            </button>
            <button className="cancel-review-button" onClick={() => setIsEditing(false)}>
              취소
            </button>
          </div>
        </div>
      ) : (
        <div>
          {hasReview ? <p>{review}</p> : <p>아직 작성된 후기가 없습니다.</p>}
        </div>
      )}
    </div>
  );
};

export default FundingReviews;
