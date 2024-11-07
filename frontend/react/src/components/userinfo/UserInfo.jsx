import React, { useState, useEffect } from "react";
import Sidebar from "../Sidebar";
import TokenManager from "../../utils/TokenManager";
import { getUserInfo } from "../../apis/auth/apiUserInfo";
import { useNavigate } from "react-router-dom"; // useNavigate 훅 추가
import Swal from "sweetalert2";
import "./UserInfo.css";

const UserInfo = () => {
  const [selectedMenu, setSelectedMenu] = useState("회원정보");
  const [userInfo, setUserInfo] = useState({
    name: "",
    email: "",
    mobilePhone: "",
    landlinePhone: "",
    sido: "",
    sigungu: "",
    profileImageUrl: "",
  });

  const navigate = useNavigate(); // navigate 선언

  const handleLogout = () => {
    // SweetAlert2를 사용해 모달창 띄우기
    Swal.fire({
      title: "로그아웃 하시겠습니까?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#c2a6f1", // 확인 버튼 색상 변경
      cancelButtonColor: "#ffffff", // 취소 버튼 배경을 흰색으로 설정
      confirmButtonText: "확인",
      cancelButtonText: "취소",
      customClass: {
        cancelButton: 'custom-cancel-btn', // 커스텀 클래스를 취소 버튼에 적용
        confirmButton: 'custom-confirm-btn' // 커스텀 클래스를 확인 버튼에 적용
      }
    }).then((result) => {
      if (result.isConfirmed) {
        // 로그아웃 확정 시 토큰 삭제 및 로그인 페이지로 이동
        TokenManager.clearTokens(); // 토큰 삭제
        navigate("/login"); // 로그인 페이지로 이동
      }
    });
  };

  // API 호출로 사용자 정보를 가져옴
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const data = await getUserInfo(); // API 호출
        setUserInfo(data); // 가져온 데이터로 상태 업데이트
      } catch (error) {
        console.error("사용자 정보 불러오기 실패:", error);
      }
    };

    fetchUserInfo(); // 컴포넌트가 마운트될 때 호출
  }, []);

  return (
    <div className="user-info-page">
      {/* Sidebar에 userInfo의 profileImageUrl을 전달 */}
      <Sidebar 
        selectedMenu={selectedMenu} 
        setSelectedMenu={setSelectedMenu} 
        profileImageUrl={userInfo.profileImageUrl} // 프로필 이미지 URL 전달
      />
      <div className="user-info-container">
        <div className="user-info-header">
          <button className="edit-button" onClick={() => navigate("/userinfoupdate")}>
            수정
          </button>
        </div>
        <div className="user-info-content">
          <div className="profile-picture">
            <img
              src={userInfo.profileImageUrl || "https://cdn.idomin.com/news/photo/202108/769634_452493_1211.jpg"}
              alt="프로필 사진"
              className="profile-img"
            />
          </div>
          <div className="user-details">
            <h2 className="user-name">{userInfo.name}</h2> {/* name */}
            <p className="region-name">{`${userInfo.sido} ${userInfo.sigungu}`}</p> {/* sido, sigungu */}
            <p className="user-info-email">
              <strong>이메일 :</strong> {userInfo.email}
            </p>
            <p className="user-info-landlinePhone">
              <strong>유선번호 :</strong> {userInfo.landlinePhone}
            </p>
            <p className="user-info-mobilePhone">
              <strong>휴대폰번호 :</strong> {userInfo.mobilePhone}
            </p>
          </div>
        </div>
        <div className="user-info-footer">
          <button className="logout-button" onClick={handleLogout}>
            로그아웃
          </button>
        </div>
      </div>
    </div>
  );
};

export default UserInfo;
