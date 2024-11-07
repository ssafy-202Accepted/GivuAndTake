import React, { useEffect, useState } from "react";
import Sidebar from "../Sidebar"; // Sidebar import
import "./Donations.css"; // 공통 CSS 파일
import { apiMyDonations } from "../../apis/donations/apiMyDonations"; // API 함수 import
import { getUserInfo } from "../../apis/auth/apiUserInfo"; // getUserInfo import
import { useNavigate, useLocation } from "react-router-dom"; // useNavigate 및 useLocation import

const Donations = () => {
  const navigate = useNavigate(); // useNavigate 훅
  const location = useLocation(); // 현재 URL 정보를 가져오기 위한 useLocation 훅

  const [selectedMenu, setSelectedMenu] = useState("기부품");

  // URL 쿼리 파라미터에서 selectedType 가져오기 (없으면 기본값은 "전체")
  const queryParams = new URLSearchParams(location.search);
  const initialSelectedType = queryParams.get("type") || "전체";
  const [selectedType, setSelectedType] = useState(initialSelectedType);

  const [donationList, setDonationList] = useState([]);
  const [loading, setLoading] = useState(true); // 로딩 상태 추가
  const [corporationEmail, setCorporationEmail] = useState(""); // corporationEmail을 저장할 상태

  // 페이지네이션 관련 상태
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 6; // 페이지당 표시할 항목 수

  // 가격을 천 단위로 쉼표로 구분해주는 함수
  const formatPrice = (price) => {
    return new Intl.NumberFormat().format(price) + "원";
  };

  // 사용자 정보를 가져오는 함수 (email 포함)
  const fetchUserInfo = async () => {
    try {
      const userInfo = await getUserInfo(); // getUserInfo 호출
      setCorporationEmail(userInfo.email); // email을 corporationEmail로 설정
    } catch (error) {
      console.error("사용자 정보를 가져오는 데 실패했습니다:", error);
    }
  };

  // 기부품 조회 API 호출 함수
  const fetchDonationList = async () => {
    if (!corporationEmail) return; // corporationEmail이 없으면 API 호출 중단
    try {
      const data = await apiMyDonations(corporationEmail); // email로 API에서 기부품 데이터 가져오기
      setDonationList(data); // 받아온 데이터를 state에 저장
    } catch (error) {
      console.error("기부품 데이터를 가져오는 데 실패했습니다:", error);
    } finally {
      setLoading(false); // 데이터 로딩이 끝났으므로 로딩 상태 false로 설정
    }
  };

  // 컴포넌트 마운트 시 사용자 정보와 기부품 리스트를 가져옴
  useEffect(() => {
    fetchUserInfo(); // 사용자 정보 불러오기
  }, []);

  // corporationEmail이 설정된 후 기부품 리스트를 가져옴
  useEffect(() => {
    if (corporationEmail) {
      fetchDonationList(); // 기부품 리스트 불러오기
    }
  }, [corporationEmail, currentPage]); // corporationEmail과 페이지가 변경될 때마다 데이터 갱신

  // 필터 버튼 클릭 시 상태 업데이트 및 URL 파라미터 갱신
  const handleFilterChange = (type) => {
    setSelectedType(type);
    setCurrentPage(1); // 필터가 변경될 때 첫 페이지로 이동
    navigate(`?type=${type}`); // URL 쿼리 파라미터 갱신
  };

  // 페이지 변경 핸들러
  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  // 선택된 필터에 맞는 기부품 목록을 필터링
  const filteredDonations = selectedType
    ? selectedType === "전체"
      ? donationList
      : donationList.filter((donation) => donation.categoryName === selectedType)
    : donationList;

  // 현재 페이지에서 보여줄 데이터 계산
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = filteredDonations.slice(indexOfFirstItem, indexOfLastItem);

  // 총 페이지 수 계산
  const totalPages = Math.max(1, Math.ceil(filteredDonations.length / itemsPerPage));

  return (
    <div className="donations-page-layout">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />
      <div className="donations-page-content">
        <div className="donations-header">
          <h1>기부품 관리</h1>
          <button className="register-button" onClick={() => navigate("/donations/create-donation")}>
            등록
          </button>
        </div>
        <div className="donations-category-and-dropdown">
          <div className="donations-type-buttons">
            <button
              className={`filter-button ${selectedType === "전체" ? "active" : ""}`}
              onClick={() => handleFilterChange("전체")}
            >
              전체
            </button>
            <button
              className={`filter-button ${selectedType === "지역상품권" ? "active" : ""}`}
              onClick={() => handleFilterChange("지역상품권")}
            >
              지역상품권
            </button>
            <button
              className={`filter-button ${selectedType === "농축산물" ? "active" : ""}`}
              onClick={() => handleFilterChange("농축산물")}
            >
              농축산물
            </button>
            <button
              className={`filter-button ${selectedType === "수산물" ? "active" : ""}`}
              onClick={() => handleFilterChange("수산물")}
            >
              수산물
            </button>
            <button
              className={`filter-button ${selectedType === "가공식품" ? "active" : ""}`}
              onClick={() => handleFilterChange("가공식품")}
            >
              가공식품
            </button>
            <button
              className={`filter-button ${selectedType === "공예품" ? "active" : ""}`}
              onClick={() => handleFilterChange("공예품")}
            >
              공예품
            </button>
          </div>
        </div>

        {/* 데이터 로딩 중일 때 표시 */}
        {loading ? (
          <p>로딩 중...</p>
        ) : (
          <div className="donations-grid">
            {currentItems.length > 0 ? (
              currentItems.map((donation) => (
                <div
                  key={donation.giftIdx} // giftIdx로 key 설정
                  className="donation-card"
                  onClick={() => navigate(`/donations/${donation.giftIdx}`)} // 클릭 시 상세 페이지로 이동
                >
                  <div className="donation-image-placeholder">
                    <img src={donation.giftThumbnail} alt={donation.giftName} />
                  </div>
                  <div className="donation-details">
                    <h2 className="donation-title">{donation.giftName}</h2>
                    <p className="donation-price">가격: {formatPrice(donation.price)}</p>
                  </div>
                </div>
              ))
            ) : (
              <p>해당 조건에 맞는 기부품이 없습니다.</p>
            )}
          </div>
        )}

        {/* 페이지네이션 */}
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

export default Donations;
