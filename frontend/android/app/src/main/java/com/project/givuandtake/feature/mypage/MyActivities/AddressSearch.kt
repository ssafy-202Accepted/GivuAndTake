package com.project.givuandtake.feature.mypage.MyActivities

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Address.AddressPostApi
import com.project.givuandtake.core.data.Address.AddressPostData
import com.project.givuandtake.core.data.Address.Juso
import com.project.givuandtake.core.data.Address.JusoResponse
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class AddressPostViewModel : ViewModel() {
    fun postAddressData(token: String, addressRequest: AddressPostData, context: Context, navController: NavController) {
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
                    navController.navigate("addressbook") {
                        popUpTo("addressbook") { inclusive = true } // 중복 방지를 위해 이전 스택 제거
                    }
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

fun fetchAddressResults(keyword: String, onResult: (List<Juso>) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://business.juso.go.kr/addrlink/addrLinkApi.do?confmKey=devU01TX0FVVEgyMDI0MTAwOTAwMDM1MDExNTE0Mjg=&currentPage=1&countPerPage=15&keyword=$keyword&resultType=json&firstSort=road")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("도로명주소", "실패")
            onResult(emptyList())
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!response.isSuccessful) {
                    Log.d("도로명주소 실패", "실패")
                    onResult(emptyList())
                    return
                }

                // JSON 응답 처리
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        Log.d("도로명주소 응답", "Raw JSON 응답: $responseBody")  // 응답을 먼저 로깅
                        val gson = Gson()
                        val jusoResponse = gson.fromJson(responseBody, JusoResponse::class.java)

                        Log.d("도로명주소", "JSON 파싱 성공: ${jusoResponse.results.juso.size}개의 주소")
                        onResult(jusoResponse.results.juso)
                    } catch (e: Exception) {
                        Log.e("도로명주소", "JSON 파싱 오류: ${e.message}", e)
                        Log.d("도로명주소 불러오기2", "실패: $responseBody")  // 실패 시 Raw JSON 응답 로깅
                        onResult(emptyList())
                    }
                } else {
                    Log.e("도로명주소 실패", "응답 본문이 비어 있음")
                    onResult(emptyList())
                }
            }
        }
    })
}


@Composable
fun AddressSearch(navController: NavController) {
    var addressInput by remember { mutableStateOf("") }
    var searchResults by remember {
        mutableStateOf<List<Juso>>(
            listOf(
                Juso(
                    roadAddr = "1",
                    jibunAddr = "지번 주소",
                    zipNo = "우편번호",
                    bdNm = "건물명",
                    bdKdcd = "건물 코드",
                    siNm = "시 이름",
                    sggNm = "구 이름",
                    emdNm = "읍면동 이름",
                    liNm = "리 이름"
                )
            )
        )
    }
    val scope = rememberCoroutineScope()
    var selectedJuso by remember { mutableStateOf<Juso?>(null) }
    var detailedAddress by remember { mutableStateOf("") }
    var addressName by remember { mutableStateOf("") }
    var openCustomAddress by remember { mutableStateOf(false) }

    val viewModel: AddressPostViewModel = viewModel()
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    val addressRequest = AddressPostData(
        zoneCode = "${selectedJuso?.zipNo}",
        addressName = "$addressName",
        address = "${selectedJuso?.siNm} ${selectedJuso?.sggNm}",
        roadAddress = "${selectedJuso?.roadAddr}",
        jibunAddress = "${selectedJuso?.jibunAddr}",
        detailAddress = "$detailedAddress",
        buildingName = "${selectedJuso?.bdNm}",
        isApartment = when (selectedJuso?.bdKdcd) {
            "1" -> true
            "2" -> false
            else -> false
        },
        sido = "${selectedJuso?.siNm}",
        sigungu = "${selectedJuso?.sggNm}",
        bname = "${selectedJuso?.emdNm}",
        bname1 = "${selectedJuso?.liNm}",
        isRepresentative = true
    )

    Log.d("AddressPost", "${addressName}")

    Column() {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically, // Row 내 수직 정렬
            horizontalArrangement = Arrangement.SpaceBetween // 좌우 정렬
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
                text = "주소검색",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f), fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }


        if (selectedJuso == null) {
            Text("배송받을 주소를 \n입력해 주세요", modifier=Modifier.padding(start=17.dp), fontSize = 20.sp)
            Spacer(modifier = Modifier.height(15.dp))
            Box(
                modifier = Modifier
                    .padding(10.dp)
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
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_search_24),
                        contentDescription = "Icon",
                        tint = Color(0xFFA093DE),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = addressInput,
                        onValueChange = { newValue -> addressInput = newValue },
                        modifier = Modifier
                            .weight(1f)
                            .background(Color.White)
                            .padding(1.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                // 비동기 요청에서 콜백을 통해 결과 처리
                                fetchAddressResults(addressInput) { result ->
                                    searchResults = result
                                }
                            }
                        ),
                        decorationBox = { innerTextField ->
                            if (addressInput.isEmpty()) {
                                Text(
                                    text = "도로명 주소를 입력하세요",
                                    color = Color.Gray
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (addressInput.isNotEmpty()) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_backspace_24),
                            contentDescription = "Clear Text",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier
                                .size(20.dp)
                                .clickable {
                                    addressInput = ""
                                }
                        )
                    }
                }
            }
            when {
                // 처음 임의의 데이터가 있을 때 검색 예시 표시
                searchResults?.firstOrNull()?.roadAddr == "1" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "이렇게 검색해보세요",
                            style = MaterialTheme.typography.body1,
                            color = Color.Gray,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            text = "- 도로명 + 건물번호 (위례성대로 2)\n" +
                                    "- 건물명 + 번지 (방이동 44-2)\n" +
                                    "- 건물명, 아파트명 (반포 자이, 분당 주공 1차)",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                }

                // 검색 결과가 들어왔을 때 리스트 표시
                searchResults != null && searchResults!!.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(searchResults!!) { index, juso ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedJuso = juso
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = juso.zipNo,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                )
                                Text(
                                    text = juso.roadAddr,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = juso.jibunAddr,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 15.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Divider(
                                    color = Color.Gray,
                                    thickness = 1.dp,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // 검색 결과가 없을 때 "검색결과가 없습니다!" 표시
                searchResults == null || searchResults!!.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_report_problem_24),
                            contentDescription = "Clear Text",
                            tint = Color.Gray,
                            modifier = Modifier.size(50.dp)
                        )
                        Text(
                            text = "검색결과가 없습니다!",
                            style = MaterialTheme.typography.body1,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(8.dp),
            ) {
                Text(
                    text = selectedJuso!!.roadAddr,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = selectedJuso!!.jibunAddr,
                    fontSize = 13.sp,
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
                                border = if (addressName == "우리집" && openCustomAddress == false) BorderStroke(
                                    2.dp,
                                    Color(0xFFA093DE)
                                ) else BorderStroke(0.dp, Color.Transparent),
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
                                border = if (addressName == "회사" && openCustomAddress == false) BorderStroke(
                                    2.dp,
                                    Color(0xFFA093DE)
                                ) else BorderStroke(0.dp, Color.Transparent),
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
                                border = if (openCustomAddress == true) BorderStroke(
                                    2.dp,
                                    Color(0xFFA093DE)
                                ) else BorderStroke(0.dp, Color.Transparent),
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