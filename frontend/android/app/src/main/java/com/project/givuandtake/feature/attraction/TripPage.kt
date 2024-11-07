package com.project.givuandtake.feature.attraction

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.MarkerIcons
import com.project.givuandtake.R
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

data class TripIdData(
    val response: ResponseData
)

data class ResponseData(
    val body: BodyData
)

data class BodyData(
    val items: ItemsData
)

data class ItemsData(
    val item: List<TourismItem>
)

data class TourismItem(
    val contentid: String,
    val title: String
)

fun fetchTripIdDataWithOkHttp(areaCode: Int, sigunguCode: Int, onResult: (List<String>) -> Unit) {
    val client = OkHttpClient()
    val serviceKey =
        "ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D"

    // API URL
    val url =
        "https://apis.data.go.kr/B551011/KorService1/areaBasedList1?serviceKey=$serviceKey&numOfRows=15&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json&contentTypeId=12&areaCode=$areaCode&sigunguCode=$sigunguCode"

    // API 요청 생성
    val request = Request.Builder()
        .url(url)
        .build()

    // 비동기 요청 처리
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
//            Log.e("OkHttp", "Failed to fetch data", e)
            onResult(emptyList()) // 실패 시 빈 리스트 반환
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
//                    Log.e("OkHttp", "Unexpected code $response")
                    onResult(emptyList())
                    return
                }

                // JSON 응답을 로그로 출력
                val responseBody = response.body?.string()
//                Log.d("OkHttpResponse", "Response Body: $responseBody")

                if (responseBody != null) {
                    try {
                        // GSON을 사용해서 JSON 데이터를 TripIdData로 변환
                        val gson = Gson()
                        val tripData = gson.fromJson(responseBody, TripIdData::class.java)

                        // contentId만 추출
                        val contentIds = tripData.response.body.items.item.map { it.contentid }
                        onResult(contentIds)
                    } catch (e: Exception) {
//                        Log.e("OkHttp", "Error parsing JSON", e)
                        onResult(emptyList())
                    }
                }
            }
        }
    })
}

fun fetchTripDetailsWithOkHttp(contentId: String, onResult: (TourismItemDetails?) -> Unit) {
    val client = OkHttpClient()
    val serviceKey = "ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D"

    // API URL
    val url = "https://apis.data.go.kr/B551011/KorService1/detailCommon1?serviceKey=$serviceKey&MobileOS=ETC&MobileApp=AppTest&_type=json&contentId=$contentId&contentTypeId=12&defaultYN=Y&firstImageYN=Y&areacodeYN=Y&catcodeYN=Y&addrinfoYN=Y&mapinfoYN=Y&overviewYN=Y&numOfRows=10&pageNo=1"

    // API 요청 생성
    val request = Request.Builder()
        .url(url)
        .build()

    // 비동기 요청 처리
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("OkHttp", "Failed to fetch detailed data", e)
            onResult(null) // 실패 시 null 반환
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
//                    Log.e("OkHttp", "Unexpected code $response")
                    onResult(null)
                    return
                }

                val responseBody = response.body?.string()
//                Log.d("OkHttpResponse", "Response Body: $responseBody")

                if (responseBody != null) {
                    try {
                        val gson = Gson()
                        val tripDetailData = gson.fromJson(responseBody, TripDetailResponse::class.java)
                        val item = tripDetailData.response.body.items.item.firstOrNull()
                        onResult(item) // 성공 시 첫 번째 item 반환
                    } catch (e: Exception) {
//                        Log.e("OkHttp", "Error parsing JSON", e)
                        onResult(null)
                    }
                }
            }
        }
    })
}

// Define the data model for the response
data class TripDetailResponse(
    val response: TripDetailResponseBody
)

data class TripDetailResponseBody(
    val body: TripDetailResponseItems
)

data class TripDetailResponseItems(
    val items: TripDetailItems
)

