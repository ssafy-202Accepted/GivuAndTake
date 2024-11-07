import React, { useEffect, useState } from "react";
import Sidebar from "../Sidebar";
import "./Funding.css";
import { apiMyFunding } from "../../apis/funding/apiMyFunding";
import { useNavigate } from "react-router-dom";
import TokenManager from "../../utils/TokenManager";

const Funding = () => {
  const [selectedMenu, setSelectedMenu] = useState("펀딩");
  const navigate = useNavigate();

  // 펀딩 상태를 관리하는 state
  const [selectedStatus, setSelectedStatus] = useState("all");
  const [fundingList, setFundingList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6;

  // API에서 펀딩 데이터 불러오기
  const fetchMyFundingList = async (state) => {
    try {
      setLoading(true);
      const accessToken = TokenManager.getAccessToken();

      let data = [];
      if (state === "all") {
        // "전체" 선택 시 상태 0, 1, 2 데이터를 모두 가져옴
        const data0 = await apiMyFunding("0", accessToken);
        const data1 = await apiMyFunding("1", accessToken);
        const data2 = await apiMyFunding("2", accessToken);

        data = [...data0, ...data1, ...data2];
      } else {
        data = await apiMyFunding(state, accessToken);
      }

      setFundingList(data);
      
      // Zustand를 사용하여 fundingType을 상태로 저장
      const types = data.map((funding) => funding.fundingType);
      setFundingTypes(types); // fundingType 저장
    } catch (error) {
      console.error("펀딩 데이터를 가져오는 데 실패했습니다:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMyFundingList(selectedStatus); // 선택된 상태에 따라 데이터 불러오기
  }, [selectedStatus]);

  const handleFilterChange = (state) => {
    setSelectedStatus(state);
    setCurrentPage(1);
    fetchMyFundingList(state);
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = fundingList.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.max(1, Math.ceil(fundingList.length / itemsPerPage));

  return (
    <div className="funding-page-layout">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />
      <div className="funding-page-content">
        <div className="funding-header">
          <h1>펀딩 관리</h1>
          {/* 등록 버튼 추가 */}
          <button
            className="register-button"
            onClick={() => navigate("/funding/create-funding")}
          >
            등록
          </button>
        </div>

        <div className="filter-container">
          <div className="funding-status-buttons">
            <button
              className={`filter-button ${selectedStatus === "all" ? "active" : ""}`}
              onClick={() => handleFilterChange("all")}
            >
              전체
            </button>
            <button
              className={`filter-button ${selectedStatus === "0" ? "active" : ""}`}
              onClick={() => handleFilterChange("0")}
            >
              진행예정
            </button>
            <button
              className={`filter-button ${selectedStatus === "1" ? "active" : ""}`}
              onClick={() => handleFilterChange("1")}
            >
              진행중
            </button>
            <button
              className={`filter-button ${selectedStatus === "2" ? "active" : ""}`}
              onClick={() => handleFilterChange("2")}
            >
              완료
            </button>
          </div>
        </div>

        {loading ? (
  <div style={{ display: "none" }}>로딩 중...</div>
) : (
          <div className="funding-grid">
            {currentItems.length > 0 ? (
              currentItems.map((funding) => (
                <div
                  key={funding.fundingIdx}
                  className="funding-card"
                  onClick={() =>
                    navigate(`/funding/${funding.fundingIdx}`)
                  }
                >
                  <img
                    src={funding.thumbnail}
                    alt="펀딩 썸네일"
                    className="funding-thumbnail"
                  />
                  <div className="funding-details">
                    <h2 className="funding-title">{funding.fundingTitle}</h2>
                    <div className="funding-info">
                      <p className="end-date">마감일<br /> {funding.endDate}</p>
                    </div>
                  </div>
                  <div className="funding-rate">
                    달성률 <br />
                    {funding.goalMoney === 0 || funding.totalMoney === 0
                      ? "0%"
                      : `${Math.round((funding.totalMoney / funding.goalMoney) * 100)}%`}
                  </div>
                </div>
              ))
            ) : (
              <p>해당 조건에 맞는 펀딩이 없습니다.</p>
            )}
          </div>
        )}

        <div className="pagination">
          <span onClick={() => handlePageChange(1)}>&lt;&lt;</span>
          {Array.from({ length: totalPages }, (_, index) => (
            <span
              key={index}
              className={currentPage === index + 1 ? "active" : ""}
              onClick={() => handlePageChange(index + 1)}
            >
              {index + 1}
            </span>
          ))}
          <span onClick={() => handlePageChange(totalPages)}>&gt;&gt;</span>
        </div>
      </div>
    </div>
  );
};

export default Funding;
