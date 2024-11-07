import React, { useState } from 'react';
import Sidebar from '../Sidebar'; // 왼쪽 메뉴
import Content from './Content'; // 오른쪽 컨텐츠

const MyPage = () => {
  const [selectedMenu, setSelectedMenu] = useState('기부품'); // 기본 메뉴는 '기부품'

  return (
    <div style={styles.pageContainer}>
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />
      <Content selectedMenu={selectedMenu} />
    </div>
  );
};

const styles = {
  pageContainer: {
    display: 'flex',
    height: '100vh',
    backgroundColor: '#f5f5f5',
  },
};

export default MyPage;
