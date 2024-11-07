package com.project.givuandtake.feature.mypage.MyActivities

import android.content.Context
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Card.CardPostApi
import com.project.givuandtake.core.data.Card.CardPostData
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch

class CardPostViewModel : ViewModel() {
    fun postCardData(token: String, cardRequest: CardPostData, context: Context, navController: NavController) {
        viewModelScope.launch {
            try {
                Log.d("AddressPost", "등록 시작: $cardRequest")

                val response = CardPostApi.api.postCardData("$token", cardRequest)

                if (response.isSuccessful) {
                    Log.d("CardPost", "등록 성공")
                    navController.popBackStack() // 첫 번째 뒤로 가기
                    navController.popBackStack() // 두 번째 뒤로 가기
                } else {
                    Log.e("CardPost", "주소 등록 실패: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CardPost", "예외 발생: ${e.message}", e)
            }
        }
    }
}

@Composable
fun CardNumberInputField(
    cardNumber: String,
    onCardNumberChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val cardParts = if (cardNumber.length == 16) {
        listOf(
            cardNumber.substring(0, 4),
            cardNumber.substring(4, 8),
            cardNumber.substring(8, 12),
            cardNumber.substring(12, 16)
        )
    } else {
        listOf("", "", "", "")
    }

    val firstPart = remember { mutableStateOf(cardParts[0]) }
    val secondPart = remember { mutableStateOf(cardParts[1]) }
    val thirdPart = remember { mutableStateOf(cardParts[2]) }
    val fourthPart = remember { mutableStateOf(cardParts[3]) }

    val firstFocusRequester = remember { FocusRequester() }
    val secondFocusRequester = remember { FocusRequester() }
    val thirdFocusRequester = remember { FocusRequester() }
    val fourthFocusRequester = remember { FocusRequester() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicTextField(
            value = firstPart.value,
            onValueChange = { newValue ->
                if (newValue.length <= 4) {
                    firstPart.value = newValue
                    if (newValue.length == 4) {
                        secondFocusRequester.requestFocus()  // 4자리 입력되면 다음 필드로 이동
                    }
                }
                onCardNumberChange(
                    "${firstPart.value}-${secondPart.value}-${thirdPart.value}-${fourthPart.value}"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .width(60.dp)
                .padding(4.dp)
                .drawBehind {
                    // 밑줄 추가
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color(0xFFD6E3FF),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
                .focusRequester(firstFocusRequester),
            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, color = Color.Black),
            singleLine = true
        )

        Text(text = "-", modifier = Modifier.padding(4.dp))

        BasicTextField(
            value = secondPart.value,
            onValueChange = { newValue ->
                if (newValue.length <= 4) {
                    secondPart.value = newValue
                    if (newValue.length == 4) {
                        thirdFocusRequester.requestFocus()  // 4자리 입력되면 다음 필드로 이동
                    }
                }
                onCardNumberChange(
                    "${firstPart.value}-${secondPart.value}-${thirdPart.value}-${fourthPart.value}"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .width(60.dp)
                .padding(4.dp)
                .drawBehind {
                    // 밑줄 추가
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color(0xFFD6E3FF),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
                .focusRequester(secondFocusRequester),
            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, color = Color.Black),
            singleLine = true
        )

        Text(text = "-", modifier = Modifier.padding(4.dp))

        BasicTextField(
            value = thirdPart.value,
            onValueChange = { newValue ->
                if (newValue.length <= 4) {
                    thirdPart.value = newValue
                    if (newValue.length == 4) {
                        fourthFocusRequester.requestFocus()  // 4자리 입력되면 다음 필드로 이동
                    }
                }
                onCardNumberChange(
                    "${firstPart.value}-${secondPart.value}-${thirdPart.value}-${fourthPart.value}"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .width(60.dp)
                .padding(4.dp)
                .drawBehind {
                    // 밑줄 추가
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color(0xFFD6E3FF),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
                .focusRequester(thirdFocusRequester),
            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, color = Color.Black),
            singleLine = true
        )

        Text(text = "-", modifier = Modifier.padding(4.dp))

        BasicTextField(
            value = fourthPart.value,
            onValueChange = { newValue ->
                if (newValue.length <= 4) {
                    fourthPart.value = newValue
                    // 마지막 필드이므로 추가 동작 필요 없음
                }
                onCardNumberChange(
                    "${firstPart.value}-${secondPart.value}-${thirdPart.value}-${fourthPart.value}"
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .width(60.dp)
                .padding(4.dp)
                .drawBehind {
                    // 밑줄 추가
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(
                        color = Color(0xFFD6E3FF),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
                .focusRequester(fourthFocusRequester),
            textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, color = Color.Black),
            singleLine = true
        )
    }
}

data class Bank(val name: String, val logoResId: Int)

@Composable
fun BankSelectionScreen(selectedBank: String, onBankSelected: (Bank) -> Unit) {
    val bankList = listOf(
        Bank("IBK기업은행", R.drawable.ibkbank),
        Bank("수협은행", R.drawable.seabank),
        Bank("NH농협", R.drawable.nhbank),
        Bank("국민은행", R.drawable.kbbank),
        Bank("신한은행", R.drawable.shinhanbank),
        Bank("우리은행", R.drawable.webank),
        Bank("하나은행", R.drawable.onebank),
        Bank("부산은행", R.drawable.busanbank),
        Bank("경남은행", R.drawable.gyeongnambank),
        Bank("대구은행", R.drawable.daegubank),
        Bank("광주은행", R.drawable.gwangjubank),
        Bank("전북은행", R.drawable.junbukbank),
        Bank("제주은행", R.drawable.jejubank),
        Bank("SC제일은행", R.drawable.scbank),
        Bank("씨티은행", R.drawable.citybank)
    )

    var selectedBankItem by remember { mutableStateOf<Bank?>(null) }

    if (selectedBankItem == null) {
        // 선택된 은행이 없을 때 은행 목록을 표시
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(bankList.size) { index ->
                val bank = bankList[index]
                BankItem(bank = bank, onBankSelected = { selectedBankItem = bank; onBankSelected(bank) })
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { selectedBankItem = null },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = selectedBankItem!!.logoResId,
                contentDescription = selectedBankItem!!.name,
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = selectedBankItem!!.name, fontSize = 16.sp, color = Color.Black)
        }
    }
}

@Composable
fun BankItem(bank: Bank, onBankSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .size(100.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(
                border = BorderStroke(2.dp, Color(0x50A093DE)),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable { onBankSelected(bank.name) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = bank.logoResId,
            contentDescription = bank.name,
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = bank.name, fontSize = 14.sp, color = Color.Black)
    }
}

@Composable
fun CardCustomRegistration(cardNumber: String, validThru: String, navController: NavController) {
    val validThruParts = remember {
        if (validThru.length == 5 && validThru.contains("/")) {
            validThru.split("/")
        } else {
            listOf("", "")
        }
    }
    var cardNumber by remember { mutableStateOf(cardNumber) }
    val formattedCardNumber = cardNumber.chunked(4).joinToString("-")

    var expiryMonth by remember { mutableStateOf(validThruParts[0]) }
    var expiryYear by remember { mutableStateOf(validThruParts[1]) }

    var cvcCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var selectedBank by remember { mutableStateOf<Bank?>(null) }
    Log.d("12adasdf34", "${cardNumber}")

    val viewModel: CardPostViewModel = viewModel()
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"
    val scope = rememberCoroutineScope()

    val cardRequest =  CardPostData(
        cardCompany = "${selectedBank?.name}",
        cardNumber = formattedCardNumber,
        cardCVC = cvcCode,
        cardExpiredDate = "20$expiryYear-$expiryMonth-28",
        cardPassword = password,
        isRepresentative = true,
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                text = "카드등록",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Column (
            modifier = Modifier.padding(15.dp)
        ){
            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "카드 회사", fontSize = 18.sp, color = Color.Gray, modifier = Modifier.padding(bottom=5.dp))
            BankSelectionScreen(
                selectedBank = selectedBank?.name ?: "", // 이름만 표시
                onBankSelected = { bank ->
                    selectedBank = bank // Bank 객체 전체를 저장
                }
            )

            Log.d("bank", "$selectedBank")

            if (selectedBank != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "카드 번호",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                CardNumberInputField(
                    cardNumber = cardNumber,
                    onCardNumberChange = { newCardNumber ->
                        cardNumber = newCardNumber
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "만료일",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    BasicTextField(
                        value = expiryMonth,
                        onValueChange = { newValue ->
                            if (newValue.length <= 2) {
                                expiryMonth = newValue
                            }
                        },
                        modifier = Modifier
                            .width(30.dp)
                            .padding(bottom = 4.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            color = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        val y = size.height - strokeWidth / 2
                                        drawLine(
                                            color = Color(0xFFD6E3FF),
                                            start = androidx.compose.ui.geometry.Offset(0f, y),
                                            end = androidx.compose.ui.geometry.Offset(
                                                size.width,
                                                y
                                            ),
                                            strokeWidth = strokeWidth
                                        )
                                    },
                            ) {
                                if (expiryMonth.isEmpty()) {
                                    Text(
                                        text = "MM",
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )


                    Spacer(modifier = Modifier.width(3.dp))
                    Text("/")
                    Spacer(modifier = Modifier.width(3.dp))

                    BasicTextField(
                        value = expiryYear,
                        onValueChange = { newValue ->
                            if (newValue.length <= 2) {
                                expiryYear = newValue
                            }
                        },
                        modifier = Modifier
                            .width(30.dp)
                            .padding(bottom = 4.dp),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp,
                            color = Color.Black
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.NumberPassword // 숫자 패드만 표시
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        val y = size.height - strokeWidth / 2
                                        drawLine(
                                            color = Color(0xFFD6E3FF),
                                            start = androidx.compose.ui.geometry.Offset(0f, y),
                                            end = androidx.compose.ui.geometry.Offset(
                                                size.width,
                                                y
                                            ),
                                            strokeWidth = strokeWidth
                                        )
                                    },
                            ) {
                                if (expiryYear.isEmpty()) {
                                    Text(
                                        text = "YY",
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 보안코드 입력
                Text(
                    text = "보안코드(CVC/CVV)",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                BasicTextField(
                    value = cvcCode,
                    onValueChange = { newValue ->
                        if (newValue.length <= 3) {
                            cvcCode = newValue
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .width(40.dp), // 하단 선과의 간격
                    textStyle = LocalTextStyle.current.copy( // 기본 텍스트 스타일 적용
                        fontSize = 20.sp, // 글씨 크기
                        color = Color.Black
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword // 숫자 패드만 표시
                    ),
                    singleLine = true,  // 한 줄 입력
                    decorationBox = { innerTextField ->
                        Column {
                            innerTextField()  // 실제 입력 필드
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFD6E3FF)) // 하단 선 색상
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 입력
                Text(
                    text = "비밀번호",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                BasicTextField(
                    value = password,
                    onValueChange = { newValue ->
                        if (newValue.length <= 4) { // 4자리까지만 입력 허용
                            password = newValue
                        }
                    },
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .width(40.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 20.sp,
                        color = Color.Black
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.NumberPassword // 숫자 패드만 표시
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Column {
                            innerTextField()  // 실제 입력 필드
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color(0xFFD6E3FF)) // 하단 선 색상
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        val y = size.height - strokeWidth / 2
                                        drawLine(
                                            color = Color(0xFFD6E3FF),
                                            start = androidx.compose.ui.geometry.Offset(0f, y),
                                            end = androidx.compose.ui.geometry.Offset(
                                                size.width,
                                                y
                                            ),
                                            strokeWidth = strokeWidth
                                        )
                                    },
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // 카드 등록 버튼
                Button(
                    onClick = {
                        Log.d("134qer134qewr", "$cardRequest")
                        scope.launch {
                            viewModel.postCardData(accessToken, cardRequest, context, navController)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
                ) {
                    Text(text = "카드 등록하기", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}