data class TripDetailItems(
    val item: List<TourismItemDetails>
)

data class TourismItemDetails(
    val contentid: String,
    val title: String,
    val firstimage: String?,
    val addr1: String,
    val addr2: String?,
    val mapx: String,
    val mapy: String,
    val overview: String,
    val tel: String,
)

fun fetchSubImageUrl(contentId: String, onResult: (String?) -> Unit) {
    val client = OkHttpClient()
    val serviceKey = "ClEl7z%2F9nNW%2Fg0NNpuJsf6wBBPJV5UWiVxKC6SzME5GsWrUpQ85zpxv1aJY4Ockw3%2Bm03%2FeCIYyg60sfOqIOxg%3D%3D"

    val url = "https://apis.data.go.kr/B551011/KorService1/detailImage1?serviceKey=$serviceKey&MobileOS=ETC&MobileApp=AppTest&_type=json&contentId=$contentId&imageYN=Y&subImageYN=Y&numOfRows=1&pageNo=1"

    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("OkHttp", "Failed to fetch data", e)
            onResult(null)  // 실패 시 null 반환
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    Log.e("OkHttp", "Unexpected code $response")
                    onResult(null)
                    return
                }

                val responseBody = response.body?.string()
                Log.d("OkHttpResponse", "Response Body: $responseBody")

                responseBody?.let {
                    try {
                        val gson = Gson()
                        val apiResponse = gson.fromJson(it, SubImageApiResponse::class.java)
                        val originimgurl = apiResponse.response.body.items.item.firstOrNull()?.originimgurl
                        Log.d("fetchimageurl", "$originimgurl")
                        onResult(originimgurl)  // 성공 시 originimgurl 반환
                    } catch (e: Exception) {
                        Log.e("OkHttp", "Error parsing JSON", e)
                        onResult(null)
                    }
                }
            }
        }
    })
}

// API 응답 모델 정의
data class SubImageApiResponse(
    val response: SubImageResponseData
)

data class SubImageResponseData(
    val body: SubImageBody
)

data class SubImageBody(
    val items: SubImageItems
)

data class SubImageItems(
    val item: List<SubImageItem>
)

data class SubImageItem(
    val contentid: String,
    val originimgurl: String
)

fun removeHtmlTags(text: String): String {
    return text.replace(Regex("<.*?>"), "") // 모든 <> 안의 내용 제거
}

