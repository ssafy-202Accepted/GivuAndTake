package com.project.givuandtake.feature.fundinig

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Funding.FundingData
import com.project.givuandtake.core.apis.Funding.FundingResponse
import com.project.givuandtake.core.apis.Funding.SearchFundingApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FundingMainPage(navController: NavController) {
    var selectedCategory by remember { mutableStateOf("재난·재해") }  // 카테고리 선택 변수
    var allFundingCards by remember { mutableStateOf<List<FundingData>>(emptyList()) }  // 전체 펀딩 데이터
    var displayedFundingCards by remember { mutableStateOf<List<FundingData>>(emptyList()) }  // 화면에 표시되는 펀딩 데이터
    var selectedState by remember { mutableStateOf(1) }  // 진행 중 or 완료 상태
    var expandedSort by remember { mutableStateOf(false) }  // 정렬 드롭다운 상태
    var selectedSort by remember { mutableStateOf("종료 임박순") }  // 정렬 옵션
    var itemsToShow by remember { mutableStateOf(6) }  // 처음에 표시할 데이터 수

    // 카테고리에 따른 데이터 로드
    LaunchedEffect(selectedCategory, selectedState, selectedSort) {
        val type = if (selectedCategory == "재난·재해") "D" else "R"
        fetchFundingData(type, selectedState) { result ->
            allFundingCards = result.sortedBySortOption(selectedSort)
            itemsToShow = 6 // 카테고리 변경 시 초기화
            displayedFundingCards = allFundingCards.take(itemsToShow) // 처음에는 6개만 표시
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxHeight() // 화면 전체 사용
    ) {
        // 카테고리 선택 탭
        CategoryTabs(
            selectedCategory = selectedCategory,
            onSelectCategory = { category ->
                selectedCategory = category
                itemsToShow = 6 // 카테고리 변경 시 데이터 초기화
                displayedFundingCards = allFundingCards.take(itemsToShow)
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 진행 상태 선택 라디오 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedState == 1,
                    onClick = {
                        selectedState = 1
                        itemsToShow = 6
                        displayedFundingCards = allFundingCards.take(itemsToShow)
                    }
                )
                Text(text = "진행 중", fontSize = 16.sp)
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(
                    selected = selectedState == 2,
                    onClick = {
                        selectedState = 2
                        itemsToShow = 6
                        displayedFundingCards = allFundingCards.take(itemsToShow)
                    }
                )
                Text(text = "완료", fontSize = 16.sp)
            }

            // 정렬 드롭다운
            Box {
                TextButton(onClick = { expandedSort = true }) {
                    Text(text = selectedSort)
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expandedSort,
                    onDismissRequest = { expandedSort = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("종료 임박순") },
                        onClick = {
                            selectedSort = "종료 임박순"
                            allFundingCards = allFundingCards.sortedBySortOption(selectedSort)
                            displayedFundingCards = allFundingCards.take(itemsToShow)
                            expandedSort = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("최근 등록순") },
                        onClick = {
                            selectedSort = "최근 등록순"
                            allFundingCards = allFundingCards.sortedBySortOption(selectedSort)
                            displayedFundingCards = allFundingCards.take(itemsToShow)
                            expandedSort = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 펀딩 리스트와 더보기 버튼
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxHeight()  // 화면의 남은 높이를 모두 사용
        ) {
            items(displayedFundingCards) { card ->
                FundingCardComposable(
                    title = card.fundingTitle,
                    location = "${card.sido} ${card.sigungu}",
                    startDate = card.startDate,
                    endDate = card.endDate,
                    nowAmount = card.totalMoney.toFloat(),
                    goalAmount = card.goalMoney.toFloat(),
                    imageUrl = card.fundingThumbnail,
                    fundingIdx = card.fundingIdx,  // 각 카드에 fundingIdx를 전달
                    onClick = { fundingIdx ->
                        // 클릭 시 상세 페이지로 이동하는 로직
                        navController.navigate("funding_detail/$fundingIdx")
                    }
                )
            }

            // 더보기 버튼 (더 불러올 데이터가 있을 때만 표시)
            if (itemsToShow < allFundingCards.size) {
                item(span = { GridItemSpan(2) }) {  // 두 열을 모두 차지하도록 설정
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            itemsToShow += 6  // 더보기 버튼을 누를 때마다 6개씩 더 보여줌
                            displayedFundingCards = allFundingCards.take(itemsToShow)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)  // 중앙 정렬
                    ) {
                        Text("더보기")
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Arrow Down Icon")
                    }
                }
            }
        }
    }
}

// 정렬 기준에 따른 정렬 함수
fun List<FundingData>.sortedBySortOption(sortOption: String): List<FundingData> {
    return when (sortOption) {
        "종료 임박순" -> this.sortedBy { it.endDate }  // endDate가 가까운 순으로 정렬
        "최근 등록순" -> this.sortedByDescending { it.fundingIdx }  // fundingIdx가 높은 순으로 정렬
        else -> this
    }
}

// 카테고리 선택 탭 (재난·재해 또는 지역 기부)
@Composable
fun CategoryTabs(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryTabItem(
            category = "재난·재해",
            isSelected = selectedCategory == "재난·재해",
            onClick = { onSelectCategory("재난·재해") },
            modifier = Modifier.weight(1f)
        )

        CategoryTabItem(
            category = "지역 기부",
            isSelected = selectedCategory == "지역 기부",
            onClick = { onSelectCategory("지역 기부") },
            modifier = Modifier.weight(1f)
        )
    }
}

