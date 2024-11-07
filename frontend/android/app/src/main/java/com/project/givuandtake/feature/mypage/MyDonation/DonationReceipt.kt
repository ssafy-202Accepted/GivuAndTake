package com.project.givuandtake.feature.mypage.MyDonation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.core.apis.Address.AddressApi
import com.project.givuandtake.core.apis.Address.AddressPostApi
import com.project.givuandtake.core.apis.Receipt.ReceiptApi
import com.project.givuandtake.core.apis.Receipt.ReceiptDonationApi
import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Address.AddressPostData
import com.project.givuandtake.core.data.Address.UserAddress
import com.project.givuandtake.core.data.GiftComment.GiftCommentPostData
import com.project.givuandtake.core.data.Receipt.DonationDetail
import com.project.givuandtake.core.datastore.TokenManager
import com.project.givuandtake.feature.mypage.MyActivities.AddressPostViewModel
import kotlinx.coroutines.launch
import retrofit2.Response

class RecieptPostViewModel : ViewModel() {

    private val _receiptdonations = mutableStateOf<List<DonationDetail>>(emptyList())
    val receiptdonations: State<List<DonationDetail>> = _receiptdonations
    fun postRecieptData(token: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = ReceiptApi.api.postReceiptData("$token")
                if (response.isSuccessful) {
                    Log.e("ReceiptPost", "이메일 발송 성공")
                    Toast.makeText(context, "이메일 발송에 성공하였습니다.", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("ReceiptPost", "이메일 발송 실패: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "이메일 발송에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.e("ReceiptPost", "예외 발생: ${e.message}", e)
                Toast.makeText(context, "네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getReceiptDonationData(token: String) {
        viewModelScope.launch {
            try {
                val response = ReceiptDonationApi.api.receiptDonationData("$token")
                if (response.isSuccessful) {
                    val receiptdonations = response.body()?.data
                    receiptdonations?.let {
                        _receiptdonations.value = it
                    }
                } else {
                    Log.e("ReceiptPost", "도네 발송 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ReceiptPost", "예외 발생: ${e.message}", e)
            }
        }
    }
}

fun calculateDeduction(totalAmount: Int): Int {
    val baseAmount = 100000  // 10만원 이하 금액
    val baseDeduction = baseAmount  // 100% 공제
    val excessAmount = if (totalAmount > baseAmount) totalAmount - baseAmount else 0
    val excessDeduction = (excessAmount * 0.165).toInt()  // 10만원 초과 부분 16.5% 공제

    return baseDeduction + excessDeduction
}

@Composable
fun DonationReceipt(navController: NavController) {
    val viewModel: RecieptPostViewModel = viewModel()
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    LaunchedEffect(Unit) {
        viewModel.getReceiptDonationData(accessToken)
    }

    val receiptdonations by viewModel.receiptdonations
    Log.d("123124234", "$receiptdonations")

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
                text = "기부 영수증",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.padding(20.dp))

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "2024년 연말정산 기부내역",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.padding(vertical = 3.dp))
            Text(
                text = "고향사랑기부제, 펀딩의 금액을 합산하여\n 영수증이 발급됩니다.",
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, Color(0xFFA093DE), RoundedCornerShape(16.dp))
                .background(Color(0xFFFBFAFF))
                .padding(25.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "2024년 기부금 공제금액",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                )
                Text(
                    text = "${formatPrice(calculateDeduction(receiptdonations.sumOf { it.price }))} 원 ",
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    color = Color(0xFFA093DE),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Divider(
            color = Color(0xFFF2F2F2), // Set the line color to gray
            thickness = 15.dp, // Set the thickness of the line
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp) // Optional padding to space it
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 10.dp)
        ) {
            Text(
                text = "올해의 기부금 : ",
                fontSize = 18.sp
            )
            Text(
                text = "${formatPrice(receiptdonations.sumOf { it.price })} 원",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

        }

        Divider(
            color = Color(0xFFF2F2F2), // Set the line color to gray
            thickness = 5.dp, // Set the thickness of the line
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp) // Optional padding to space it
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(receiptdonations) { donation ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp, horizontal = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = donation.name,
                        modifier = Modifier.weight(0.7f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${formatPrice(donation.price)}원",
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 30.dp)
        ) {
            Button(
                onClick = {
                    viewModel.postRecieptData(token = accessToken, context = context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 25.dp)
                    .shadow(4.dp, RoundedCornerShape(25.dp))
                    .height(55.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
            ) {
                Text(text = "기부금 영수증 발송하기", fontSize = 18.sp, color = Color.White)
            }
            Text(
                text = "등록하신 이메일로 발송됩니다.",
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 0.dp, end = 5.dp)
            )
        }
    }
}