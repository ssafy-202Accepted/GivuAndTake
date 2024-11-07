import React, { useState, useCallback } from "react";
import Sidebar from "../Sidebar";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import TokenManager from "../../utils/TokenManager";
import { apiCreateFunding } from "../../apis/funding/apiCreateFunding";
import Swal from "sweetalert2";
import { useNavigate } from "react-router-dom";
import "./CreateFunding.css";

// debounce 함수 정의
const debounce = (func, delay) => {
  let timeoutId;
  return (...args) => {
    if (timeoutId) clearTimeout(timeoutId);
    timeoutId = setTimeout(() => {
      func(...args);
    }, delay);
  };
};

const CreateFunding = () => {
  const [fundingName, setFundingName] = useState("");
  const [goalAmount, setGoalAmount] = useState("");
  const [description, setDescription] = useState("");
  const [selectedMenu, setSelectedMenu] = useState("펀딩");

  const [fundingType, setFundingType] = useState("D");

  const [startDate, setStartDate] = useState(null);
  const [endDate, setEndDate] = useState(null);

  const [thumbnail, setThumbnail] = useState(null);
  const [contentImage, setContentImage] = useState(null);

  const navigate = useNavigate();

  // 금액 형식화 함수 (천 단위로 쉼표 추가)
  const formatNumber = (value) => {
    const number = value.replace(/[^0-9]/g, "");
    return new Intl.NumberFormat().format(number);
  };

  const handleGoalAmountChange = (e) => {
    const formattedValue = formatNumber(e.target.value);
    setGoalAmount(formattedValue);
  };

  // 썸네일 이미지 선택 핸들러
  const handleThumbnailChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setThumbnail(file); // 파일 객체를 저장
    } else {
      setThumbnail(null); // 파일이 없을 경우 null로 설정
    }
  };

  // 내용 이미지 선택 핸들러
  const handleContentImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setContentImage(file); // 파일 객체를 바로 저장
    }
  };

  const setKoreanTime = (date) => {
    if (!date) return null;
    const adjustedDate = new Date(date);
    adjustedDate.setHours(9, 0, 0, 0);
    return adjustedDate.toISOString().split("T")[0];
  };

  // debounce 적용된 설명 업데이트 핸들러
  const debouncedSetDescription = useCallback(
    debounce((value) => {
      setDescription(value);
    }, 1000),
    []
  );

  const handleSave = async () => {
    const accessToken = TokenManager.getAccessToken();

    const fundingData = {
      fundingTitle: fundingName,
      fundingContent: description,
      goalMoney: parseInt(goalAmount.replace(/,/g, "")),
      startDate: setKoreanTime(startDate),
      endDate: setKoreanTime(endDate),
      fundingThumbnail: thumbnail || null, // 썸네일이 없으면 null로 설정
      contentImage: contentImage || null, // contentImage도 함께 전송
      fundingType: fundingType,
    };

    try {
      const result = await apiCreateFunding(fundingData, accessToken);
      Swal.fire({
        title: "펀딩 등록 성공",
        text: "펀딩이 성공적으로 등록되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      }).then(() => {
        navigate("/funding");
      });
    } catch (error) {
      if (error.response && error.response.data.code === "ES0004") {
        Swal.fire({
          title: "펀딩 등록 실패",
          text: "모금 시작일은 현재 날짜 이후여야 합니다.",
          icon: "error",
          confirmButtonText: "확인",
        });
      } else {
        Swal.fire({
          title: "오류",
          text: "펀딩 등록에 실패했습니다.",
          icon: "error",
          confirmButtonText: "확인",
        });
      }
    }
  };

  return (
    <div className="create-funding-layout">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />

      <div className="create-funding-content">
        <div className="header">
          <h1>펀딩 등록</h1>
          <button className="save-button" onClick={handleSave}>
            등록
          </button>
        </div>

        <div className="funding-details-container">
          <div className="thumbnail">
            {thumbnail ? (
              <img src={URL.createObjectURL(thumbnail)} alt="썸네일 미리보기" className="thumbnail-preview" />
            ) : (
              <div className="thumbnail-placeholder">썸네일</div>
            )}
          </div>

          <div className="details-section">
            <input
              type="text"
              placeholder="펀딩명"
              className="funding-name"
              value={fundingName}
              onChange={(e) => setFundingName(e.target.value)}
            />
            <div className="funding-type">
              <label>
                <input
                  type="radio"
                  name="fundingType"
                  value="D"
                  checked={fundingType === "D"}
                  onChange={(e) => setFundingType(e.target.value)}
                />{" "}
                재난재해
              </label>
              <label>
                <input
                  type="radio"
                  name="fundingType"
                  value="R"
                  checked={fundingType === "R"}
                  onChange={(e) => setFundingType(e.target.value)}
                />{" "}
                지역기부
              </label>
            </div>

            <div className="dates">
              <div className="date-picker">
                <label>시작일:</label>
                <DatePicker
                  selected={startDate}
                  onChange={(date) => setStartDate(date)}
                  dateFormat="yyyy-MM-dd"
                  placeholderText="날짜를 선택하세요"
                />
              </div>

              <div className="date-picker">
                <label>마감일:</label>
                <DatePicker
                  selected={endDate}
                  onChange={(date) => setEndDate(date)}
                  dateFormat="yyyy-MM-dd"
                  placeholderText="날짜를 선택하세요"
                />
              </div>
            </div>

            <div className="goal-amount">
              <input
                type="text"
                placeholder="목표 금액"
                value={goalAmount}
                onChange={handleGoalAmountChange}
              />
            </div>

            <div className="image-upload-container">
              <label htmlFor="thumbnail" className="custom-file-upload">
                대표 이미지
              </label>
              <input
                type="file"
                id="thumbnail"
                accept="image/*"
                style={{ display: "none" }}
                onChange={handleThumbnailChange}
              />

              <label htmlFor="contentImage" className="custom-file-upload content-image-button">
                내용 이미지
              </label>
              <input
                type="file"
                id="contentImage"
                accept="image/*"
                style={{ display: "none" }}
                onChange={handleContentImageChange}
              />

              {/* 내용 이미지 미리보기 버튼 오른쪽에 표시 */}
              {contentImage && (
                <img
                  src={URL.createObjectURL(contentImage)}
                  alt="내용 이미지 미리보기"
                  className="content-image-preview"
                />
              )}
            </div>
          </div>
        </div>

        <div className="description-section">
          {/* 이미지 및 텍스트를 함께 편집할 수 있는 영역 */}
          <div
            contentEditable={true}
            className="description-editor"
            onInput={(e) => debouncedSetDescription(e.currentTarget.innerHTML)}
            dangerouslySetInnerHTML={{ __html: description }}
          />
        </div>
      </div>
    </div>
  );
};

export default CreateFunding;
