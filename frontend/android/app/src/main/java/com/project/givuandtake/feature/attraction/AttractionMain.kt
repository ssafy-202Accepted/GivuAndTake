
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.RetrofitInstance
import com.project.givuandtake.core.data.WeatherData
import com.project.givuandtake.feature.attraction.MainFestivalTab
import com.project.givuandtake.feature.attraction.MainMarketTab
import com.project.givuandtake.feature.attraction.MainVillageTab
import com.skydoves.landscapist.glide.GlideImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getWeatherData(lat: Double, lon: Double, onResult: (String, String, String) -> Unit) {
    val apiKey = "fe4c6b378cbe4af2538f2d255f5bdcea" // API 키를 여기에 입력
    val lang = "kr"

    RetrofitInstance.api.getWeather(lat, lon, apiKey, lang).enqueue(object : Callback<WeatherData> {
        override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
            if (response.isSuccessful) {
                val weatherData = response.body()
                val temperature = weatherData?.main?.temp?.minus(273.15) // 켈빈에서 섭씨로 변환
                val weatherDes = weatherData?.weather?.get(0)?.main
                val weatherMoreDes = weatherData?.weather?.get(0)?.description
                onResult(temperature?.toInt().toString(), weatherDes ?: "", weatherMoreDes ?: "")
            } else {
                Log.e("Weather", "Error: ${response.code()}")
                onResult("", "", "")
            }
        }

        override fun onFailure(call: Call<WeatherData>, t: Throwable) {
            Log.e("Weather", "Failed to get weather data", t)
            onResult("", "", "")
        }
    })
}

@Composable
fun GifImage(weatherDes: String) {
    // weatherMain 값을 기반으로 이미지 파일 경로 설정
    val assetPath = when (weatherDes) {
        "Clear" -> R.drawable.clear
        "Clouds" -> R.drawable.clouds
        "Atmosphere" -> R.drawable.atmosphere
        "Snow" -> R.drawable.snow
        "Rain" -> R.drawable.rain
        "Drizzle" -> R.drawable.drizzle
        "Thunderstorm" -> R.drawable.thunderstrom
        else -> R.drawable.clear // 기본값
    }

    // 이미지 로딩
    GlideImage(
        imageModel = assetPath,
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(30.dp))
    )
}


class AttractionViewModel : ViewModel() {
    // 탭 상태 관리
    private val _selectedTabIndex = mutableStateOf(0)
    val selectedTabIndex: State<Int> = _selectedTabIndex

    // 탭 변경 함수
    fun updateTabIndex(index: Int) {
        _selectedTabIndex.value = index
    }
}

