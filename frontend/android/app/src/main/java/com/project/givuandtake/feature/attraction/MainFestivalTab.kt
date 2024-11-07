package com.project.givuandtake.feature.attraction

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.project.givuandtake.core.data.FestivalMainData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

data class FestivalItemData(
    val fstvlNm: String,
    val rdnmadr: String,
    val fstvlStartDate: String,
    val fstvlEndDate: String,
    val fstvlCo: String,
)

fun fetchFestivalDataWithOkHttp(displayedCity:String, onDataFetched: (List<FestivalItemData>) -> Unit) {
    val client = OkHttpClient()
    val insttCode = when(displayedCity) {
        "영도" -> 3280000
        "군위" -> 5141000
        "남원" -> 4701000
        "무주" -> 4741000
        "순창" -> "B551011"
        "임실" -> 4761000
        "고흥" -> 4880000
        "보성" -> 4890000
        "신안" -> 5010000
        "함평" -> "B551011"
        "고성" -> 4341000
        "남해" -> 5430000
        "하동" -> 5440000
        "합천" -> 5480000
        "문경" -> 5120000
        "상주" -> 5110000
        "안동" -> "B551011"
        "영천" -> 5100000
        "평창" -> 4281000
        "횡성" -> 4261000
        "태백" -> 4221000
        "정선" -> 4291000
        "괴산" -> "B551011"
        "보은" -> 6430000
        "영동" -> 6430000
        "제천" -> 4400000
        "보령" -> 4510000
        "부여" -> 4570000
        "공주" -> 4500000
        "태안" -> 4620000
        else -> 4181000  // 기본값
    }


    // API URL
    val url = "http://api.data.go.kr/openapi/tn_pubr_public_cltur_fstvl_api?serviceKey=ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D&pageNo=1&numOfRows=150&type=json&insttCode=$insttCode"

    // API 요청 생성
    val request = Request.Builder()
        .url(url)
        .build()

    // 비동기 요청 처리
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("OkHttp", "Failed to fetch data", e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    Log.e("OkHttp", "Unexpected code $response")
                    return
                }

                // JSON 파싱
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        // GSON을 사용해서 JSON 데이터를 FestivalMainData로 변환
                        val gson = Gson()
                        val festivalData = gson.fromJson(responseBody, FestivalMainData::class.java)

                        // 축제 목록 전달 (필터링 없이 전체 데이터)
                        val festivalList = festivalData.response.body.items
                        onDataFetched(festivalList)
                    } catch (e: Exception) {
                        Log.e("OkHttp", "Error parsing JSON", e)
                    }
                }
            }
        }
    })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FestivalItem(
    location: String,
    description: String,
    title: String,
    dateRange: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()

            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = location,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateRange,
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                val periods = description.split("+")
                periods.forEach { period ->
                    val displayText = period
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp, bottom = 4.dp)  // 항목 간 간격 설정
                            .background(Color(0xFFA093DE), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = displayText,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun formatDateWithoutYear(startDate: String, endDate: String): String {
    // 날짜를 '-'로 split하고 월-일만 남긴다
    val startMonthDay = startDate.split("-").slice(1..2).joinToString("-")
    val endMonthDay = endDate.split("-").slice(1..2).joinToString("-")

    return "$startMonthDay ~ $endMonthDay"
}

@Composable
fun MainFestivalTab(displayedCity:String, navController: NavController) {
    // 상태 변수로 축제 데이터를 관리
    var festivalData by remember { mutableStateOf<List<FestivalItemData>>(emptyList()) }

    // API 호출
    LaunchedEffect(displayedCity) {
        fetchFestivalDataWithOkHttp(displayedCity) { data ->
            festivalData = data
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(0.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "우리 고향 축제",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 15.dp)
            )
            Text(
                text = "전체보기",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .clickable {
                        val city = displayedCity
                        navController.navigate("festivalpage?city=$city")
                    }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 축제 데이터를 표시하는 Column
        if (festivalData.isEmpty()) {
            Text(text = "데이터를 불러오는 중입니다...", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Column {
                // 상위 3개 데이터만 출력
                festivalData.take(3).forEach { festival ->
                    FestivalItem(
                        location = festival.rdnmadr ?: "주소 없음",
                        description = festival.fstvlCo ?: "설명 없음",
                        title = festival.fstvlNm,
                        dateRange = formatDateWithoutYear(festival.fstvlStartDate, festival.fstvlEndDate)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
