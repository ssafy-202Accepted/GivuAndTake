import React, { useState, useEffect, useRef } from "react";
import Sidebar from "../Sidebar";
import { Line, Pie, Bar } from "react-chartjs-2";
import {
  Typography, MenuItem, Select, FormControl, InputLabel, Box, Grid,
  Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow
} from "@mui/material";
import "./DonationsStatistics.css";
import { apiMyDonations } from "../../apis/donations/apiMyDonations";
import { getUserInfo } from "../../apis/auth/apiUserInfo";
import { apiDonationsStatistics } from "../../apis/statistics/apiDonationsStatistics";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js';
import ChartDataLabels from 'chartjs-plugin-datalabels';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  ChartDataLabels
);

const DonationsStatistics = () => {
  const [selectedMenu, setSelectedMenu] = useState("통계");
  const [selectedDonation, setSelectedDonation] = useState(null);
  const [donations, setDonations] = useState([]);
  const [donationStatistics, setDonationStatistics] = useState(null);
  const [loading, setLoading] = useState(false);

  const chartContainerRef = useRef(null);
  const [chartHeight, setChartHeight] = useState(0);

  useEffect(() => {
    const fetchDonations = async () => {
      try {
        const userInfo = await getUserInfo();
        const email = userInfo.email;
        const donationData = await apiMyDonations(email);
        setDonations(donationData);
      } catch (error) {
        console.error("기부 목록을 가져오는 데 실패했습니다:", error);
      }
    };

    fetchDonations();
  }, []);

  useEffect(() => {
    const fetchDonationStatistics = async () => {
      if (selectedDonation) {
        setLoading(true);
        try {
          const statistics = await apiDonationsStatistics(selectedDonation);
          setDonationStatistics(statistics);
        } catch (error) {
          console.error("기부 통계를 가져오는 데 실패했습니다:", error);
        } finally {
          setLoading(false);
        }
      }
    };

    fetchDonationStatistics();
  }, [selectedDonation]);

  const handleDonationChange = (event) => {
    setSelectedDonation(event.target.value);
    setDonationStatistics(null);
  };

  return (
    <div className="donations-statistics-container">
      <Sidebar selectedMenu={selectedMenu} setSelectedMenu={setSelectedMenu} />

      <div className="donations-statistics-content">
        <Typography variant="h4" className="donations-statistics-title">
          {selectedDonation
            ? `${donations.find(d => d.giftIdx === selectedDonation)?.giftName} 통계`
            : "기부품을 선택해 주세요."}
        </Typography>

        <div className="donations-statistics-buttons">
          <FormControl variant="outlined" className="donations-dropdown">
            <InputLabel>기부품 선택</InputLabel>
            <Select value={selectedDonation || ""} onChange={handleDonationChange} label="기부품 선택">
              {donations.map((donation) => (
                <MenuItem key={donation.giftIdx} value={donation.giftIdx}>
                  {donation.giftName}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>

        {loading ? (
          <Typography variant="h6" className="loading-message">로딩 중...</Typography>
        ) : donationStatistics ? (
          <>
            <Grid container spacing={4} className="donations-statistics-main">
              {/* 기간 통계 차트 */}
              <Grid item xs={12} md={8}>
                <Box className="chart-container" ref={chartContainerRef}>
                  <Typography variant="h6">기간 통계</Typography>
                  <Line
                    data={{
                      labels: [...Array(12).keys()].map(i => i + 1), // 1~12월
                      datasets: [{
                        label: "월별 구매량",
                        data: donationStatistics.giftYearStatistics.slice(1), // 첫번째 값 무시
                        fill: true,
                        backgroundColor: "rgba(102, 178, 255, 0.2)",
                        borderColor: "rgba(102, 178, 255, 1)",
                      }],
                    }}
                    options={{
                      scales: {
                        y: {
                          beginAtZero: true,
                          ticks: {
                            stepSize: 5, // y축을 5단위로 설정
                          },
                        },
                      },
                    }}
                  />
                </Box>
              </Grid>

              {/* 구매자 통계 박스 */}
              <Grid item xs={12} md={4}>
                <TableContainer component={Paper} style={{ maxHeight: '415px', overflowY: 'auto' }}>
                <Typography variant="h6" className="purchase-statistics-title">구매자 통계</Typography>
                  <Table aria-label="purchaser table">
                    <TableHead>
                      <TableRow>
                        <TableCell>이름</TableCell>
                        <TableCell align="right">금액</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {donationStatistics?.giftPurchasers.map((purchaser, index) => (
                        <TableRow key={index}>
                          <TableCell component="th" scope="row">
                            {purchaser.name}
                          </TableCell>
                          <TableCell align="right">
                          {(Math.round(purchaser.price / 1000) * 1000).toLocaleString()}원
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </TableContainer>
              </Grid>
            </Grid>

            <Grid container spacing={4} className="donations-statistics-second-row">
  {/* 카테고리 통계 (0.5 비율) */}
  <Grid item xs={6} md={3}>
    <Box className="donation-category-statistics">
      <Typography variant="h6" className="pie-chart-title">동일 카테고리</Typography>
      <Pie
        data={{
          labels: donationStatistics?.giftPercentageByCategory
          ? Object.keys(donationStatistics.giftPercentageByCategory).map(label =>
            label.length > 10 ? `${label.substring(0, 10)}...` : label
          )
        : [],
          datasets: [{
            data: donationStatistics?.giftPercentageByCategory
              ? Object.values(donationStatistics.giftPercentageByCategory).map(item => item.count)
              : [],
            backgroundColor: ["#FFCE56", "#4BC0C0", "#9966FF"],
          }],
        }}
        options={{
          plugins: {
            tooltip: {
              callbacks: {
                label: function(tooltipItem) {
                  // 해당 레이블의 전체 텍스트를 반환하여 hover 시 표시
                  const fullLabel = Object.keys(donationStatistics.giftPercentageByCategory)[tooltipItem.dataIndex];
                  const value = tooltipItem.raw;
                  return `${fullLabel}: ${value}`;
                }
              }
            },
            datalabels: {
              formatter: (value) => `${value}`,
              color: '#fff',
              font: {
                weight: 'bold',
              }
              }
          }
        }}
      />
    </Box>
  </Grid>

  {/* 성별 통계 (0.5 비율) */}
  <Grid item xs={6} md={3}>
    <Box className="donation-gender-statistics">
      <Typography variant="h6" className="pie-chart-title">구매자 비율</Typography>
      <div className="pie-chart-container">
        <div className="chart">
          <Pie
            data={{
              labels: ["남성", "여성"],
              datasets: [{
                data: [
                  donationStatistics.giftPercentageByGender.male?.count || 0,
                  donationStatistics.giftPercentageByGender.female?.count || 0
                ],
                backgroundColor: ["#36A2EB", "#FF6384"],
              }],
            }}
          />
        </div>
      </div>
    </Box>
  </Grid>

  {/* 연령대 통계 (1 비율) */}
  <Grid item xs={12} md={6}>
    <Box className="donation-age-statistics">
      <Typography variant="h6">연령대 통계</Typography>
      <Bar
        data={{
          labels: ["20s", "30s", "40s", "50s", "60+"],
          datasets: [{
            label: "연령대별 기부자 수",
            data: [
              donationStatistics.giftPercentageByAge["20s"]?.count || 0,
              donationStatistics.giftPercentageByAge["30s"]?.count || 0,
              donationStatistics.giftPercentageByAge["40s"]?.count || 0,
              donationStatistics.giftPercentageByAge["50s"]?.count || 0,
              donationStatistics.giftPercentageByAge["60+"]?.count || 0
            ],
            backgroundColor: "rgba(75, 192, 192, 0.2)",
            borderColor: "rgba(75, 192, 192, 1)",
            borderWidth: 1,
          }]
        }}
        options={{
          scales: {
            y: {
              beginAtZero: true,
              ticks: {
                stepSize: 1,
              }
            }
          }
        }}
      />
    </Box>
  </Grid>
</Grid>
          </>
        ) : (
          <Typography variant="h6" className="no-donation-message">
            기부품을 선택해 주세요.
          </Typography>
        )}
      </div>
    </div>
  );
};

export default DonationsStatistics;