@Composable
fun AttractionMain(navController: NavController, city: String?) {
    val scrollState = rememberScrollState()
    val viewModel: AttractionViewModel = viewModel()
    val selectedTabIndex by viewModel.selectedTabIndex

    val displayedCity = city ?: "도 선택"
    Log.d("CitySelection", displayedCity)
    val displayedText = when (displayedCity) {
        "영도" -> "부산광역시 영도구"
        "군위" -> "대구광역시 군위군"

        "남원" -> "전북특별자치도 남원시"
        "무주" -> "전북특별자치도 무주군"
        "순창" -> "전북특별자치도 순창군"
        "임실" -> "전북특별자치도 임실군"

        "고흥" -> "전라남도 고흥군"
        "보성" -> "전라남도 보성군"
        "신안" -> "전라남도 신안군"
        "함평" -> "전라남도 함평군"

        "고성" -> "경상남도 고성군"
        "남해" -> "경상남도 남해군"
        "하동" -> "경상남도 하동군"
        "합천" -> "경상남도 합천군"

        "문경" -> "경상북도 문경시"
        "상주" -> "경상북도 상주시"
        "안동" -> "경상북도 안동시"
        "영천" -> "경상북도 영천시"

        "평창" -> "강원특별자치도 평창군"
        "횡성" -> "강원특별자치도 횡성군"
        "태백" -> "강원특별자치도 태백시"
        "정선" -> "강원특별자치도 정선군"

        "괴산" -> "충청북도 괴산군"
        "보은" -> "충청북도 보은군"
        "영동" -> "충청북도 영동군"
        "제천" -> "충청북도 제천시"

        "보령" -> "충청남도 보령시"
        "부여" -> "충청남도 부여군"
        "공주" -> "충청남도 공주시"
        "태안" -> "충청남도 태안군"
        else -> displayedCity // Default case for cities not specified
    }

    var temperature by remember { mutableStateOf("") }
    var weatherDes by remember { mutableStateOf("") }
    var weatherMoreDes by remember { mutableStateOf("") }

    // 탭 상태 관리
    val tabs = listOf("전통시장", "축제", "관광지", "체험마을")

    LaunchedEffect(Unit) {
        val (lat, lon) = when (displayedCity) {
            "영도" -> Pair(35.0911, 129.0689) // 부산 영도구
            "군위" -> Pair(36.2395, 128.5727) // 대구 군위군

            "남원" -> Pair(35.4164, 127.3900) // 전북 남원시
            "무주" -> Pair(35.9078, 127.6606) // 전북 무주군
            "순창" -> Pair(35.3741, 127.1387) // 전북 순창군
            "임실" -> Pair(35.6175, 127.2886) // 전북 임실군

            "고흥" -> Pair(34.6050, 127.2827) // 전남 고흥군
            "보성" -> Pair(34.7717, 127.0802) // 전남 보성군
            "신안" -> Pair(34.8277, 126.1072) // 전남 신안군
            "함평" -> Pair(35.0650, 126.5169) // 전남 함평군

            "고성" -> Pair(34.9733, 128.3236) // 경남 고성군
            "남해" -> Pair(34.8371, 127.8925) // 경남 남해군
            "하동" -> Pair(35.0666, 127.7514) // 경남 하동군
            "합천" -> Pair(35.5661, 128.1654) // 경남 합천군

            "문경" -> Pair(36.5866, 128.1996) // 경북 문경시
            "상주" -> Pair(36.4106, 128.1593) // 경북 상주시
            "안동" -> Pair(36.5684, 128.7227) // 경북 안동시
            "영천" -> Pair(35.9733, 128.9389) // 경북 영천시

            "평창" -> Pair(37.3704, 128.3906) // 강원 평창군
            "횡성" -> Pair(37.4912, 127.9846) // 강원 횡성군
            "태백" -> Pair(37.1640, 128.9859) // 강원 태백시
            "정선" -> Pair(37.3800, 128.6608) // 강원 정선군

            "괴산" -> Pair(36.8152, 127.7902) // 충북 괴산군
            "보은" -> Pair(36.4897, 127.7297) // 충북 보은군
            "영동" -> Pair(36.1750, 127.7766) // 충북 영동군
            "제천" -> Pair(37.1325, 128.1900) // 충북 제천시

            "보령" -> Pair(36.3335, 126.6129) // 충남 보령시
            "부여" -> Pair(36.2744, 126.9094) // 충남 부여군
            "공주" -> Pair(36.4467, 127.1192) // 충남 공주시
            "태안" -> Pair(36.7456, 126.2970) // 충남 태안군

            else -> Pair(37.5665, 126.9780) // Default to Seoul if no match
        }

        // 비동기 API 호출로 날씨 데이터를 가져옴
        getWeatherData(lat, lon) { temp, weather, des ->
            temperature = temp // 상태 업데이트
            weatherDes = weather
            weatherMoreDes = des
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(Color(0xFFB3C3F4)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start, // Align items to the left
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 16.dp)
        ) {

            Text(
                text = displayedText,
                fontSize = 25.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                contentDescription = "locationselect",
                modifier = Modifier
                    .size(50.dp)
                    .clickable { navController.navigate("locationSelection") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 날씨 정보와 탭 UI 추가
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(110.dp)
                .background(Color(0xFF4099E9), shape = RoundedCornerShape(30.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp),
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = "Location Icon",
                            modifier = Modifier.size(24.dp) // 이미지 크기 설정
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = displayedText,
                            color = Color(0xFFFFFFFF)
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            append(temperature)
                            withStyle(style = SpanStyle(fontSize = 20.sp)) {
                                append("°C   ")
                            }
                            val displayWeatherDescription = when (weatherMoreDes) {
                                "튼구름" -> "구름 조금"
                                "구름조금" -> "구름 조금"
                                "실 비" -> "비"
                                "맑음" -> "맑음"
                                "약간의 구름이 낀 하늘" -> "구름 약간"
                                "온흐림" -> "구름 많음"
                                else -> weatherMoreDes // Default case, keeps the original text if no match
                            }
                            withStyle(style = SpanStyle(fontSize = 25.sp)) {
                                append("$displayWeatherDescription")
                            }
                        },
                        fontSize = 30.sp,
                        color = Color(0xFFFFFFFF),
                    )
                }
                GifImage(weatherDes)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(0.9f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically

        ) {
            items(tabs.size) { index ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { viewModel.updateTabIndex(index) },
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(horizontal = 12.dp) // 탭 너비를 텍스트에 맞춤
                ) {
                    Text(
                        text = tabs[index],
                        color = if (selectedTabIndex == index) Color.Black else Color.White, // 선택 여부에 따라 글씨 색상 변경
                        fontSize = 20.sp,
                        style = TextStyle(
                            textDecoration = if (selectedTabIndex == index) {
                                TextDecoration.Underline // 선택된 탭에 밑줄 적용
                            } else {
                                TextDecoration.None // 선택되지 않은 탭은 밑줄 없음
                            }
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .height(600.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                )
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight()  // 높이를 모두 채우도록 설정
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        MainMarketTab(displayedCity)
                    }

                    1 -> {
                        MainFestivalTab(navController = navController, displayedCity = displayedCity)
                    }

                    2 -> {
                        MainTripTab(navController = navController, displayedCity = displayedCity)
                    }

                    3 -> {
                        MainVillageTab(navController = navController, displayedCity = displayedCity)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))  // 남는 공간을 모두 차지하도록 설정
            }
        }
    }
}