@Composable
fun TripPage(navController: NavController, city: String?) {
    var tripMainData by remember { mutableStateOf(listOf<TourismItemDetails>()) }
    var isBoxExpanded by remember { mutableStateOf(false) }  // Box 확장 여부 상태
    var selectedItem by remember { mutableStateOf<TourismItemDetails?>(null) }


    LaunchedEffect(Unit) {
        val (areaCode, sigunguCode) = when (city) {
            "영도" -> Pair(6, 14) // 부산 영도구
            "군위" -> Pair(4, 9) // 대구 군위군

            "남원" -> Pair(37, 4) // 전북 남원시
            "무주" -> Pair(37, 5) // 전북 무주군
            "순창" -> Pair(37, 7) // 전북 순창군
            "임실" -> Pair(37, 10) // 전북 임실군

            "고흥" -> Pair(38, 2) // 전남 고흥군
            "보성" -> Pair(38, 10) // 전남 보성군
            "신안" -> Pair(38, 12) // 전남 신안군
            "함평" -> Pair(38, 22) // 전남 함평군

            "고성" -> Pair(36, 3) // 경남 고성군
            "남해" -> Pair(36, 5) // 경남 남해군
            "하동" -> Pair(36, 18) // 경남 하동군
            "합천" -> Pair(36, 21) // 경남 합천군

            "문경" -> Pair(35, 7) // 경북 문경시
            "상주" -> Pair(35, 9) // 경북 상주시
            "안동" -> Pair(35, 11) // 경북 안동시
            "영천" -> Pair(35, 15) // 경북 영천시

            "평창" -> Pair(32, 15) // 강원 평창군
            "횡성" -> Pair(32, 18) // 강원 횡성군
            "태백" -> Pair(32, 14) // 강원 태백시
            "정선" -> Pair(32, 11) // 강원 정선군

            "괴산" -> Pair(33, 1) // 충북 괴산군
            "보은" -> Pair(33, 3) // 충북 보은군
            "영동" -> Pair(33, 4) // 충북 영동군
            "제천" -> Pair(33, 7) // 충북 제천시

            "보령" -> Pair(34, 5) // 충남 보령시
            "부여" -> Pair(34, 6) // 충남 부여군
            "공주" -> Pair(34, 1) // 충남 공주시
            "태안" -> Pair(34, 14) // 충남 태안군

            else -> Pair(34, 14) // Default to Seoul if no match
        }

        fetchTripIdDataWithOkHttp(areaCode, sigunguCode) { contentIds ->
            // For each contentId, fetch the detailed data
            contentIds.forEach { contentId ->
                fetchTripDetailsWithOkHttp(contentId) { detail ->
                    detail?.let {
                        // Add the detail to the list
                        tripMainData = tripMainData + it
                    }
                }
            }
        }
    }



    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var mapIsReady by remember { mutableStateOf(false) }

    val (lat, lon) = when (city) {
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

//    Log.d("TourismData", "$tripMainData")

    val options = NaverMapOptions()
        .camera(CameraPosition(LatLng(lat, lon), 11.0))

    val mapView = remember {
        MapView(context, options).apply {
            getMapAsync { naverMap ->
                mapIsReady = true
            }
        }
    }
    val markerClickListener = Overlay.OnClickListener { overlay ->
        val marker = overlay as Marker
        val clickedItem = tripMainData.find { it.title == marker.captionText }
        clickedItem?.let {
            selectedItem = it
            isBoxExpanded = true  // 마커를 클릭하면 Box를 확장
        }
        true
    }

    // Lifecycle 이벤트를 수신하기 위해 AndroidView의 밖에서 먼저 선언합니다.
    // recomposition시에도 유지되어야 하기 때문에 remember { } 로 기억합니다.
    LaunchedEffect(tripMainData, mapIsReady) {
        if (mapIsReady && tripMainData.isNotEmpty()) {
            mapView.getMapAsync { naverMap ->
                tripMainData.forEach { item ->
                    val marker = Marker()
                    marker.position = LatLng(item.mapy.toDouble(), item.mapx.toDouble()) // Lat and Lng
                    marker.captionText = item.title
                    marker.icon = MarkerIcons.RED
                    marker.width = Marker.SIZE_AUTO
                    marker.onClickListener = markerClickListener
                    marker.map = naverMap
                }
            }
        }
    }

    val lifecycleObserver = remember {
        LifecycleEventObserver { source, event ->
            // CoroutineScope 안에서 호출해야 정상적으로 동작합니다.
            coroutineScope.launch {
                when (event) {
                    Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                    else -> Unit
                }
            }
        }
    }

    // 뷰가 해제될 때 이벤트 리스너를 제거합니다.
    DisposableEffect(true) {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = {
                    isBoxExpanded = false  // 화면 바깥을 클릭하면 false로 설정
                }
            )
        }
    ) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(25.dp)
                .size(55.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false
                )
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
                .clickable { navController.popBackStack() }
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
            )
        }
        val boxHeight by animateDpAsState(
            targetValue = if (isBoxExpanded) 450.dp else 180.dp, label = ""
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isBoxExpanded = false
                        }
                    )
                }
                .fillMaxWidth()
                .height(boxHeight)
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
            val displayedText = when (city) {
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
                else -> city
            }
            val Description = when (city) {
            "영도" -> "부산의 영도구는 아름다운 해안 경치와 태종대, 흰여울 문화마을 같은 문화 명소로 유명합니다."
            "군위" -> "경상북도에 위치한 군위군은 팔공산을 비롯한 역사적 유적지와 자연경관으로 유명합니다."

            "남원" -> "전북 남원시는 한국의 유명한 고전 소설 \"춘향전\"의 배경이자 광한루원과 같은 아름다운 공원으로 잘 알려져 있습니다."
            "무주" -> "무주군은 무주리조트와 태권도 공원이 있는 겨울 스포츠의 중심지로, 전북에 위치하고 있습니다."
            "순창" -> "순창은 전통 고추장으로 유명하며, 강천산과 같은 아름다운 등산로로 많은 사람들이 찾습니다."
            "임실" -> "임실은 대한민국 최초의 치즈 생산지로, 임실치즈마을에서 치즈 체험을 할 수 있습니다."

            "고흥" -> "전남 고흥군은 나로우주센터가 있는 곳으로, 한국의 우주 산업을 상징하는 지역입니다."
            "보성" -> "보성은 넓은 녹차밭으로 유명하며, 한국에서 가장 큰 녹차 생산지 중 하나입니다."
            "신안" -> "신안군은 수천 개의 섬으로 이루어진 지역으로, 해양 생태 관광의 중심지입니다."
            "함평" -> "함평군은 매년 열리는 나비 축제로 유명하며, 아름다운 시골 풍경과 자연이 돋보이는 곳입니다."

            "고성" -> "경남 고성군은 공룡 화석지로 유명하며, 아름다운 해안선과 자연경관을 자랑합니다."
            "남해" -> "남해군은 독일마을과 다랭이논 등으로 유명하며, 아름다운 해안선이 있는 지역입니다."
            "하동" -> "하동군은 녹차밭과 섬진강으로 유명하며, 한국에서 녹차의 주요 생산지 중 하나입니다."
            "합천" -> "합천군은 유네스코 세계문화유산인 해인사로 유명하며, 여러 드라마 촬영지로도 유명합니다."

            "문경" -> "문경시는 전통 도자기와 문경새재로 잘 알려진 역사적 고장이며, 아름다운 자연경관을 자랑합니다."
            "상주" -> "상주시는 곶감 생산지로 유명하며, 경북의 대표적인 농업 도시 중 하나입니다."
            "안동" -> "안동시는 하회마을과 안동탈춤축제로 잘 알려진 전통문화의 중심지입니다."
            "영천" -> "영천시는 포도와 와인 산업으로 유명하며, 많은 전통 사찰이 있는 지역입니다."

            "평창" -> "평창군은 2018년 동계 올림픽 개최지로, 아름다운 스키 리조트와 야외 활동으로 잘 알려져 있습니다."
            "횡성" -> "횡성군은 한우(소고기)로 유명하며, 청정한 자연환경을 자랑합니다."
            "태백" -> "태백시는 석탄 산업의 중심지였으며, 태백산 눈축제로 잘 알려진 강원도 도시입니다."
            "정선" -> "정선군은 아름다운 철도와 전통 민요인 정선아리랑, 그리고 아웃도어 액티비티로 유명합니다."

            "괴산" -> "괴산군은 청정한 자연환경과 산악지대가 유명한 충청북도의 작은 군입니다."
            "보은" -> "보은군은 속리산 국립공원과 법주사로 유명한 역사적 명소입니다."
            "영동" -> "영동군은 포도와 와인 생산지로 유명하며, 아름다운 산과 숲이 있는 곳입니다"
            "제천" -> "제천시는 한방 산업과 청풍호로 유명하며, 전통 의학과 아름다운 경관을 제공합니다."

            "보령" -> "보령시는 매년 열리는 보령머드축제로 유명하며, 아름다운 해변과 진흙으로 많은 관광객을 끌어들입니다."
            "부여" -> "부여군은 백제의 옛 수도로, 백제 문화유적지와 국립부여박물관으로 유명합니다."
            "공주" -> "공주시도 백제 유적지로 유명하며, 공산성과 같은 유네스코 세계유산이 있는 곳입니다."
            "태안" -> "태안군은 해안 국립공원과 넓은 모래 해변으로 유명한 충남의 해안 지역입니다."
            else -> city
            }

            Column {



                if (!isBoxExpanded) {
                    Text(
                        text = "$displayedText",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "$Description",
                        modifier = Modifier.padding(8.dp)
                    )
                    Text(
                        text = "아이콘을 클릭하시면 상세정보가 나옵니다.",
                        color = Color(0xFFA093DE),
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    val dragAmount = remember { mutableStateOf(0f) }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp) // 회색 바의 높이 설정
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragEnd = {
                                        if (dragAmount.value > 50) { // 드래그 거리가 50 이상이면 축소
                                            isBoxExpanded = false
                                        }
                                        dragAmount.value = 0f // 드래그 값 초기화
                                    },
                                    onDrag = { change, dragAmountDelta ->
                                        dragAmount.value += dragAmountDelta.y
                                    }
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .width(120.dp)  // 너비 설정
                                .height(10.dp)  // 높이 설정
                                .background(Color.Gray, shape = RoundedCornerShape(16.dp))
                                .align(Alignment.Center)
                        ) {
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))  // 원하는 높이로 조절
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(430.dp)  // LazyColumn 내부를 고정된 높이로 설정
                    ) {
                        selectedItem?.let { item ->
                            item.addr1?.let { addr1 ->
                                item {
                                    Text("$addr1", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                }
                            }

                            item {
                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                            }

                            item {
                                var imageUrl by remember { mutableStateOf(item.firstimage) }  // 초기 상태로 firstimage 설정

                                LaunchedEffect(item) {  // item이 변경될 때마다 LaunchedEffect 실행
                                    if (item.firstimage.isNullOrEmpty()) {  // firstimage가 없을 경우만 API 호출
                                        fetchSubImageUrl(item.contentid) { fetchedUrl ->
                                            imageUrl = fetchedUrl ?: R.drawable.no_image_found.toString()  // null일 경우 기본 이미지 설정
                                        }
                                    } else {
                                        imageUrl = item.firstimage  // firstimage가 있으면 그대로 사용
                                    }
                                }

                                Log.d("imageUrl", "$imageUrl")

                                Image(
                                    painter = rememberImagePainter(
                                        data = imageUrl ?: R.drawable.no_image_found,
                                        builder = {
                                            crossfade(true)
                                            placeholder(R.drawable.placeholder_image)  // 로딩 중 표시할 이미지
                                            error(R.drawable.no_image_found)  // 에러 발생 시 표시할 이미지
                                        }
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(16.dp))  // 모서리를 둥글게 설정
                                        .border(
                                            width = 2.dp,  // 테두리 두께 설정
                                            color = Color(0xFFA093DE),  // 테두리 색상 설정 (A093DE)
                                            shape = RoundedCornerShape(16.dp)  // 테두리 모양도 둥글게
                                        ),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))  // 원하는 높이로 조절
                            }
                            item.overview?.let { overview ->
                                val cleanedOverview = removeHtmlTags(overview)  // HTML 태그 제거
                                item {
                                    Text(cleanedOverview, modifier = Modifier.padding(8.dp), fontSize = 15.sp)
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(10.dp))  // 원하는 높이로 조절
                            }
                            item.tel?.let { tel ->
                                item {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Call,  // 기본 아이콘 사용
                                            contentDescription = "Phone icon",
                                            modifier = Modifier.size(24.dp),  // 아이콘 크기 설정
                                            tint = Color(0xFFA093DE)  // 아이콘 색상 설정
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))  // Text와 Icon 사이에 여백 추가
                                        Text("$tel", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewTripPage() {
    val navController = rememberNavController()
    TripPage(navController, "영도")
}
