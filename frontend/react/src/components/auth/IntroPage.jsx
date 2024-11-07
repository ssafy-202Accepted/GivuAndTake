import React from 'react';
import { useNavigate } from 'react-router-dom';
import './IntroPage.css';
import loginIcon from '../../assets/login_icon.png'; // 로그인 아이콘 이미지 경로
import signupIcon from '../../assets/signup_icon.png'; // 회원가입 아이콘 이미지 경로

const IntroPage = () => {
  const navigate = useNavigate();

  return (
    <div className="intro-page">
      <div className="title-container">
        <h1 className="intro-title">GIVU<br />&nbsp;&amp;<br />TAKE</h1>
      </div>

      <div className="auth-cards">
        {/* 로그인 아이콘 */}
        <div className="auth-card" onClick={() => navigate('/login')}>
          <img src={loginIcon} alt="로그인" className="auth-icon" />
        </div>


        {/* 회원가입 아이콘과 설명 텍스트 */}
        <div className="auth-card" onClick={() => navigate('/signup')}>
          <img src={signupIcon} alt="회원가입" className="auth-icon" />
          <p className="subtext">지자체만 회원가입이 가능합니다</p> {/* 회원가입 설명 */}
        </div>
      </div>
    </div>
  );
};

export default IntroPage;