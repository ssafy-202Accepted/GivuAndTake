import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2"; // SweetAlert2 추가
import "./LoginPage.css";
import { login } from "../../apis/auth/apiLogin"; // 로그인 API 호출 함수 추가

const LoginPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await login(formData); // 로그인 API 호출
      console.log("로그인 성공:", response);

      // 로그인 성공 시 모달 창 띄우기
      Swal.fire({
        icon: "success",
        title: "로그인에 성공했습니다!",
        confirmButtonText: "확인",
      }).then(() => {
        // 모달 확인 버튼 클릭 시 메인 페이지로 이동
        navigate("/donations");
      });
    } catch (error) {
      console.error("로그인 실패:", error);

      // 로그인 실패 시 모달 창 띄우기
      Swal.fire({
        icon: "error",
        title: "로그인에 실패했습니다.",
        text: "이메일 또는 비밀번호를 확인하세요.",
        confirmButtonText: "확인",
      });
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h2>로그인</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <div className="label-container">
              <label>이메일</label>
            </div>
            <div className="input-container">
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <div className="label-container">
              <label>비밀번호</label>
            </div>
            <div className="input-container">
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleInputChange}
                required
              />
            </div>
          </div>

          <button type="submit" className="submit-button">
            로그인
          </button>
        </form>

        <div className="signup-link">
          <p>아직 계정이 없으신가요?</p>
          <button onClick={() => navigate("/signup")}>회원가입</button>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
