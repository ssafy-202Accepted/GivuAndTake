import React from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate import 추가
import './MainPage.css';
import qr_code from "../../assets/qr_code.png";
import download from "../../assets/down.png";
import city from "../../assets/city.png";


const MainPage = () => {
  const navigate = useNavigate(); // useNavigate 훅 사용

  return (
    <div className="main-page">
      <div className="content">
        <div className="header">
          {/* 회원가입과 로그인 버튼 */}
          {/* <div className="auth-buttons">
            <button className="login-button" onClick={() => navigate('/login')}>로그인</button>
            <button className="signup-button" onClick={() => navigate('/signup')}>회원가입</button>
          </div> */}
        </div>

        {/* 타이틀 */}
        <h1 className="title">GIVU<br />&nbsp;&amp;<br />TAKE</h1>

        {/* 다운로드 버튼과 QR 코드 컨테이너 */}
        <div className="download-qr-container">
          <div className="download-button">
            <a href="https://j11e202.p.ssafy.io/download/app.apk" className="app-download">
              <img src={download} className="downloadImg" alt="앱 다운로드"
                style={{ cursor: 'pointer' }} /* 이미지에도 커서를 설정 */
                />
            </a>
          </div>

          <div className="qr-code">
            <p className="qrtext">QR 코드 다운로드</p>
            <img src={qr_code} className="qrImg" alt="QR 코드" />
          </div>
        </div>
      </div>

      {/* city 이미지에 onClick 이벤트 추가 */}
      <img
        src={city}
        className="cityImg"
        alt="city"
        onClick={() => navigate('/intro')} // 클릭 시 /intro 페이지로 이동
        style={{ cursor: 'pointer' }} // 마우스 포인터가 클릭할 수 있음을 나타냄
      />
    </div>
  );
};

export default MainPage;
