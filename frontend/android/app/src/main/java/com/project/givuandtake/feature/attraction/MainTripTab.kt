
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// 관광지 데이터를 담을 데이터 클래스 정의
data class TourismItem(
    val trrsrtNm: String,
    val cnvnncFclty: String,
    val rdnmadr: String,
    val trrsrtIntrcn: String,
    val prkplceCo: String
)

// API 응답 데이터의 형식에 맞는 래퍼 클래스 (items가 리스트인 경우)
data class ApiResponse(
    val response: ResponseData
)

data class ResponseData(
    val body: BodyData
)

data class BodyData(
    val items: List<TourismItem>
)

// 관광지 데이터를 가져오는 함수
fun fetchTourismDataWithOkHttp(displayedCity:String, onDataFetched: (List<TourismItem>) -> Unit) {
    val client = OkHttpClient()
    val instt_code = when(displayedCity) {
        "영도" -> 3280000
        "군위" -> 5141000
        "남원" -> 4701000
        "무주" -> 4741000
        "순창" -> "없음"
        "임실" -> 4761000
        "고흥" -> "없음"
        "보성" -> 4890000
        "신안" -> 5010000
        "함평" -> 4960000
        "고성" -> 5420000
        "남해" -> 5430000
        "하동" -> 5440000
        "합천" -> 5480000
        "문경" -> 5120000
        "상주" -> 5110000
        "안동" -> 5070000
        "영천" -> 5100000
        "평창" -> 4281000
        "횡성" -> 4261000
        "태백" -> 4221000
        "정선" -> 4291000
        "괴산" -> 4460000
        "보은" -> 4420000
        "영동" -> 4440000
        "제천" -> 4400000
        "보령" -> 4510000
        "부여" -> 4570000
        "공주" -> 4500000
        "태안" -> 4620000
        else -> 4181000  // 기본값
    }

    val url = "http://api.data.go.kr/openapi/tn_pubr_public_trrsrt_api?serviceKey=ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D&pageNo=1&numOfRows=999&type=json&instt_code=$instt_code"

    val request = Request.Builder()
        .url(url)
        .build()

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

                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val gson = Gson()
                        // TypeToken을 명시적으로 사용하여 items가 배열로 되어 있음을 정의
                        val apiResponseType = object : TypeToken<ApiResponse>() {}.type
                        val apiResponse: ApiResponse = gson.fromJson(responseBody, apiResponseType)
                        val tourismItems = apiResponse.response.body.items
                        onDataFetched(tourismItems)
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
fun TripItem(
    location: String,
    description: String,
    title: String,
    facilities: String,
    parking: String
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded }
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

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Text(
                        text = description,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            FlowRow(
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                val periods = facilities.split("+")
                periods.forEach { period ->
                    val displayText = if (period == "주차장") "$period ($parking)" else period
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp, bottom = 8.dp)  // 항목 간 간격 설정
                            .background(Color(0xFFA093DE), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
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

@Composable
fun MainTripTab(displayedCity: String = "영도", navController: NavController) {
    var tourismData by remember { mutableStateOf<List<TourismItem>>(emptyList()) }

    // API 호출
    LaunchedEffect(displayedCity) {
        fetchTourismDataWithOkHttp(displayedCity) { data ->
            tourismData = data
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "우리 고향 관광지",
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 15.dp)
            )
            Text(
                text = "지도로 더 보기",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .clickable {
                        val city = displayedCity
                        navController.navigate("trippage?city=$city")
                    }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (tourismData.isEmpty()) {
            Text(text = "데이터를 불러오는 중입니다...", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Column {
                // 상위 3개 데이터만 출력
                tourismData.take(3).forEach { trip ->
                    TripItem(
                        location = trip.rdnmadr ?: "주소 없음",
                        description = trip.trrsrtIntrcn ?: "설명 없음",
                        title = trip.trrsrtNm,
                        facilities = trip.cnvnncFclty,
                        parking = trip.prkplceCo
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
