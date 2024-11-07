import axios from "axios";

const apiUrl = "https://j11e202.p.ssafy.io/api";

// 시도 데이터를 가져오는 함수
export const getSido = async () => {
  try {
    const response = await axios.get(`${apiUrl}/regions/sido`);
    return response.data;
  } catch (error) {
    console.error("Failed to fetch 시도 data:", error);
    throw error;
  }
};

// 시군구 데이터를 가져오는 함수
export const getSigungu = async (sido) => {
  try {
    const response = await axios.get(`${apiUrl}/regions/sigungu`, {
      params: {
        sido,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Failed to fetch 시군구 data:", error);
    throw error;
  }
};

// 회원가입 요청을 보내는 함수
export const signUp = async (formData, profileImage) => {
  // 전달된 formData 확인 로그 추가
  console.log("signUp 함수에서 받은 formData:", formData); 
  console.log("signUp 함수에서 받은 profileImage:", profileImage); // profileImage 값이 없을 때 명확히 표시

  const signUpDto = {
    name: formData.name,
    email: formData.email,
    password: formData.password,
    mobilePhone: formData.mobilePhone,
    landlinePhone: formData.landlinePhone,
    sido: formData.sido,
    sigungu: formData.sigungu,
    roles: "ROLE_CORPORATIONYET",
    isSocial: false,
    socialType: null,
    socialSerialNum: null,
  };

  console.log("생성된 signUpDto:", signUpDto); // signUpDto 로그

  // FormData 객체 생성
  const formDataObj = new FormData();

  // JSON 데이터를 Blob으로 변환하여 추가
  formDataObj.append("signUpDto", new Blob([JSON.stringify(signUpDto)], { type: "application/json" }));

  // 프로필 이미지를 profileImage 키로 추가 (없을 경우 빈 문자열로 대체)
  formDataObj.append("profileImage", profileImage || "");

  console.log("FormData에 추가된 signUpDto 및 profileImage:", formDataObj); // FormData 확인

  try {
    const response = await axios.post(`${apiUrl}/users`, formDataObj, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    console.log("회원가입 응답 데이터:", response.data);
    return response.data;
  } catch (error) {
    console.error("회원가입 실패:", error.response ? error.response.data : error);
    throw error;
  }
};

export default { getSido, getSigungu, signUp };
