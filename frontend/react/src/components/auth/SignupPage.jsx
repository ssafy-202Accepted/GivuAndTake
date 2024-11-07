import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom"; // 페이지 이동을 위한 useNavigate 추가
import Swal from "sweetalert2"; // SweetAlert2 추가
import "./SignupPage.css";
import { getSido, getSigungu, signUp } from "../../apis/auth/apiSignup"; // signUp API 함수 추가

const SignupPage = () => {
  const navigate = useNavigate(); // 페이지 이동을 위한 navigate 사용
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
    mobilePhone: "",
    landlinePhone: "",
    sido: "",
    sigungu: "",
  });

  const [profileImage, setProfileImage] = useState(null); // 프로필 이미지 파일 상태 추가
  const [passwordValid, setPasswordValid] = useState(null); // 비밀번호 유효성 상태 추가
  const [passwordMatch, setPasswordMatch] = useState(null); // 비밀번호 일치 여부 상태 추가
  const [sidoOptions, setSidoOptions] = useState([]); // 시도 옵션 데이터
  const [sigunguOptions, setSigunguOptions] = useState([]); // 시군구 옵션 데이터

  // 비밀번호 형식 체크 함수
  const validatePassword = (password) => {
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!\"#$%&'()*+,\-./:;<=>?@[\\\]^_`{|}~]).{8,16}$/;
    return passwordRegex.test(password);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    console.log(`입력 변경됨 - 필드: ${name}, 값: ${value}`); // 입력값 디버깅 로그 추가
    setFormData({ ...formData, [name]: value });

    if (name === "password") {
      setPasswordValid(validatePassword(value)); // 비밀번호 유효성 검사
    }

    if (name === "confirmPassword" || name === "password") {
      const password = name === "password" ? value : formData.password;
      const confirmPassword =
        name === "confirmPassword" ? value : formData.confirmPassword;
      if (password && confirmPassword) {
        setPasswordMatch(password === confirmPassword);
      } else {
        setPasswordMatch(null); // 하나라도 입력되지 않았으면 상태 초기화
      }
    }
  };

  // 시도 데이터를 가져오는 함수 (드롭다운 클릭 시 호출)
  const handleSidoClick = async () => {
    try {
      const response = await getSido(); // 시도 데이터 API 호출
      if (response.success) {
        setSidoOptions(response.data); // 시도 데이터를 상태로 설정
      }
    } catch (error) {
      console.error("Failed to fetch 시도 data:", error);
    }
  };

  // 시도를 선택할 때 시군구 데이터를 가져오는 함수
  const handleSidoChange = async (e) => {
    const selectedSido = e.target.value;
    setFormData({ ...formData, sido: selectedSido, sigungu: "" }); // 시도 변경 시 시군구 초기화
  
    try {
      const response = await getSigungu(selectedSido);
      if (response.success) {
        setSigunguOptions(response.data); // 시군구 데이터를 상태로 설정
      }
    } catch (error) {
      console.error("Failed to fetch 시군구 data:", error);
    }
  };
  
// 회원가입 처리 함수
const handleSubmit = async (e) => {
  e.preventDefault();

  console.log("SignupPage에서 전송할 formData:", formData); // formData 확인
  console.log("SignupPage에서 전송할 profileImage:", profileImage); // profileImage 확인

  // 프로필 이미지가 null일 경우 빈 문자열로 처리
  const processedProfileImage = profileImage || ""; // null 또는 undefined일 경우 빈 문자열로 대체

  if (passwordValid && passwordMatch) {
    try {
      const response = await signUp(formData, processedProfileImage); // 빈 문자열로 처리된 프로필 이미지 전달
      console.log("회원가입 성공:", response);
      
      Swal.fire({
        icon: "success",
        title: "회원가입이 완료되었습니다!",
        confirmButtonText: "확인",
      }).then(() => {
        navigate("/login");
      });
    } catch (error) {
      console.error("회원가입 실패:", error);
      Swal.fire({
        icon: "error",
        title: "회원가입이 실패하였습니다.",
        text: "다시 시도해주세요.",
        confirmButtonText: "확인",
      });
    }
  } else {
    console.log("유효하지 않은 입력입니다.");
  }
};

return (
    <div className="signup-container">
      <h2>회원가입</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <div className="label-container">
            <label>이름</label>
          </div>
          <div className="input-container">
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              required
            />
          </div>
        </div>

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
              placeholder="대문자, 소문자, 숫자, 특수문자 각 1개 포함, 8~16글자"
              required
            />
          </div>
          {passwordValid === false && (
            <p style={{ color: "red" }}>비밀번호 형식이 맞지 않습니다.</p>
          )}
          {passwordValid === true && (
            <p style={{ color: "green" }}>비밀번호가 유효합니다.</p>
          )}
        </div>

        <div className="form-group">
          <div className="label-container">
            <label>비밀번호 확인</label>
          </div>
          <div className="input-container">
            <input
              type="password"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleInputChange}
              required
            />
          </div>
          {passwordMatch === false && (
            <p style={{ color: "red" }}>비밀번호가 일치하지 않습니다.</p>
          )}
          {passwordMatch === true && (
            <p style={{ color: "green" }}>비밀번호가 일치합니다.</p>
          )}
        </div>

        <div className="form-group">
          <div className="label-container">
            <label>전화번호</label>
          </div>
          <div className="input-container">
            <input
              type="tel"
              name="landlinePhone"
              value={formData.landlinePhone}
              onChange={handleInputChange}
              placeholder="ex) 02-1234-5678"
              required
            />
          </div>
        </div>

        <div className="form-group">
          <div className="label-container">
            <label>휴대폰 번호</label>
          </div>
          <div className="input-container">
            <input
              type="tel"
              name="mobilePhone"
              value={formData.mobilePhone}
              onChange={handleInputChange}
              placeholder="ex) 010-1234-5678"
              required
            />
          </div>
        </div>

        <div className="flex-container">
          <div className="form-group">
            <div className="label-container">
              <label>시도</label>
            </div>
            <div className="input-container">
              <select
                name="sido"
                value={formData.sido}
                onClick={handleSidoClick}
                onChange={handleSidoChange}
                required
              >
                <option value="">시도 선택</option>
                {sidoOptions.map((sido, index) => (
                  <option key={index} value={sido}>
                    {sido}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group">
            <div className="label-container">
              <label>시군구</label>
            </div>
            <div className="input-container">
              <select
                name="sigungu"
                value={formData.sigungu}
                onChange={handleInputChange}
                required
              >
                <option value="">시군구 선택</option>
                {sigunguOptions.map((sigungu, index) => (
                  <option key={index} value={sigungu}>
                    {sigungu}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        <div className="form-group">
          <div className="label-container">
            <label>프로필 이미지</label>
          </div>
          <div className="input-container">
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setProfileImage(e.target.files[0])} // 이미지 선택
            />
          </div>
        </div>

        <button type="submit" className="submit-button">
          가입하기
        </button>
      </form>
    </div>
  );
};

export default SignupPage;
