import React, { useState, useEffect } from "react";
import Sidebar from "../Sidebar";
import { getUserInfo } from "../../apis/auth/apiUserInfo";
import { updateUserInfo } from "../../apis/auth/apiUserInfoUpdate"; // 회원정보 수정 API import
import { useNavigate } from "react-router-dom"; // 페이지 이동을 위한 useNavigate 훅
import Swal from "sweetalert2"; // SweetAlert2 import
import "./UserInfoUpdate.css";

const UserInfoUpdate = () => {
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

  const navigate = useNavigate(); // navigate 사용

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const data = await getUserInfo(); // 기존 정보를 가져옴
        setUserInfo(data); // 상태 업데이트
      } catch (error) {
        console.error("사용자 정보 불러오기 실패:", error);
      }
    };

    fetchUserInfo(); // 컴포넌트가 마운트될 때 호출
  }, []);

  // 입력값이 변경될 때 userInfo 상태 업데이트
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setUserInfo({ ...userInfo, [name]: value });
  };

  const handleSave = async () => {
    try {
      // 수정된 값을 API에 전달하여 업데이트
      const updatedUser = {
        name: userInfo.name,
        mobilePhone: userInfo.mobilePhone,
        landlinePhone: userInfo.landlinePhone,
        profileImageUrl: userInfo.profileImageUrl,
      };

      // API 호출로 회원정보 수정 요청
      const response = await updateUserInfo(updatedUser);

      // 성공 시 SweetAlert2 모달창 띄우기
      Swal.fire({
        title: "회원정보가 수정되었습니다.",
        icon: "success",
        confirmButtonText: "확인",
      }).then((result) => {
        if (result.isConfirmed) {
          navigate("/userinfo"); // 확인 버튼 누르면 userinfo 페이지로 이동
        }
      });

      console.log("회원정보 수정 성공:", response);
    } catch (error) {
      console.error("회원정보 수정 실패:", error);
      alert("회원정보 수정에 실패했습니다.");
    }
  };

  // 취소 버튼 핸들러: 취소 시 회원 정보 페이지로 이동
  const handleCancel = () => {
    navigate("/userinfo");
  };

  return (
    <div className="userinfoupdate-page">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />
      <div className="userinfoupdate-container">
        <div className="userinfoupdate-header">
          <button className="userinfoupdate-save-button" onClick={handleSave}>
            저장
          </button>
          <button className="userinfoupdate-cancel-button" onClick={handleCancel}>
            취소
          </button> {/* 취소 버튼 */}
        </div>
        <div className="userinfoupdate-content">
          <div className="userinfoupdate-profile-picture">
            <img
              src={userInfo.profileImageUrl || "https://cdn.idomin.com/news/photo/202108/769634_452493_1211.jpg"}
              alt="프로필 사진"
              className="userinfoupdate-profile-img"
            />
          </div>
          <div className="userinfoupdate-details">
            <label className="userinfoupdate-label">
              <strong>이름 :</strong>
              <input
                type="text"
                name="name"
                value={userInfo.name}
                onChange={handleInputChange}
                className="userinfoupdate-input-field"
              />
            </label>
            <p className="userinfoupdate-region-name">{`${userInfo.sido} ${userInfo.sigungu}`}</p>
            <label className="userinfoupdate-label">
              <strong>이메일 :</strong>
              <input
                type="email"
                name="email"
                value={userInfo.email}
                className="userinfoupdate-input-field"
                disabled
              />
            </label>
            <label className="userinfoupdate-label">
              <strong>유선번호 :</strong>
              <input
                type="text"
                name="landlinePhone"
                value={userInfo.landlinePhone}
                onChange={handleInputChange}
                className="userinfoupdate-input-field"
              />
            </label>
            <label className="userinfoupdate-label">
              <strong>휴대폰번호 :</strong>
              <input
                type="text"
                name="mobilePhone"
                value={userInfo.mobilePhone}
                onChange={handleInputChange}
                className="userinfoupdate-input-field"
              />
            </label>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserInfoUpdate;
