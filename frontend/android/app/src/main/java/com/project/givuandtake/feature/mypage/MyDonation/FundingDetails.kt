package com.project.givuandtake.feature.mypage.MyDonation

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.project.givuandtake.core.apis.Funding.MyFundingApi
import com.project.givuandtake.core.data.Funding.FundingData
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class MyFundingViewModel : ViewModel() {

    private val _myfundings = mutableStateOf<List<FundingData>>(emptyList())
    val myfundings: State<List<FundingData>> = _myfundings

    fun fetchMyFunding(token: String, category: Int) {
        viewModelScope.launch {
            try {
                val (startDate, endDate) = getDateRangeForCategory(category)

                val response = MyFundingApi.api.getMyFundingData(token, startDate, endDate)
                if (response.isSuccessful) {
                    val myfundings = response.body()?.data
                    myfundings?.let {
                        _myfundings.value = it
                    }
                } else {
                    Log.e("MyFundings", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MyFundings", "Exception: ${e.message}")
            }
        }
    }
}

fun getDateRangeForCategory(category: Int): Pair<String?, String?> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val calendar = Calendar.getInstance()

    return when (category) {
        0 -> { // 최근 6개월
            calendar.add(Calendar.MONTH, -6)
            val startDate = dateFormat.format(calendar.time)
            val endDate = dateFormat.format(Calendar.getInstance().time)
            Pair(startDate, endDate)
        }
        1 -> { // 최근 1년
            calendar.add(Calendar.YEAR, -1)
            val startDate = dateFormat.format(calendar.time)
            val endDate = dateFormat.format(Calendar.getInstance().time)
            Pair(startDate, endDate)
        }
        else -> { // 전체 보기
            Pair(null, null) // 전체 보기일 경우 null 값 전달
        }
    }
}

@Composable
fun CategoryFundingDateBar(
    selectedCategory: Int,
    onCategorySelected: (Int) -> Unit
) {
    val categories = listOf("최근 6개월", "최근 1년", "전체 보기")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEachIndexed { index, category ->
            val isSelected = selectedCategory == index

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFA093DE) else Color(0xFFB3C3F4), label = ""
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) Color(0xFFA093DE) else Color.Transparent, label = ""
            )
            val boxWeight by animateFloatAsState(
                targetValue = if (isSelected) 2f else 1f, label = ""
            )
            val fontSize by animateDpAsState(
                targetValue = if (isSelected) 17.dp else 12.dp, label = ""
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(boxWeight)
                    .height(30.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(backgroundColor)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(25.dp)
                    )
                    .clickable { onCategorySelected(index) }
            ) {
                Text(
                    text = category,
                    color = Color.White,
                    fontSize = fontSize.value.sp,
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

fun calculateTotalFundingFee(fundings: List<FundingData>): Int {
    return fundings.sumOf { it.fundingFee }
}

@Composable
fun DottedLineWithArrow() {
    Canvas(
        modifier = Modifier
            .fillMaxHeight() // Make sure it fills the available height
            .width(2.dp) // Adjust the width of the line
    ) {
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) // Dotted effect
        drawLine(
            color = Color(0xFFA093DE), // Your preferred color
            start = Offset(x = size.width / 2, y = -20f),
            end = Offset(x = size.width / 2, y = size.height + 350f), // End before the arrow
            pathEffect = pathEffect,
            strokeWidth = 4f
        )

        // Draw the arrow at the bottom
        drawArrow(
            color = Color(0xFFA093DE), // Match with line color
            centerX = size.width / 2,
            centerY = size.height + 350f, // Adjust the position
            arrowHeight = 40f // Arrow size
        )
    }
}

fun DrawScope.drawArrow(color: Color, centerX: Float, centerY: Float, arrowHeight: Float) {
    drawLine(
        color = color,
        start = Offset(centerX, centerY),
        end = Offset(centerX - arrowHeight / 2, centerY - arrowHeight),
        strokeWidth = 4f
    )
    drawLine(
        color = color,
        start = Offset(centerX, centerY),
        end = Offset(centerX + arrowHeight / 2, centerY - arrowHeight),
        strokeWidth = 4f
    )
}

@Composable
fun FundingDetails(navController: NavController) {
    var selectedCategory by remember { mutableStateOf(2) }
    var dateRange by remember { mutableStateOf(Pair<String?, String?>(null, null)) }

    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val viewModel: MyFundingViewModel = viewModel()

    LaunchedEffect(selectedCategory) {
        dateRange = getDateRangeForCategory(selectedCategory)
        viewModel.fetchMyFunding(accessToken, selectedCategory)
    }

    val myfundings by viewModel.myfundings
    val totalFundingFee = remember(myfundings) {
        calculateTotalFundingFee(myfundings)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
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
                text = "펀딩내역",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        CategoryFundingDateBar(selectedCategory = selectedCategory, onCategorySelected = {
            selectedCategory = it
        })

        Box(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                .border(
                    width = 4.dp,
                    color = Color(0xFFB3A0F2),
                    shape = RoundedCornerShape(16.dp)
                )
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "펀딩금 총액",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${formatPrice(totalFundingFee)}₩",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 15.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Text(
            text = "${dateRange.first ?: "전체"} ~ ${dateRange.second ?: "전체"}",
            modifier = Modifier.padding(start = 15.dp),
            fontSize = 16.sp,
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            itemsIndexed(myfundings.reversed()) { index, funding ->
                Column() {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(
                                    if (funding.fundingType == "R") Color(0xFFB3C3F4) else Color(
                                        0xFFDAEBFD
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (funding.fundingType == "R") "지역" else "재난", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Box(
                            modifier = Modifier
                                .background(
                                    if (funding.fundingType == "R") Color(0xFFB3C3F4) else Color(
                                        0xFFDAEBFD
                                    ), shape = RoundedCornerShape(20.dp)
                                )
                                .border(2.dp, Color(0xFFDAEBFD), shape = RoundedCornerShape(20.dp))
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = funding.createdDate.substring(0, 10), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(text = "${formatPrice(funding.fundingFee)}₩", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                    ) {
                        Box(
                            modifier = Modifier.fillMaxHeight().width(50.dp), // Adjust the width for the timeline
                            contentAlignment = Alignment.TopCenter
                        ) {
                            if (index != myfundings.size - 1) {
                                DottedLineWithArrow()
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Box(
                            modifier = Modifier
                                .border(2.dp, if (funding.fundingType == "R") Color(0xFFB3C3F4) else Color(0xFFDAEBFD), shape = RoundedCornerShape(8.dp))
                                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                                .background(Color(0xFFFBFAFF))
                                .clip(RoundedCornerShape(20.dp))
                                .fillMaxWidth()
                                .height(120.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Image(
                                    painter = rememberImagePainter(data = funding.fundingThumbnail),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp))                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = funding.fundingTitle,
                                        fontSize = 15.sp,
                                        modifier = Modifier.align(Alignment.TopStart)
                                    )

                                    Text(
                                        text = "후기보기 >",
                                        modifier = Modifier
                                            .clickable { navController.navigate("funding_detail/${funding.fundingIdx}") }
                                            .align(Alignment.BottomEnd),
                                        color = Color.Blue
                                    )
                                }
                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
            }
        }
    }
}