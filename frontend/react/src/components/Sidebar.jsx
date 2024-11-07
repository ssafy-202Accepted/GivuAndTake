import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './Sidebar.css'; // Sidebar의 스타일
import { getUserInfo } from '../apis/auth/apiUserInfo'; // getUserInfo API 호출
import useProfileStore from '../store/profileStore'; // zustand store 불러오기

const Sidebar = ({ selectedMenu, setSelectedMenu }) => {
  const menuItems = ['기부품', '펀딩', '통계', '회원정보'];
  const [isStatisticsOpen, setIsStatisticsOpen] = useState(false); // 통계 탭 하위 메뉴 열림 상태
  const [selectedSubMenu, setSelectedSubMenu] = useState(''); // 서브메뉴 선택 상태 추가
  const profileImageUrl = useProfileStore((state) => state.profileImageUrl); // zustand에서 상태 가져오기
  const sido = useProfileStore((state) => state.sido); // zustand에서 sido 상태 가져오기
  const sigungu = useProfileStore((state) => state.sigungu); // zustand에서 sigungu 상태 가져오기
  const setProfileImageUrl = useProfileStore((state) => state.setProfileImageUrl); // zustand에서 상태 설정 함수 가져오기
  const setSido = useProfileStore((state) => state.setSido); // zustand에서 setSido 가져오기
  const setSigungu = useProfileStore((state) => state.setSigungu); // zustand에서 setSigungu 가져오기
  const navigate = useNavigate();
  const location = useLocation(); // 현재 경로를 얻기 위한 훅

  // 사용자 정보 가져오기
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const data = await getUserInfo(); // API 호출하여 사용자 정보 가져오기
        setProfileImageUrl(data.profileImageUrl); // 전역 상태로 프로필 이미지 URL 설정
        setSido(data.sido); // 전역 상태로 sido 설정
        setSigungu(data.sigungu); // 전역 상태로 sigungu 설정
      } catch (error) {
        console.error('프로필 이미지 불러오기 실패:', error);
      }
    };

    fetchUserInfo(); // 컴포넌트 마운트 시 사용자 정보 가져오기
  }, [setProfileImageUrl, setSido, setSigungu]); // 상태 설정 함수에 의존성 추가

  // 페이지 이동 후에도 통계 서브메뉴 상태를 유지
  useEffect(() => {
    if (location.pathname.includes('/funding-statistics')) {
      setIsStatisticsOpen(true);
      setSelectedSubMenu('펀딩 통계'); // 펀딩 통계 선택
    } else if (location.pathname.includes('/donation-statistics')) {
      setIsStatisticsOpen(true);
      setSelectedSubMenu('기부품 통계'); // 기부품 통계 선택
    }
  }, [location]);

  const handleMenuClick = (item) => {
    if (item === '통계') {
      setIsStatisticsOpen(!isStatisticsOpen); // 통계 하위 메뉴 토글
    } else {
      setSelectedMenu(item);
      setIsStatisticsOpen(false); // 다른 메뉴 클릭 시 서브메뉴 닫기
      setSelectedSubMenu(''); // 서브메뉴 초기화
      if (item === '기부품') {
        navigate('/donations');
      } else if (item === '펀딩') {
        navigate('/funding');
      } else if (item === '회원정보') {
        navigate('/userinfo');
      }
    }
  };

  const handleSubMenuClick = (subMenu) => {
    setSelectedMenu('통계');
    setSelectedSubMenu(subMenu); // 서브메뉴 선택 상태 업데이트
    if (subMenu === '기부품 통계') {
      navigate('/donation-statistics');
    } else if (subMenu === '펀딩 통계') {
      navigate('/funding-statistics');
    }
  };

  return (
    <div className="sidebar">
      <div className="header">GIVU & TAKE</div>
      <div className="profile-section">
        <img
          src={profileImageUrl || "https://cdn.idomin.com/news/photo/202108/769634_452493_1211.jpg"} // profileImageUrl 사용
          alt="프로필사진"
          className="profile-image"
        />
        <div className = "location">{sido} {sigungu}</div> {/* sido와 sigungu를 사용자명 위치에 추가 */}
      </div>
      <div className="menu">
        {menuItems.map((item) => (
          <div key={item}>
            <div
              className={`menu-item ${selectedMenu === item ? 'menu-item-active' : ''}`}
              onClick={() => handleMenuClick(item)}
            >
              {item}
            </div>
            {item === '통계' && isStatisticsOpen && (
              <div className="submenu">
                <div
                  className={`submenu-item ${selectedSubMenu === '기부품 통계' ? 'submenu-item-active' : ''}`}
                  onClick={() => handleSubMenuClick('기부품 통계')}
                >
                  기부품 통계
                </div>
                <div
                  className={`submenu-item ${selectedSubMenu === '펀딩 통계' ? 'submenu-item-active' : ''}`}
                  onClick={() => handleSubMenuClick('펀딩 통계')}
                >
                  펀딩 통계
                </div>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default Sidebar;
