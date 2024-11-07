// import React, { useEffect, useState } from "react";
// import { useParams, useNavigate } from "react-router-dom";
// import Sidebar from "../Sidebar";
// import { apiFundingDetail } from "../../apis/funding/apiFundingDetail"; // 기존 펀딩 상세 정보 가져오는 API
// import { apiUpdateFunding } from "../../apis/funding/apiUpdateFunding"; // 펀딩 수정 API
// import DatePicker from "react-datepicker";
// import "react-datepicker/dist/react-datepicker.css";
// import Swal from "sweetalert2";
// import "./UpdateFunding.css";

// const UpdateFunding = () => {
//   const { fundingIdx } = useParams();
//   const [funding, setFunding] = useState(null);
//   const [loading, setLoading] = useState(true);
//   const [selectedMenu, setSelectedMenu] = useState("펀딩");

//   // 수정할 필드들
//   const [fundingTitle, setFundingTitle] = useState("");
//   const [fundingType, setFundingType] = useState("D");
//   const [goalMoney, setGoalMoney] = useState("");
//   const [startDate, setStartDate] = useState(null);
//   const [endDate, setEndDate] = useState(null);
//   const [thumbnail, setThumbnail] = useState(null);

//   const navigate = useNavigate();

//   useEffect(() => {
//     const fetchFundingDetail = async () => {
//       try {
//         const data = await apiFundingDetail(fundingIdx);
//         setFunding(data);
//         setFundingTitle(data.fundingTitle);
//         setFundingType(data.fundingType);
//         setGoalMoney(data.goalMoney.toLocaleString());
//         setStartDate(new Date(data.startDate));
//         setEndDate(new Date(data.endDate));
//         setThumbnail(data.thumbnail);
//       } catch (error) {
//         console.error("펀딩 상세 정보를 가져오는 데 실패했습니다:", error);
//       } finally {
//         setLoading(false);
//       }
//     };

//     fetchFundingDetail();
//   }, [fundingIdx]);

//   // 금액 입력 형식화 함수 (천 단위로 쉼표 추가)
//   const formatNumber = (value) => {
//     const number = value.replace(/[^0-9]/g, "");
//     return new Intl.NumberFormat().format(number);
//   };

//   const handleGoalMoneyChange = (e) => {
//     const formattedValue = formatNumber(e.target.value);
//     setGoalMoney(formattedValue);
//   };

//   const handleImageChange = (e) => {
//     const file = e.target.files[0];
//     if (file) {
//       const reader = new FileReader();
//       reader.onloadend = () => {
//         setThumbnail(reader.result);
//       };
//       reader.readAsDataURL(file);
//     }
//   };

//   const handleSave = async () => {
//     const updateData = {
//       fundingTitle,
//       fundingType,
//       goalMoney: parseInt(goalMoney.replace(/,/g, "")),
//       startDate: startDate.toISOString().split("T")[0],
//       endDate: endDate.toISOString().split("T")[0],
//       thumbnail,
//     };

//     try {
//       await apiUpdateFunding(fundingIdx, updateData);
//       Swal.fire({
//         title: "수정 성공",
//         text: "펀딩이 성공적으로 수정되었습니다.",
//         icon: "success",
//         confirmButtonText: "확인",
//       }).then(() => {
//         navigate(`/funding/${fundingIdx}`); // 수정 후 펀딩 상세 페이지로 이동
//       });
//     } catch (error) {
//       console.error("펀딩 수정 중 오류 발생:", error);
//       Swal.fire({
//         title: "수정 실패",
//         text: "펀딩 수정에 실패했습니다.",
//         icon: "error",
//         confirmButtonText: "확인",
//       });
//     }
//   };

//   if (loading) {
//     return <p>로딩 중...</p>;
//   }

//   return (
//     <div className="update-funding-layout">
//       <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />

//       <div className="update-funding-content">
//         <div className="header">
//           <h1>펀딩 수정</h1>
//           <button className="save-button" onClick={handleSave}>
//             수정 완료
//           </button>
//         </div>

//         <div className="funding-details-container">
//           <div className="thumbnail">
//             {thumbnail ? (
//               <img src={thumbnail} alt="썸네일 미리보기" className="thumbnail-preview" />
//             ) : (
//               <div className="thumbnail-placeholder">썸네일</div>
//             )}
//           </div>

//           <div className="details-section">
//             <input
//               type="text"
//               placeholder="펀딩명"
//               className="funding-name"
//               value={fundingTitle}
//               onChange={(e) => setFundingTitle(e.target.value)}
//             />
//             <div className="funding-type">
//               <label>
//                 <input
//                   type="radio"
//                   name="fundingType"
//                   value="D"
//                   checked={fundingType === "D"}
//                   onChange={(e) => setFundingType(e.target.value)}
//                 />{" "}
//                 재난재해
//               </label>
//               <label>
//                 <input
//                   type="radio"
//                   name="fundingType"
//                   value="R"
//                   checked={fundingType === "R"}
//                   onChange={(e) => setFundingType(e.target.value)}
//                 />{" "}
//                 지역기부
//               </label>
//             </div>

//             <div className="dates">
//               <div className="date-picker">
//                 <label>시작일:</label>
//                 <DatePicker
//                   selected={startDate}
//                   onChange={(date) => setStartDate(date)}
//                   dateFormat="yyyy-MM-dd"
//                 />
//               </div>

//               <div className="date-picker">
//                 <label>마감일:</label>
//                 <DatePicker
//                   selected={endDate}
//                   onChange={(date) => setEndDate(date)}
//                   dateFormat="yyyy-MM-dd"
//                 />
//               </div>
//             </div>

//             <div className="goal-amount">
//               <input
//                 type="text"
//                 placeholder="목표 금액"
//                 value={goalMoney}
//                 onChange={handleGoalMoneyChange}
//               />
//             </div>

//             <div className="image-upload">
//               <label htmlFor="thumbnail" className="custom-file-upload">
//                 이미지 선택
//               </label>
//               <input type="file" id="thumbnail" accept="image/*" onChange={handleImageChange} />
//             </div>
//           </div>
//         </div>

//         <div className="description-section">
//           <textarea
//             placeholder="설명을 입력해주세요"
//             value={funding.fundingContent}
//             onChange={(e) => setFunding({ ...funding, fundingContent: e.target.value })}
//           />
//         </div>
//       </div>
//     </div>
//   );
// };

// export default UpdateFunding;
