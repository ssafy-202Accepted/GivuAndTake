package com.project.givuandtake.ui.navbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.givuandtake.R

@Composable
fun BottomNavBar(navController: NavController, selectedItem: Int, onItemSelected: (Int) -> Unit) {
    Box (
        modifier = Modifier.fillMaxWidth()
    ){
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Color(0xFFB3C3F4),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .drawBehind {
                    withTransform({
                        translate(top = 4f)
                    }) {
                        drawLine(
                            color = Color(0xFFB3C3F4), // 원하는 보더 색상
                            strokeWidth = 4f, // 보더 두께
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f)
                        )
                    }
                }
        ) {
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.navgift),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("기부") },
                selected = selectedItem == 0,
                onClick = {
                    onItemSelected(0)
                    navController.navigate("gift")
                }
            )
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.navtrip),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("관광") },
                selected = selectedItem == 1,
                onClick = {
                    onItemSelected(1)
                    navController.navigate("attraction")
                }
            )
            Spacer(modifier = Modifier.width(90.dp)) // 가운데 버튼의 공간을 확보하기 위해 Spacer 추가
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.navfunding),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("펀딩") },
                selected = selectedItem == 3,
                onClick = {
                    onItemSelected(3)
                    navController.navigate("funding")
                }
            )
            BottomNavigationItem(
                icon = {
                    Image(
                        painter = painterResource(id = R.drawable.navmypage),
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = { Text("내정보") },
                selected = selectedItem == 4,
                onClick = {
                    onItemSelected(4)
                    navController.navigate("mypage")
                }
            )
        }

        // 가운데 아이콘이 네비게이션 바를 벗어나도록 설정
        Box(
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.TopCenter) // 중앙에 배치
                .offset(y = (-10).dp) // 네비게이션 바 위로 튀어나오도록 설정
                .background(color = Color(0xFFB3C3F4), shape = CircleShape)
                .clickable {
                    onItemSelected(2) // 선택된 아이템 인덱스를 2로 설정
                    navController.navigate("mainpage") // 메인페이지로 이동
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.navhome),
                contentDescription = null,
                modifier = Modifier.size(40.dp) // 가운데 아이콘 크기 설정
            )
        }
    }
}
