import React from 'react';

const Content = ({ selectedMenu }) => {
  return (
    <div style={styles.content}>
      <h1>{selectedMenu}</h1>
      <p>이곳은 {selectedMenu}에 대한 내용을 표시하는 곳입니다.</p>
    </div>
  );
};

const styles = {
  content: {
    flex: 1,
    padding: '40px',
    backgroundColor: '#fff',
  },
};

export default Content;