// 카테고리 탭 항목
@Composable
fun CategoryTabItem(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
    ) {
        Text(
            text = category,
            fontSize = 22.sp, // 모든 탭에 동일한 크기 유지
            fontWeight = FontWeight.Bold, // 굵은 글씨 유지
            color = if (isSelected) Color.Black else Color.Gray, // 선택된 항목 색상만 변경
            modifier = Modifier.padding(8.dp)
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Divider(
                color = Color.Blue,
                thickness = 4.dp,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 밑줄의 길이를 80%로 설정
            )
        }
        else{
            Spacer(modifier = Modifier.height(4.dp))
            Divider(
                color = Color.White,
                thickness = 4.dp,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // 밑줄의 길이를 80%로 설정
            )
        }
    }
}

// 펀딩 카드 컴포저블 수정 - onClick 추가
@Composable
fun FundingCardComposable(
    title: String,
    location: String,
    startDate: String,
    endDate: String,
    nowAmount: Float,
    goalAmount: Float,
    imageUrl: String?,
    fundingIdx: Int,  // fundingIdx를 전달받음
    onClick: (Int) -> Unit  // 클릭 시 호출될 콜백
) {
    val progress = if (goalAmount > 0) nowAmount / goalAmount else 0f

    // 금액을 3자리마다 쉼표로 구분
    val formattedGoalAmount = NumberFormat.getNumberInstance(Locale.KOREA).format(goalAmount.toInt())

    // 이미지 로드 (null일 경우 기본 이미지 사용)
    val imagePainter = if (imageUrl.isNullOrEmpty()) {
        painterResource(id = R.drawable.hamo)  // res/drawable/hamo.PNG
    } else {
        rememberAsyncImagePainter(imageUrl)
    }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick(fundingIdx) }  // 클릭 시 fundingIdx 전달
    ) {
        // 위치 표시
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Location Icon")
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = location, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // 이미지 로드 및 표시
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize()
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp)),  // 이미지에 둥근 모서리 적용
            contentScale = ContentScale.Crop  // 이미지를 border 안에 꽉 채움
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 날짜 및 제목
        Text(text = "$startDate ~ $endDate", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
        Spacer(modifier = Modifier.height(8.dp))

        // ProgressBar 및 목표 금액
        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "${formattedGoalAmount}원", style = MaterialTheme.typography.bodyMedium, fontSize = 12.sp)
        }
    }
}


// API 데이터 불러오는 함수
fun fetchFundingData(type: String, state: Int, onSuccess: (List<FundingData>) -> Unit) {
    val call = SearchFundingApi.api.searchGovernmentFundings(type, state)
    call.enqueue(object : Callback<FundingResponse> {
        override fun onResponse(call: Call<FundingResponse>, response: Response<FundingResponse>) {
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let { onSuccess(it) }
            }
        }

        override fun onFailure(call: Call<FundingResponse>, t: Throwable) {
            // 실패 처리
        }
    })
}