import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Address.AddressPostApi
import com.project.givuandtake.core.data.Address.AddressPostData
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.MyActivities.AddressPostViewModel
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject

class AddressPostViewModel : ViewModel() {
    fun postAddressData(token: String, addressRequest: AddressPostData, context: Context) {
        viewModelScope.launch {
            try {
                // Log 시작 지점
                Log.d("AddressPost", "주소 등록 시작: $addressRequest")

                val response = AddressPostApi.api.postAddressData("$token", addressRequest)

                // 응답 로그
                Log.d("AddressPost", "응답 코드: ${response.code()}")
                Log.d("AddressPost", "응답 메시지: ${response.message()}")

                if (response.isSuccessful) {
                    Log.d("AddressPost", "주소 등록 성공")
                    Toast.makeText(context, "주소가 성공적으로 등록되었습니다.", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("AddressPost", "주소 등록 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "주소 등록에 실패했습니다.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("AddressPost", "예외 발생: ${e.message}", e)
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}

fun reverseGeocode(lat: Double, lng: Double, onResult: (String, String, String, String, String, String, String, String) -> Unit) {
    val client = OkHttpClient()
    val url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?request=coordsToaddr&coords=$lng,$lat&sourcecrs=epsg:4326&output=json&orders=legalcode,addr,roadaddr"

    val request = Request.Builder()
        .url(url)
        .addHeader("X-NCP-APIGW-API-KEY-ID", "wlvq2as1zo")
        .addHeader("X-NCP-APIGW-API-KEY", "bzKsMW5ETkZSvLcvCMLthQgk9wbHtiEmV5MW2xBR")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            response.body?.string()?.let { responseBody ->
                val json = JSONObject(responseBody)
                val results = json.getJSONArray("results")

                Log.d("result", "$results")

                var roadAddress = "도로명 주소를 찾을 수 없음"
                var jibunAddress = "지번 주소를 찾을 수 없음"
                var postalCode = "우편번호 없음"
                var buildingName = "건물명 없음"
                var legalDong = "법정동/법정리 없음"
                var eupMyon = "읍/면 없음"
                var sido = "시/도 없음"
                var sigungu = "시/군/구 없음"

                for (i in 0 until results.length()) {
                    val result = results.getJSONObject(i)

                    when (result.getString("name")) {
                        "roadaddr" -> {
                            val region = result.getJSONObject("region")
                            val land = result.getJSONObject("land")
                            val area1 = region.getJSONObject("area1").getString("name")
                            val area2 = region.getJSONObject("area2").getString("name")
                            val roadName = land.getString("name")
                            roadAddress = "$area1 $area2 $roadName ${land.getString("number1")}"
                            sido = "$area1"
                            sigungu = "$area2"

                            // 우편번호와 건물명 추출
                            if (land.has("addition0") && land.getJSONObject("addition0").getString("type") == "building") {
                                buildingName = land.getJSONObject("addition0").getString("value")
                            }
                            if (land.has("addition1") && land.getJSONObject("addition1").getString("type") == "zipcode") {
                                postalCode = land.getJSONObject("addition1").getString("value")
                            }
                        }
                        "addr" -> {
                            val region = result.getJSONObject("region")
                            val land = result.getJSONObject("land")
                            val area1 = region.getJSONObject("area1").getString("name")
                            val area2 = region.getJSONObject("area2").getString("name")
                            val area3 = region.getJSONObject("area3").getString("name")
                            jibunAddress = "$area1 $area2 $area3 ${land.getString("number1")}-${land.getString("number2")}"

                            // 법정동/법정리 이름과 읍/면 이름 추출
                            legalDong = area3
                            if (region.has("area4")) {
                                eupMyon = region.getJSONObject("area4").getString("name")
                            }
                        }
                        "legalcode" -> {

                        }
                    }
                }

                // 콜백으로 필요한 정보 반환
                onResult(roadAddress, jibunAddress, postalCode, buildingName, legalDong, eupMyon, sido, sigungu)
            }
        }
    })
}



@Composable
fun AddressMapSearch(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current as Activity
    val scope = rememberCoroutineScope()
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val viewModel: AddressPostViewModel = viewModel()

    // FusedLocationSource를 생성합니다.
    val locationSource = remember { FusedLocationSource(activity, LOCATION_PERMISSION_REQUEST_CODE) }

    var mapIsReady by remember { mutableStateOf(false) }
    var naverMap: NaverMap? = null
    var currentLocation by remember { mutableStateOf(LatLng(37.5665, 126.9780)) } // Default to Seoul
    var centerMarker: Marker? = remember { Marker() }
    var centerCoordinates by remember { mutableStateOf(LatLng(37.5665, 126.9780)) } // To store center marker coordinates

    var roadAddressData by remember { mutableStateOf("도로명 주소를 찾을 수 없음") }
    var jibunAddressData by remember { mutableStateOf("지번 주소를 찾을 수 없음") }
    var postalCodeData by remember { mutableStateOf("우편번호를 찾을 수 없음") }
    var buildingNameData by remember { mutableStateOf("빌딩이름을 찾을 수 없음") }
    var legalDongData by remember { mutableStateOf("법정동을 찾을 수 없음") }
    var eupMyonData by remember { mutableStateOf("법정면을 찾을 수 없음") }
    var sidoData by remember { mutableStateOf("시도를 찾을 수 없음") }
    var sigunguData by remember { mutableStateOf("시군구를 찾을 수 없음") }

    var settingCustomJuso by remember { mutableStateOf(false) }
    var openCustomAddress by remember { mutableStateOf(false) }

    var detailedAddress by remember { mutableStateOf("") }
    var addressName by remember { mutableStateOf("") }

    val addressRequest = AddressPostData(
        zoneCode = postalCodeData,
        addressName = "$addressName",
        address = "$sidoData $sigunguData",
        roadAddress = roadAddressData,
        jibunAddress = jibunAddressData,
        detailAddress = "$detailedAddress",
        buildingName = buildingNameData,
        isApartment = false,
        sido = sidoData,
        sigungu = sigunguData,
        bname = legalDongData,
        bname1 = eupMyonData,
        isRepresentative = true
    )

    // 권한 요청을 위한 런처 설정
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // 권한이 허용되었을 때 위치를 가져옴
            getCurrentLocation(activity) { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
            }
        } else {
            // 권한이 거부되었을 때 처리 (사용자에게 권한이 필요하다는 안내 가능)
        }
    }

    // 권한 상태를 체크하고 권한을 요청
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없을 경우 권한 요청 실행
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        } else {
            // 권한이 이미 있을 경우 현재 위치 가져오기
            getCurrentLocation(activity) { location ->
                currentLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }

    // 초기 카메라 위치 설정
    val options = NaverMapOptions().camera(com.naver.maps.map.CameraPosition(currentLocation, 19.0))

    // MapView 생성
    val mapView = remember {
        MapView(context, options).apply {
            getMapAsync { nMap ->
                naverMap = nMap
                mapIsReady = true

                // FusedLocationSource를 네이버 맵에 설정
                naverMap?.locationSource = locationSource

                naverMap?.uiSettings?.apply {
                    isLocationButtonEnabled = true // 현재 위치 버튼 활성화
                }

                // 위치 추적 모드를 설정 (Follow로 설정하면 사용자의 위치를 따라 이동합니다)
                naverMap?.locationTrackingMode = LocationTrackingMode.Follow

                // Set the marker at the center of the map
                centerMarker?.apply {
                    position = naverMap?.cameraPosition?.target ?: currentLocation
                    map = naverMap
                }

                // 카메라가 움직일 때 마커 위치 업데이트 및 투명도 설정
                naverMap?.addOnCameraChangeListener { _, _ ->
                    centerMarker?.apply {
                        position = naverMap?.cameraPosition?.target ?: currentLocation
                        alpha = 0.5f  // 투명도 설정
                    }
                }

                // 카메라가 멈췄을 때 마커 불투명하게 변경
                naverMap?.addOnCameraIdleListener {
                    val newCenter = naverMap?.cameraPosition?.target ?: currentLocation
                    centerMarker?.apply {
                        position = newCenter
                        alpha = 1f  // 투명도 복원
                    }
                    centerCoordinates = newCenter // Update the coordinates for the Text

                    // Reverse geocode to get the address
                    reverseGeocode(centerCoordinates.latitude, centerCoordinates.longitude) { roadAddress, jibunAddress, postalCode, buildingName, legalDong, eupMyon, sido, sigungu ->
                        roadAddressData = roadAddress
                        jibunAddressData = jibunAddress
                        postalCodeData = postalCode
                        buildingNameData = buildingName
                        legalDongData = legalDong
                        eupMyonData = eupMyon
                        sidoData = sido
                        sigunguData = sigungu
                        Log.d("AddressMapSearch", "도로명 주소: $roadAddress, 지번 주소: $jibunAddress, 우편번호: $postalCode, 건물명: $buildingName, 법정동: $legalDong, 읍/면: $eupMyon")
                    }
                }

            }
        }
    }

    // 생명주기 관리
    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
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

    DisposableEffect(true) {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    // UI 레이아웃
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
                    .weight(0.3f)
            )

            Spacer(modifier = Modifier.weight(0.7f))

            Text(
                text = "현재 주소 설정",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        if (!settingCustomJuso) {

            // 네이버 지도 표시
            AndroidView(factory = { mapView }, modifier = Modifier.height(630.dp))

            // 하단 박스 (현재 위치 설정 버튼 예시)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = roadAddressData,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = jibunAddressData,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 13.dp)
                    )

                    Button(
                        onClick = { settingCustomJuso = true },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4)), // 버튼의 배경색
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "이 위치로 주소 설정",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(15.dp),
            ) {
                Text(
                    text = roadAddressData,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )
                Text(
                    text = jibunAddressData,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .padding(bottom = 13.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .border(
                            border = BorderStroke(2.dp, Color(0xFFA093DE)),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = detailedAddress,
                            onValueChange = { newValue -> detailedAddress = newValue },
                            modifier = Modifier
                                .weight(1f)
                                .background(Color.White)
                                .padding(1.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                            decorationBox = { innerTextField ->
                                if (detailedAddress.isEmpty()) {
                                    Text(
                                        text = "건물명, 동/호수 등의 상세주소 입력",
                                        color = Color.Gray
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (detailedAddress.isNotEmpty()) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_backspace_24),
                                contentDescription = "Clear Text",
                                tint = Color(0xFFA093DE),
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable {
                                        detailedAddress = ""
                                    }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { addressName = "우리집"
                            openCustomAddress = false },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                border = if (addressName == "우리집" && openCustomAddress == false) BorderStroke(2.dp, Color(0xFFA093DE)) else BorderStroke(0.dp, Color.Transparent),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_house_24),
                            contentDescription = "Clear Text",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text("우리집")
                    }

                    Button(
                        onClick = {
                            addressName = "회사"
                            openCustomAddress = false },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                border = if (addressName == "회사" && openCustomAddress == false) BorderStroke(2.dp, Color(0xFFA093DE)) else BorderStroke(0.dp, Color.Transparent),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_apartment_24),
                            contentDescription = "Clear Text",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text("회사")
                    }

                    Button(
                        onClick = { openCustomAddress = true
                            addressName = ""},
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                border = if (openCustomAddress == true) BorderStroke(2.dp, Color(0xFFA093DE)) else BorderStroke(0.dp, Color.Transparent),
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_location_on_24),
                            contentDescription = "Clear Text",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier
                                .size(20.dp)
                        )
                        Text("직접입력")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (openCustomAddress == true) {
                    Box(
                        modifier = Modifier
                            .border(
                                border = BorderStroke(2.dp, Color(0xFFA093DE)),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = addressName,
                                onValueChange = { newValue -> addressName = newValue },
                                modifier = Modifier
                                    .weight(1f)
                                    .background(Color.White)
                                    .padding(1.dp),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    Row() {
                                        if (addressName.isEmpty()) {
                                            Text(
                                                text = "예) 학교, 아현이집",
                                                color = Color.Gray
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                            if (addressName != "학교" && addressName != "우리집" && addressName != "직접입력") {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_backspace_24),
                                    contentDescription = "Clear Text",
                                    tint = Color(0xFFA093DE),
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            addressName = ""
                                        }
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.postAddressData(accessToken, addressRequest, context, navController)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(24.dp))
                            .height(55.dp)
                            .align(Alignment.BottomCenter),
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
                    ) {
                        Text(text = "주소 등록하기", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }

    }
}

fun getCurrentLocation(activity: Activity, onLocationReceived: (Location) -> Unit) {
    val locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
    if (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        location?.let {
            Log.d("AddressMapSearch", "Current location: $it")
            onLocationReceived(it)
        }
    }
}

private const val LOCATION_PERMISSION_REQUEST_CODE = 1000