import React, { useState, useEffect, useRef } from "react";
import Sidebar from "../Sidebar";
import { Line, Pie } from "react-chartjs-2";
import {
  Typography, MenuItem, Select, FormControl, InputLabel, Box, Grid,
} from "@mui/material";
import "./FundingStatistics.css";
import { apiFundingList } from "../../apis/statistics/apiFundingList";
import { apiFundingStatistics } from "../../apis/statistics/apiFundingStatistics";
import TokenManager from "../../utils/TokenManager";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  ChartDataLabels
);

const FundingStatistics = () => {
  const [selectedMenu, setSelectedMenu] = useState("통계");
  const [selectedFunding, setSelectedFunding] = useState(null);
  const [fundingList, setFundingList] = useState([]);
  const [fundingStatistics, setFundingStatistics] = useState(null);
  const [loading, setLoading] = useState(false);

  const chartContainerRef = useRef(null);
  const [chartHeight, setChartHeight] = useState(0);

  useEffect(() => {
    const fetchFundingList = async () => {
      try {
        const accessToken = TokenManager.getAccessToken();
        const response = await apiFundingList(accessToken);
        setFundingList(response);
      } catch (error) {
        console.error("펀딩 목록을 가져오는 데 실패했습니다:", error);
      }
    };

    fetchFundingList();
  }, []);

  useEffect(() => {
    const fetchFundingStatistics = async () => {
      if (selectedFunding) {
        setLoading(true);
        try {
          const statistics = await apiFundingStatistics(selectedFunding);
          setFundingStatistics(statistics);
        } catch (error) {
          console.error("펀딩 통계를 가져오는 데 실패했습니다:", error);
        } finally {
          setLoading(false);
        }
      }
    };

    fetchFundingStatistics();
  }, [selectedFunding]);

  useEffect(() => {
    if (chartContainerRef.current) {
      setChartHeight(chartContainerRef.current.offsetHeight);
    }
  }, [fundingStatistics]);

  const handleFundingChange = (event) => {
    setSelectedFunding(event.target.value);
    setFundingStatistics(null);
  };

  return (
    <div className="funding-statistics-container">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />

      <div className="funding-statistics-content">
        <Typography variant="h4" className="funding-statistics-title">
          {selectedFunding
            ? `${fundingList.find(f => f.fundingIdx === selectedFunding)?.fundingTitle} 통계`
            : "펀딩을 선택해 주세요."}
        </Typography>

        <div className="funding-statistics-buttons">
          <FormControl variant="outlined" className="dropdown">
            <InputLabel>펀딩 선택</InputLabel>
            <Select value={selectedFunding || ""} onChange={handleFundingChange} label="펀딩 선택">
              {fundingList.map((funding) => (
                <MenuItem key={funding.fundingIdx} value={funding.fundingIdx}>
                  {funding.fundingTitle}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>

        {loading ? (
          <Typography variant="h6" className="loading-message">로딩 중...</Typography>
        ) : fundingStatistics ? (
          <Grid container spacing={4} className="funding-statistics-main">
            <Grid item xs={12} md={8}>
              <Box className="chart-container" ref={chartContainerRef}>
                <Typography variant="h6">기간 통계</Typography>
                <Line
  data={{
    labels: fundingStatistics.fundingDayStatistic?.map((_, index) => `Day ${index + 1}`) || [],
    datasets: [{
      label: "일별 모금액",
      data: fundingStatistics.fundingDayStatistic || [],
      fill: true,
      backgroundColor: "rgba(102, 178, 255, 0.2)",
      borderColor: "rgba(102, 178, 255, 1)",
    }],
  }}
  options={{
    scales: {
      y: {
        beginAtZero: true,
      },
    },
    plugins: {
      tooltip: {
        enabled: true,  // 툴팁 활성화 (hover 시 가격 표시)
        callbacks: {
          label: function(tooltipItem) {
            return `${tooltipItem.raw.toLocaleString()}원`;  // 가격에 세 자리마다 쉼표 추가
          }
        }
      },
      datalabels: {
        display: false,  // 데이터 라벨은 표시하지 않음
      },
    },
  }}
/>


              </Box>
            </Grid>

            <Grid item xs={12} md={4}>
              <Box className="user-statistics" style={{ height: chartHeight }}>
                <Typography variant="h6">유저 개인 통계</Typography>
                <div className="user-list">
                  <ul>
                    {fundingStatistics.fundingParticipants?.map((participant, index) => (
                      <li key={index}>
                        <span>{participant.name}</span> <span>{participant.price.toLocaleString()}원</span>
                      </li>
                    )) || <Typography>참여자가 없습니다.</Typography>}
                  </ul>
                </div>
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box className="gender-statistics">
                <Typography variant="h6">남성 연령별 통계</Typography>
                <Pie
                  data={{
                    labels: Object.keys(fundingStatistics.fundingStatsByAgeAndGender.maleData),
                    datasets: [{
                      data: Object.values(fundingStatistics.fundingStatsByAgeAndGender.maleData),
                      backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF"],
                    }],
                  }}
                  options={{
                    plugins: {
                      datalabels: {
                        formatter: (value, context) => {
                          if (value === 0) return null;
                          const total = context.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
                          const percentage = ((value / total) * 100).toFixed(2) + "%";
                          return percentage;
                        },
                        color: '#fff',
                        font: { weight: 'bold' },
                      },
                    },
                  }}
                />
              </Box>
            </Grid>

            <Grid item xs={12} md={6}>
              <Box className="gender-statistics">
                <Typography variant="h6">여성 연령별 통계</Typography>
                <Pie
                  data={{
                    labels: Object.keys(fundingStatistics.fundingStatsByAgeAndGender.femaleData),
                    datasets: [{
                      data: Object.values(fundingStatistics.fundingStatsByAgeAndGender.femaleData),
                      backgroundColor: ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF"],
                    }],
                  }}
                  options={{
                    plugins: {
                      datalabels: {
                        formatter: (value, context) => {
                          if (value === 0) return null;
                          const total = context.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
                          const percentage = ((value / total) * 100).toFixed(2) + "%";
                          return percentage;
                        },
                        color: '#fff',
                        font: { weight: 'bold' },
                      },
                    },
                  }}
                />
              </Box>
            </Grid>
          </Grid>
        ) : (
          <Typography variant="h6" className="no-funding-message">
            펀딩을 선택해 주세요.
          </Typography>
        )}
      </div>
    </div>
  );
};

export default FundingStatistics;
