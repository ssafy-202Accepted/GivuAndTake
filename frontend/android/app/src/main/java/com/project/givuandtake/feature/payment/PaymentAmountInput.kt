package com.project.givuandtake.feature.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AmountInputField(
    inputText: String,
    onInputChange: (String) -> Unit,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (isFocused) Color.Black else Color.Gray)
    ) {
        BasicTextField(
            value = inputText,
            onValueChange = { onInputChange(it) },
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                color = if (inputText.isNotEmpty() || isFocused) Color.Black else Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Start
            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    if (inputText.isEmpty() && !isFocused) {
                        Text("금액을 직접 입력해주세요", color = Color.Gray)
                    }
                    innerTextField() // 실제 텍스트 필드 표시
                }
            },
            cursorBrush = SolidColor(Color.Black), // 커서 색상 설정
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                }
        )
    }
}

@Composable
fun AmountButtonsRow(onAmountAdd: (Int) -> Unit) {
    // 금액 입력 버튼들을 별도의 Composable로 분리
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AmountButton(text = "+5천", modifier = Modifier.weight(1f)) {
            onAmountAdd(5000)
        }
        AmountButton(text = "+1만", modifier = Modifier.weight(1f)) {
            onAmountAdd(10000)
        }
        AmountButton(text = "+5만", modifier = Modifier.weight(1f)) {
            onAmountAdd(50000)
        }
        AmountButton(text = "+10만", modifier = Modifier.weight(1f)) {
            onAmountAdd(100000)
        }
    }
}

@Composable
fun AmountButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text(text = text)
    }
}