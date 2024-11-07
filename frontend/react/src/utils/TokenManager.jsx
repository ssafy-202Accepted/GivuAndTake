// TokenManager.jsx

export const TokenManager = {
  // AccessToken 저장
  setAccessToken(token) {
    localStorage.setItem("accessToken", token);
  },

  // RefreshToken 저장
  setRefreshToken(token) {
    localStorage.setItem("refreshToken", token);
  },

  // AccessToken 가져오기
  getAccessToken() {
    return localStorage.getItem("accessToken");
  },

  // RefreshToken 가져오기
  getRefreshToken() {
    return localStorage.getItem("refreshToken");
  },

  // AccessToken 삭제
  removeAccessToken() {
    localStorage.removeItem("accessToken");
  },

  // RefreshToken 삭제
  removeRefreshToken() {
    localStorage.removeItem("refreshToken");
  },

  // 모든 토큰 삭제
  clearTokens() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
  },
};

export default TokenManager;
