package com.project.givuandtake.feature.fundinig

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.givuandtake.core.apis.Funding.CommentData
import com.project.givuandtake.core.apis.Funding.CommentResponse
import com.project.givuandtake.core.apis.Funding.FundingCommentsApi
import com.project.givuandtake.core.apis.Funding.FundingDetailApi
import com.project.givuandtake.core.apis.Funding.FundingDetailData
import com.project.givuandtake.core.apis.Funding.FundingDetailResponse
import com.project.givuandtake.core.apis.Funding.WriteCommentRequest
import com.project.givuandtake.core.apis.Funding.WriteCommentResponse
import com.project.givuandtake.core.apis.Funding.WriteFundingCommentApi
import androidx.compose.ui.res.painterResource
import com.google.gson.Gson
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Funding.DeleteCommentResponse
import com.project.givuandtake.core.apis.Funding.DeleteFundingCommentApi
import com.project.givuandtake.core.apis.TourismIdRetrofitInstance.gson
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.core.datastore.TokenManager.getUserIdFromToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundingDetailPage(
    fundingIdx: Int,
    navController: NavController,
    onBackClick: () -> Unit
) {
    var fundingDetail by remember { mutableStateOf<FundingDetailData?>(null) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val tabs = listOf("사업소개", "응원메시지", "기부후기")
    val gson = Gson() // Gson 객체 생성

    // 날짜 비교에 필요한 LocalDateTime 가져오기
    val currentDate = java.time.LocalDate.now()


    // 응원 메시지 데이터를 위한 상태 변수
    var comments by remember { mutableStateOf<List<CommentData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) } // 데이터 로딩 상태
    var commentText by remember { mutableStateOf("") } // 댓글 입력 상태
    var isPosting by remember { mutableStateOf(false) } // 댓글 작성 중 상태
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val userId = TokenManager.getUserIdFromToken(context)
    val loggedInUserName = TokenManager.getUserNameFromToken(context) // 로그인한 사용자 이름 가져오기
    var showDeleteDialog by remember { mutableStateOf(false) } // 모달창 상태 관리
    var selectedCommentIdx by remember { mutableStateOf<Int?>(null) } // 삭제할 댓글의 ID 저장

    Log.d("LoggedInUserName", "Logged in user: $loggedInUserName")
    Log.d("AccessToken", "Token: $accessToken")


    // 펀딩 상세 데이터 로드
    LaunchedEffect(fundingIdx) {
        fetchFundingDetail(fundingIdx) { detail ->
            fundingDetail = detail
        }

        // API 호출로 응원 메시지 데이터 가져오기
        fetchFundingComments(fundingIdx) { response ->
            comments = response.data
            isLoading = false
        }
    }
    // 삭제 확인 모달
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "댓글 삭제") },
            text = { Text("댓글을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedCommentIdx?.let { commentIdx ->
                        deleteComment(
                            accessToken = accessToken,
                            fundingIdx = fundingIdx,
                            commentIdx = commentIdx,
                            onSuccess = {
                                fetchFundingComments(fundingIdx) { response ->
                                    comments = response.data
                                }
                                showDeleteDialog = false
                            }
                        )
                    }
                }) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    Log.d("fundingDetail","${fundingDetail}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "펀딩 상세보기",
                        fontWeight = FontWeight.Medium  // 글꼴 두께를 Medium으로 설정
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        bottomBar = {
            fundingDetail?.let { detail ->
                // 종료된 펀딩인지 확인 (endDate가 현재 날짜보다 이전이면 완료)
                val isFundingComplete = detail.endDate < currentDate.toString()

                Button(
                    onClick = {
                        if (!isFundingComplete) {
                            // 기부하기 동작
                            val fundingDetailJson = gson.toJson(fundingDetail)
                            val encodedFundingDetailJson = URLEncoder.encode(fundingDetailJson, StandardCharsets.UTF_8.toString())
                            navController.navigate("payment/$encodedFundingDetailJson")
                        }
                    },
                    enabled = !isFundingComplete, // 펀딩이 종료되면 버튼 비활성화
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFundingComplete) Color.Gray else Color.Blue  // 종료되면 회색 버튼
                    )
                ) {
                    Text(
                        text = if (isFundingComplete) "펀딩 종료됨" else "기부하기",
                        color = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        fundingDetail?.let { detail ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .fillMaxSize()
            ) {
                // 이미지 표시
// 이미지 표시
                AsyncImage(
                    model = detail.fundingThumbnail,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .border(
                            width = 2.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp)),  // 이미지도 border처럼 둥글게
                    contentScale = ContentScale.Crop  // 이미지를 border 안에 꽉 채움
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    // 위치 및 제목
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "위치 아이콘",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp)
                            .height(8.dp))
                        Text(
                            text = "${detail.sido} ${detail.sigungu}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = detail.fundingTitle,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    // 기간 표시
                    Text(text = "${detail.startDate} ~ ${detail.endDate}")

                    // 목표 금액과 모금된 금액
                    val formattedGoalAmount =
                        NumberFormat.getNumberInstance(Locale.KOREA).format(detail.goalMoney)
                    val formattedTotalMoney =
                        NumberFormat.getNumberInstance(Locale.KOREA).format(detail.totalMoney)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "기부 총액 ",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.alignByBaseline()
                        )
                        Text(
                            text = "${formattedTotalMoney}",
                            fontSize = 20.sp,
                            color = Color.Blue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alignByBaseline()
                        )
                        Text(
                            text = "원",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.alignByBaseline()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = if (detail.goalMoney > 0) detail.totalMoney / detail.goalMoney.toFloat() else 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color.Blue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${(detail.totalMoney / detail.goalMoney.toFloat() * 100).toInt()}%",
                        color = Color.Blue
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tabs (사업소개, 응원메시지, 기부후기)
                    TabRow(selectedTabIndex = selectedTabIndex) {
                        tabs.forEachIndexed { index, tabTitle ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = {
                                    scope.launch {
                                        selectedTabIndex = index
                                        if (index == 1) {
                                            scrollState.animateScrollTo(scrollState.maxValue)
                                        }
                                    }
                                },
                                text = { Text(tabTitle) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                when (selectedTabIndex) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // 이미지 표시 (fundingContentImage가 null 또는 빈 문자열이 아닐 경우)
                            if (!detail.fundingContentImage.isNullOrEmpty()) {
                                AsyncImage(
                                    model = detail.fundingContentImage,
                                    contentDescription = "사업 소개 이미지",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .border(
                                            width = 2.dp,
                                            color = Color.Gray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clip(RoundedCornerShape(8.dp)),  // 이미지도 border처럼 둥글게
                                    contentScale = ContentScale.Crop  // 이미지를 border 안에 꽉 채움
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                            }
                            // 텍스트 표시 (fundingContent)
                            Text(
                                text = detail.fundingContent,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }

                    1 -> {
                        var commentText by remember { mutableStateOf("") }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "따뜻한 댓글을 남겨주세요",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = commentText,
                                onValueChange = { commentText = it },
                                placeholder = { Text("댓글 남기기") },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    backgroundColor = Color.White,
                                    focusedBorderColor = Color.Gray,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 작성 버튼 (댓글 작성 중에는 버튼 비활성화)
                            Button(
                                onClick = {
                                    isPosting = true
                                    // 댓글 작성 API 호출
                                    writeComment(
                                        accessToken = accessToken,
                                        fundingIdx = fundingIdx,
                                        commentContent = commentText,
                                        onSuccess = {
                                            // 댓글 작성 성공 시 UI 갱신
                                            fetchFundingComments(fundingIdx) { response ->
                                                comments = response.data
                                                isLoading = false
                                                commentText = "" // 댓글 작성 후 입력란 초기화
                                                isPosting = false
                                            }
                                        },
                                        onFailure = {
                                            // 실패 처리 로직 (필요 시)
                                            isPosting = false
                                        }
                                    )
                                },
                                enabled = commentText.isNotEmpty() && !isPosting, // 텍스트가 비어있지 않으면 활성화
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .size(width = 60.dp, height = 30.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White
                                ),
                                border = BorderStroke(1.dp, Color.Gray),
                                contentPadding = PaddingValues(
                                    horizontal = 0.dp,
                                    vertical = 0.dp
                                )
                            ) {
                                Text("작성", color = Color.Black)
                            }


                            Spacer(modifier = Modifier.height(4.dp))

                            Divider(
                                color = Color.Gray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            Text(
                                text = "댓글 ${comments.size}개",
                                fontWeight = FontWeight.Bold
                            )

                            // 로딩 상태 표시
                            if (isLoading) {
                                Text("댓글을 불러오는 중입니다...")
                            } else {
                                // 실제 응원 메시지 데이터 표시
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                ) {
                                    comments.forEach { comment ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically // 세로 중앙 정렬

                                        ) {
                                            // 이미지와 이름을 세로로 정렬
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.padding(end = 8.dp) // 오른쪽에 간격 추가
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.hamo), // drawable 리소스에서 이미지 가져오기
                                                    contentDescription = "프로필 이미지",
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(RoundedCornerShape(20.dp))
                                                        .border(
                                                            1.dp,
                                                            Color.Gray,
                                                            RoundedCornerShape(20.dp)
                                                        ),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Spacer(modifier = Modifier.height(4.dp)) // 이미지와 텍스트 사이 간격
                                                Text(
                                                    text = comment.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                            }

                                            // 댓글 내용을 오른쪽에 표시
                                            Box(
                                                modifier = Modifier
                                                    .border(
                                                        1.dp,
                                                        Color.LightGray,
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .padding(12.dp)
                                                    .wrapContentSize() // 가로 공간의 80%를 차지하도록 설정
                                                    .widthIn(max = 270.dp)
                                                    .heightIn(min = 40.dp), // 최소 높이를 설정
                                                        contentAlignment = Alignment.Center

                                            ) {
                                                Text(
                                                    text = comment.commentContent,
                                                    fontSize = 14.sp
                                                )
                                            }
                                            // 만약 현재 댓글의 작성자가 로그인한 사용자라면 쓰레기통 아이콘 표시
                                            if (comment.name != loggedInUserName) {
                                                IconButton(
                                                    onClick = {
                                                        // 삭제 확인 모달을 보여주고, 선택된 댓글의 ID를 저장
                                                        selectedCommentIdx = comment.commentIdx
                                                        showDeleteDialog = true
                                                    },
                                                    modifier = Modifier
                                                        .size(24.dp) // 아이콘 크기를 작게 설정
                                                        .padding(start = 4.dp) // 아이콘과 댓글 내용을 더 가깝게 배치
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Delete,
                                                        contentDescription = "댓글 삭제",
                                                        tint = Color.Gray,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }}
                    2 -> Text(text = "기부후기: 아직 추가되지 않았습니다.")
                }

                Spacer(modifier = Modifier.height(50.dp))
            }
        } ?: run {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text("아직 응원메시지가 없습니다.")
            }
        }
    }
}

// 펀딩 상세 데이터 가져오기
fun fetchFundingDetail(fundingIdx: Int, onSuccess: (FundingDetailData) -> Unit) {
    val call = FundingDetailApi.api.getFundingDetail(fundingIdx)
    call.enqueue(object : Callback<FundingDetailResponse> {
        override fun onResponse(call: Call<FundingDetailResponse>, response: Response<FundingDetailResponse>) {
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { onSuccess(it) }
            }
        }

        override fun onFailure(call: Call<FundingDetailResponse>, t: Throwable) {
            // 에러 처리
        }
    })
}

// 응원 메시지 가져오기
fun fetchFundingComments(fundingIdx: Int, onSuccess: (CommentResponse) -> Unit) {
    val call = FundingCommentsApi.api.getFundingComments(fundingIdx)
    call.enqueue(object : Callback<CommentResponse> {
        override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.let { onSuccess(it) }
            }
        }

        override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
            // 에러 처리
        }
    })
}

// 댓글 작성 API 호출 함수
fun writeComment(
    accessToken: String,
    fundingIdx: Int,
    commentContent: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val call = WriteFundingCommentApi.api.writeFundingComment(
        authorization = accessToken,
        fundingIdx = fundingIdx,
        request = WriteCommentRequest(commentContent)
    )

    call.enqueue(object : Callback<WriteCommentResponse> {
        override fun onResponse(call: Call<WriteCommentResponse>, response: Response<WriteCommentResponse>) {
            if (response.isSuccessful && response.body()?.success == true) {
                onSuccess()
            } else {
                onFailure()
            }
        }

        override fun onFailure(call: Call<WriteCommentResponse>, t: Throwable) {
            onFailure()
        }
    })
}
// 댓글 삭제 API 호출 함수
fun deleteComment(
    accessToken: String,
    fundingIdx: Int,
    commentIdx: Int,
    onSuccess: () -> Unit
) {
    DeleteFundingCommentApi.api.deleteFundingComment(
        authorization = accessToken,
        fundingIdx = fundingIdx,
        commentIdx = commentIdx
    ).enqueue(object : Callback<DeleteCommentResponse> {
        override fun onResponse(call: Call<DeleteCommentResponse>, response: Response<DeleteCommentResponse>) {
            if (response.isSuccessful && response.body()?.success == true) {
                onSuccess()
            }
        }

        override fun onFailure(call: Call<DeleteCommentResponse>, t: Throwable) {
            // 에러 처리
        }
    })
}
