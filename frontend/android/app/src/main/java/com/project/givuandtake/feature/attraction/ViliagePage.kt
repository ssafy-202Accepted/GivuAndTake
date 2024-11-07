package com.project.givuandtake.feature.attraction

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Viliage.ViliageApi
import com.project.givuandtake.core.data.Viliage.ExperienceVillage
import com.project.givuandtake.core.data.Viliage.VillageData
import kotlinx.coroutines.launch
import retrofit2.Response

class SubVillageViewModel : ViewModel() {

    private val _villageCategoryData = mutableStateOf<List<ExperienceVillage>>(emptyList())
    val villageCategoryData: State<List<ExperienceVillage>> = _villageCategoryData

    fun fetchSubVillageData(sido: String, sigungu: String, division: String?, pageNo: Int?, pageSize: Int? ) {
        Log.d("SubVillageViewModel", "Fetching data for division: $division, sido: $sido, sigungu: $sigungu")
        viewModelScope.launch {
            try {
                val response: Response<VillageData> = ViliageApi.api.getExperienceVillage(sido, sigungu, division, pageNo, pageSize)
                if (response.isSuccessful) {
                    Log.d("MainVillageViewModel", "체험 마을 데이터: ${response.body()?.data}")
                    response.body()?.let {
                        Log.d("MainVillageViewModel", "체험 마을 데이터 가져오기 성공: ${it.data}")
                        _villageCategoryData.value = it.data
                    } ?: run {
                        Log.e("MainVillageViewModel", "응답은 성공했으나 데이터가 비어있습니다.")
                    }
                } else {
                    Log.e("MainVillageViewModel", "체험 마을 데이터 가져오기 실패: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MainVillageViewModel", "API 호출 중 예외 발생: ${e.message}", e)
            }
        }
    }
}

@Composable
fun CategorySelection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("전체", "전통문화체험", "만들기체험", "농작물경작체험", "건강", "자연생태체험", "기타")

    Row(modifier = Modifier
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 10.dp)
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category
            Button(
                onClick = { onCategorySelected(category) },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isSelected) Color(0xFFB39DDB) else Color(0xFFB3C3F4),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 12.dp),
                modifier = Modifier
                    .padding(horizontal = 5.dp, vertical = 0.dp)
                    .height(30.dp)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 0.dp)
                )
            }
        }
    }
}

@Composable
fun ViliagePage(navController: NavController, city: String?) {
    var selectedCategory by remember { mutableStateOf("전체") }
    val viewModel: SubVillageViewModel = viewModel()

    val (sido, sigungu) = when (city) {
        "영도" -> Pair("부산광역시", "영도구")
        "군위" -> Pair("대구광역시", "군위군")

        "남원" -> Pair("전북특별자치도", "남원시")
        "무주" -> Pair("전북특별자치도", "무주군")
        "순창" -> Pair("전북특별자치도", "순창군")
        "임실" -> Pair("전북특별자치도", "임실군")

        "고흥" -> Pair("전라남도", "고흥군")
        "보성" -> Pair("전라남도", "보성군")
        "신안" -> Pair("전라남도", "신안군")
        "함평" -> Pair("전라남도", "함평군")

        "고성" -> Pair("경상남도", "고성군")
        "남해" -> Pair("경상남도", "남해군")
        "하동" -> Pair("경상남도", "하동군")
        "합천" -> Pair("경상남도", "합천군")

        "문경" -> Pair("경상북도", "문경시")
        "상주" -> Pair("경상북도", "상주시")
        "안동" -> Pair("경상북도", "안동시")
        "영천" -> Pair("경상북도", "영천시")

        "평창" -> Pair("강원특별자치도", "평창군")
        "횡성" -> Pair("강원특별자치도", "횡성군")
        "태백" -> Pair("강원특별자치도", "태백시")
        "정선" -> Pair("강원특별자치도", "정선군")

        "괴산" -> Pair("충청북도", "괴산군")
        "보은" -> Pair("충청북도", "보은군")
        "영동" -> Pair("충청북도", "영동군")
        "제천" -> Pair("충청북도", "제천시")

        "보령" -> Pair("충청남도", "보령시")
        "부여" -> Pair("충청남도", "부여군")
        "공주" -> Pair("충청남도", "공주시")
        "태안" -> Pair("충청남도", "태안군")

        else -> Pair("기타", "기타")
    }

    LaunchedEffect(sido, sigungu, selectedCategory) {
        Log.d("ViliagePage", "Fetching data for category: $selectedCategory, sido: $sido, sigungu: $sigungu")
        viewModel.fetchSubVillageData(
            sido = sido,
            sigungu = sigungu,
            division = if (selectedCategory=="전체") null else selectedCategory,
            pageNo = null,
            pageSize = 50
        )
    }

    val villageCategoryData by viewModel.villageCategoryData
    Log.d("asdfqewrasdf", "${villageCategoryData.size}")

    Column() {
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
                    .size(32.dp)
                    .clickable { navController.popBackStack() }
            )

            // 텍스트 중앙 배치
            Text(
                text = "체험마을",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f) // 텍스트를 중앙에 배치하기 위해 가중치 설정
                    .padding(start = 16.dp, end = 16.dp),
                fontSize = 23.sp
            )

            Spacer(modifier = Modifier.size(24.dp)) // 빈 공간 유지
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
            val selectedCity = when (city) {
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
                else -> city // Default case for cities not specified
            }
            Image(
                painter = painterResource(id = R.drawable.baseline_location_on_24),
                contentDescription = "map",
                modifier = Modifier
                    .size(25.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("$selectedCity", fontSize = 18.sp)
        }

        CategorySelection(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategory = category // Update selected category
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .heightIn(min = 540.dp)
                .background(
                    color = Color(0xFFB3C3F4),
                    shape = RoundedCornerShape(
                        topStart = 24.dp,
                        topEnd = 24.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                )
                .padding(20.dp)
        ) {
            if (villageCategoryData.isEmpty()) {
                Text(text = "등록된 체험 마을이 없습니다.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Adds space between items
                ) {
                    items(villageCategoryData) { village ->
                        village?.let {
                            VillageCategoryItem(it)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VillageCategoryItem(village: ExperienceVillage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(15.dp)
    ) {
        Text(text = village.experienceVillageName, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = village.experienceVillageAddress, fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(5.dp))
        if ( !village.experienceVillagePhone.isNullOrEmpty() ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_call_24),
                    contentDescription = "map",
                    modifier = Modifier
                        .size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = village.experienceVillagePhone, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        if (!village.experienceVillageHomepageUrl.isNullOrEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_home_24),
                    contentDescription = "map",
                    modifier = Modifier
                        .size(20.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = village.experienceVillageHomepageUrl, fontSize = 16.sp)
            }
        }
        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        FlowRow(
            modifier = Modifier.padding(vertical = 2.dp)
        ) {
            val periods = village.experienceVillageProgram.split("+")
            periods.forEach { period ->
                val displayText = period
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp, bottom = 8.dp)  // 항목 간 간격 설정
                        .background(Color(0xFFA093DE), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = displayText,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
