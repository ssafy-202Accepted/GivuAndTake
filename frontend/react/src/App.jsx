import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import MainPage from './components/mainPage/MainPage';
import SignupPage from './components/auth/SignupPage';
import LoginPage from './components/auth/LoginPage';
import MyPage from './components/myPage/MyPage';
import IntroPage from './components/auth/IntroPage';

import Donations from './components/donations/Donations';
import CreateDonations from './components/donations/CreateDonations';
import DonationsDetail from './components/donations/DonationsDetail';



import Funding from './components/funding/Funding';
import FundingDetail from './components/funding/FundingDetail';
import CreateFunding from "./components/funding/CreateFunding";
// import UpdateFunding from './components/funding/UpdateFunding';

// import Statistics from './components/statistics/Statistics';
import FundingStatistics from './components/statistics/FundingStatistics';
import DonationsStatistics from './components/statistics/DonationsStatistics';



import UserInfo from './components/userinfo/UserInfo';
import UserInfoUpdate from './components/userinfo/UserInfoUpdate';


import React from 'react';

const App = () => {
  
  return (
    <Router>
      
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/signup" element={<SignupPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/intro" element={<IntroPage />} />


        <Route path="/userinfo" element={<UserInfo />} />
        <Route path="/userinfoupdate" element={<UserInfoUpdate />} />

        <Route path="/donations" element={<Donations />} />
        <Route path="/donations/create-donation" element={<CreateDonations />} />
        <Route path="/donations/:giftIdx" element={<DonationsDetail />} /> {/* 상세 페이지 경로 */}


        <Route path="/funding" element={<Funding />} />
        <Route path="/funding/:fundingIdx" element={<FundingDetail />} /> {/* 상세 페이지 경로 */}
        <Route path="/funding/create-funding" element={<CreateFunding />} />
        {/* <Route path="/funding/update/:fundingIdx" element={<UpdateFunding />} /> */}


        {/* <Route path="/statistics" element={<Statistics />} /> */}
        <Route path="/funding-statistics" element={<FundingStatistics />} />
        <Route path="/donation-statistics" element={<DonationsStatistics />} />


      </Routes>
    </Router>
  );
};

export default App;
