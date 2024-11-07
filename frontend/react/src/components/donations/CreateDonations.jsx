import React, { useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "../Sidebar";
import "./CreateDonations.css";
import { apiCreateDonations } from "../../apis/donations/apiCreateDonations";
import TokenManager from "../../utils/TokenManager";
import Swal from "sweetalert2";

const CreateDonations = () => {
  const [donationName, setDonationName] = useState("");
  const [category, setCategory] = useState("");
  const [price, setPrice] = useState("");
  const [description, setDescription] = useState("");
  const [thumbnail, setThumbnail] = useState(null);
  const [contentImage, setContentImage] = useState(null);
  const [selectedMenu, setSelectedMenu] = useState("기부품");
  const navigate = useNavigate();
  const descriptionRef = useRef(null);

  const handleThumbnailChange = (e) => {
    setThumbnail(e.target.files[0]);
  };

  const handleContentImageChange = (e) => {
    setContentImage(e.target.files[0]);
  };

  const formatPrice = (value) => {
    const number = value.replace(/[^0-9]/g, "");
    return new Intl.NumberFormat().format(number);
  };

  const handlePriceChange = (e) => {
    const formattedValue = formatPrice(e.target.value);
    setPrice(formattedValue);
  };

  const handleSave = async () => {
    const accessToken = TokenManager.getAccessToken();

    const donationData = {
      giftName: donationName,
      categoryIdx: parseInt(category.replace("category", "")),
      giftContent: description,
      price: parseInt(price.replace(/,/g, "")),
    };

    try {
      const result = await apiCreateDonations(donationData, thumbnail, contentImage, accessToken);
      console.log("기부품 등록 성공:", result);

      Swal.fire({
        title: "등록 성공",
        text: "기부품이 성공적으로 등록되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      }).then(() => {
        navigate("/donations");
      });
    } catch (error) {
      console.error("기부품 등록 실패:", error);

      Swal.fire({
        title: "등록 실패",
        text: "기부품 등록 중 오류가 발생했습니다. 다시 시도해주세요.",
        icon: "error",
        confirmButtonText: "확인",
      });
    }
  };

  return (
    <div className="create-donations-layout-unique">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />

      <div className="create-donations-content-unique">
        <div className="donations-header-unique">
          <h1>기부품 등록</h1>
          <button className="donations-save-button-unique" onClick={handleSave}>
            저장
          </button>
        </div>

        <div className="donations-details-unique">
          <div className="donations-thumbnail-section-unique">
            {thumbnail ? (
              <img
                src={URL.createObjectURL(thumbnail)}
                alt="썸네일 미리보기"
                className="donations-thumbnail-unique"
              />
            ) : (
              <div className="donations-thumbnail-placeholder-unique">썸네일</div>
            )}
          </div>

          <div className="donations-details-section-unique">
            <input
              type="text"
              placeholder="상품명"
              value={donationName}
              onChange={(e) => setDonationName(e.target.value)}
              className="donations-input-unique"
            />
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value)}
              className="donations-input-unique category-select-unique"
            >
              <option value="" disabled>
                카테고리
              </option>
              <option value="category1">지역상품권</option>
              <option value="category2">농축산물</option>
              <option value="category3">수산물</option>
              <option value="category4">가공식품</option>
              <option value="category5">공예품</option>
            </select>

            <input
              type="text"
              placeholder="가격"
              value={price}
              onChange={handlePriceChange}
              className="donations-input-unique"
            />

<div className="donations-image-upload-wrapper">
  <div className="donations-button-group">
    <label htmlFor="thumbnail" className="donations-custom-file-upload">
      대표 이미지
    </label>
    <input type="file" id="thumbnail" accept="image/*" onChange={handleThumbnailChange} />

    <label htmlFor="contentImage" className="donations-content-file-upload">
      내용 이미지
    </label>
    <input type="file" id="contentImage" accept="image/*" onChange={handleContentImageChange} />
  </div>

  {contentImage && (
    <div className="donations-content-image-preview-right">
      <img
        src={URL.createObjectURL(contentImage)}
        alt="내용 이미지 미리보기"
        className="donations-content-image-inside-preview-right"
      />
    </div>
  )}
</div>
          </div>
        </div>

        <div className="donations-description-section-unique">
          <div className="description-container">
            <textarea
              placeholder="설명을 입력하세요"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="donations-description-textarea-unique"
              ref={descriptionRef}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default CreateDonations;
