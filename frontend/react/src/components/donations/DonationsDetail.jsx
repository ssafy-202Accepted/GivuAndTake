import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Sidebar from "../Sidebar";
import "./DonationsDetail.css";
import Swal from "sweetalert2";
import { apiDonationsDetail } from "../../apis/donations/apiDonationsDetail";
import { apiDonationsSales } from "../../apis/donations/apiDonationsSales";
import { apiDeleteDonations } from "../../apis/donations/apiDeleteDonations";
import { apiUpdateDonations } from "../../apis/donations/apiUpdateDonations";
import { apiDonationsReview } from "../../apis/donations/apiDonationsReview";
import TokenManager from "../../utils/TokenManager";
import defaultProfile from "../../assets/student.png"; // 기본 프로필 이미지

const DonationsDetail = () => {
  const navigate = useNavigate();
  const { giftIdx } = useParams();
  const [selectedMenu, setSelectedMenu] = useState("기부품");
  const [activeTab, setActiveTab] = useState("소개");
  const [donation, setDonation] = useState(null);
  const [salesCount, setSalesCount] = useState(0);
  const [reviews, setReviews] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [updatedDonation, setUpdatedDonation] = useState({
    giftName: "",
    price: "",
    giftThumbnail: "",
    giftContent: "",
    giftContentImage: "",
    categoryName: "",
  });

  const categories = ["지역상품권", "농축산물", "수산물", "가공식품", "공예품"];

  useEffect(() => {
    const fetchDonationDetail = async () => {
      try {
        const data = await apiDonationsDetail(giftIdx);
        setDonation(data);
        setUpdatedDonation({
          giftName: data.giftName,
          price: data.price?.toString(),
          giftThumbnail: data.giftThumbnail,
          giftContent: data.giftContent,
          giftContentImage: data.giftContentImage,
          categoryName: data.categoryName,
        });
        const salesData = await apiDonationsSales(giftIdx);
        setSalesCount(salesData);
      } catch (error) {
        console.error("기부품 상세 정보를 가져오는 데 실패했습니다:", error);
      }
    };

    fetchDonationDetail();
  }, [giftIdx]);

  useEffect(() => {
    if (activeTab === "후기") {
      const fetchReviews = async () => {
        try {
          const reviewData = await apiDonationsReview(giftIdx);
          setReviews(reviewData);
        } catch (error) {
          console.error("후기를 불러오는 데 실패했습니다:", error);
          setReviews([]);
        }
      };
      fetchReviews();
    }
  }, [activeTab, giftIdx]);

  const handleDelete = () => {
    Swal.fire({
      title: '정말 삭제하시겠습니까?',
      text: "이 작업은 되돌릴 수 없습니다.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: '삭제',
      cancelButtonText: '취소'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          const accessToken = TokenManager.getAccessToken();
          await apiDeleteDonations(giftIdx, accessToken);
          Swal.fire('삭제되었습니다!', '기부품이 성공적으로 삭제되었습니다.', 'success');
          navigate("/donations");
        } catch (error) {
          Swal.fire('삭제 실패', '기부품을 삭제하는 중 문제가 발생했습니다. 다시 시도해주세요.', 'error');
          console.error("기부품 삭제 실패:", error);
        }
      }
    });
  };

  const handleEditClick = () => {
    setIsEditing(true);
  };

  const handleCancelClick = () => {
    setIsEditing(false);
  };

  const handleSaveClick = async () => {
    try {
      const accessToken = TokenManager.getAccessToken();
      const categoryIdx = categories.indexOf(updatedDonation.categoryName) + 1;

      await apiUpdateDonations(giftIdx, {
        giftName: updatedDonation.giftName,
        price: parseInt(updatedDonation.price, 10),
        giftThumbnail: updatedDonation.giftThumbnail,
        giftContent: updatedDonation.giftContent,
        giftContentImage: updatedDonation.giftContentImage,
        categoryIdx: categoryIdx
      }, accessToken);

      Swal.fire({
        title: "수정되었습니다.",
        text: "기부품 정보가 성공적으로 수정되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      }).then(() => {
        window.location.reload();
      });

      setIsEditing(false);
    } catch (error) {
      Swal.fire({
        title: "수정 실패",
        text: "기부품 정보를 수정하는 중 문제가 발생했습니다.",
        icon: "error",
        confirmButtonText: "확인",
      });
      console.error("기부품 수정 실패:", error);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUpdatedDonation((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const renderContent = () => {
    switch (activeTab) {
      case "소개":
        return (
          <div className="donations-description">
            <h2>기부품 소개</h2>
            {donation?.giftContentImage && (
              <img
                src={donation.giftContentImage}
                alt="기부품 내용 이미지"
                className="donations-content-image"
              />
            )}
            {isEditing ? (
              <textarea
                name="giftContent"
                value={updatedDonation.giftContent}
                onChange={handleChange}
                className="edit-textarea"
              />
            ) : (
              <p style={{ whiteSpace: "pre-wrap" }}>
                {donation?.giftContent}
              </p>
            )}
          </div>
        );
      case "후기":
        return (
          <div className="donations-reviews-section">
            {reviews.length > 0 ? (
              reviews.map((review) => (
                <div key={review.reviewIdx} className="review-item">
                  <div className="review-header">
                    <img
                      src={review.userProfileImage || defaultProfile}
                      alt={review.userName}
                      className="review-user-image"
                    />
                    <div className="review-meta">
                      <span className="review-user-name">{review.userName}</span>
                      <span className="review-date">{new Date(review.createdDate).toLocaleDateString()}</span>
                    </div>
                  </div>
                  <div className="review-content-wrapper">
                    {review.reviewImage && (
                      <img
                        src={review.reviewImage}
                        alt="후기 이미지"
                        className="review-image"
                      />
                    )}
                    <p className="review-content">{review.reviewContent}</p>
                  </div>
                  <div className="review-footer">
                    <span className="review-likes">좋아요 {review.likedCount}</span>
                  </div>
                </div>
              ))
            ) : (
              <p>후기가 없습니다.</p>
            )}
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="donations-detail-container">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />

      <div className="donations-detail-content">
        <h1 className="donations-detail-title">기부품 상세</h1>

        <div className="donations-detail-header">
          <div className="donations-tabs">
            <button
              className={activeTab === "소개" ? "active" : ""}
              onClick={() => setActiveTab("소개")}
            >
              소개
            </button>
            <button
              className={activeTab === "후기" ? "active" : ""}
              onClick={() => setActiveTab("후기")}
            >
              후기
            </button>
          </div>
          <div className="donations-button-group">
            {isEditing ? (
              <>
                <button className="save-button" onClick={handleSaveClick}>저장</button>
                <button className="cancel-button" onClick={handleCancelClick}>취소</button>
              </>
            ) : (
              <>
                <button className="donations-edit-button" onClick={handleEditClick}>
                  수정
                </button>
                <button className="donations-delete-button" onClick={handleDelete}>
                  삭제
                </button>
              </>
            )}
          </div>
        </div>
                {/* activeTab이 "후기"일 때 기부품 내용 섹션을 감추기 */}
        {activeTab !== "후기" && (
          <div className="donations-detail-body">
            <div className="donations-thumbnail-section">
              {isEditing ? (
                <img
                  src={updatedDonation.giftThumbnail}
                  alt="기부품 썸네일"
                  className="donations-thumbnail"
                />
              ) : donation?.giftThumbnail ? (
                <img
                  src={donation.giftThumbnail}
                  alt="기부품 썸네일"
                  className="donations-thumbnail"
                />
              ) : (
                <div className="donations-thumbnail-placeholder">썸네일</div>
              )}
            </div>

            <div className="donations-info-section">
              {isEditing ? (
                <>
                  <div className="input-row">
                    <label htmlFor="giftName">기부품 이름:</label>
                    <input
                      id="giftName"
                      type="text"
                      name="giftName"
                      value={updatedDonation.giftName}
                      onChange={handleChange}
                      className="edit-input"
                    />
                  </div>
                  <div className="input-row">
                    <label htmlFor="price">가격:</label>
                    <input
                      id="price"
                      type="text"
                      name="price"
                      value={updatedDonation.price}
                      onChange={handleChange}
                      className="edit-input"
                    />
                  </div>
                  <div className="input-row">
                    <label htmlFor="category">카테고리:</label>
                    <select
                      id="category"
                      name="categoryName"
                      value={updatedDonation.categoryName}
                      onChange={handleChange}
                      className="edit-input"
                    >
                      {categories.map((category) => (
                        <option key={category} value={category}>
                          {category}
                        </option>
                      ))}
                    </select>
                  </div>
                </>
              ) : (
                <>
                  <h2 className="donations-title">{donation?.giftName}</h2>
                  <p className="donations-price">가격: {donation?.price?.toLocaleString()}원</p>
                  <p className="donations-category">카테고리: {donation?.categoryName}</p>
                </>
              )}
              <p className="donations-stock">판매량: {salesCount}개</p>
              <p className="donations-corporationName">판매자: {donation?.corporationSido} {donation?.corporationSigungu} </p>
              <p className="donations-date">등록일: {new Date(donation?.createdDate).toLocaleDateString()}</p>
            </div>
          </div>
        )}

        {renderContent()}
      </div>
    </div>
  );
};

export default DonationsDetail;
