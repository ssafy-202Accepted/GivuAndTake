package com.project.givuandtake.feature.mypage.MyActivities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun editableTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(6.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp)),
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = Color.Black
            ),
            enabled = true
        )
    }
}


@Composable
fun displayTextField(label: String, value: String) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
        )

        Text(
            text = value,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 24.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Transparent)
                .height(36.dp) // 입력 필드 높이 수정

        )
    }
}