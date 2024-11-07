package com.project.givuandtake.feature.gift

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.ui.theme.gmarketSans

@Composable
fun RelatedRecommendations(navController: NavController, location: String) {
    Log.d("location","location : ${location}")
    val (description, imageRes) = when (location) {
        "전북특별자치도 임실군" -> Pair(
            "전라북도 임실군은 치즈의 고장으로 널리 알려진 지역입니다. 임실 치즈 테마파크는 다양한 체험 프로그램을 제공하며 가족 단위 관광객들에게 인기가 많습니다. 또한, 섬진강 자전거길과 자연경관도 유명합니다.",
            R.drawable.p_imsil // 임실 이미지
        )
        "전라남도 보성군" -> Pair(
            "전라남도 보성군은 녹차로 유명한 지역입니다. 보성 녹차밭과 대한다원의 풍경은 많은 이들에게 힐링의 장소로 사랑받고 있습니다. 또한, 보성차밭축제와 율포 해수욕장도 많은 인기를 끌고 있습니다.",
            R.drawable.p_boseong // 보성 이미지
        )
        "경상남도 하동군" -> Pair(
            "경상남도 하동군은 지리산과 섬진강을 품은 자연경관이 아름다운 곳입니다. 섬진강을 따라 펼쳐진 화개장터와 하동 야생차문화축제 등이 유명합니다.",
            R.drawable.p_hadong // 하동 이미지
        )
        "경상북도 문경시" -> Pair(
            "경상북도 문경시는 문경새재로 유명한 도시입니다. 문경새재와 함께 전통 도자기와 오미자가 유명하며, 문경 오미자 축제는 많은 관광객을 불러모읍니다.",
            R.drawable.p_mungyeong // 문경 이미지
        )
        "강원특별자치도 태백시" -> Pair(
            "강원도 태백시는 태백산을 중심으로 한 자연경관이 자랑입니다. 겨울철 태백산 눈축제와 태백 석탄박물관이 주요 명소입니다.",
            R.drawable.p_taebaek // 태백 이미지
        )
        "충청북도 보은군" -> Pair(
            "충청북도 보은군은 속리산국립공원과 법주사가 유명합니다. 대추로도 유명한 보은은 매년 대추 축제가 열려 많은 관광객들이 방문합니다.",
            R.drawable.p_boeun // 보은 이미지
        )
        "충청남도 보령시" -> Pair(
            "충청남도 보령시는 대천해수욕장과 머드축제가 유명한 서해안의 대표적인 관광지입니다.",
            R.drawable.p_boryeong // 보령 이미지
        )
        else -> Pair(
            "전라북도 임실군은 치즈의 고장으로 널리 알려진 지역입니다. 임실 치즈 테마파크는 다양한 체험 프로그램을 제공하며 가족 단위 관광객들에게 인기가 많습니다. 또한, 섬진강 자전거길과 자연경관도 유명합니다.",
            R.drawable.p_imsil // 디폴트 임실 이미지
        )
    }

    Column(modifier = Modifier.padding(8.dp)) {
        // 위치 텍스트
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_location_on_24),
                contentDescription = "Location icon"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = location,
                fontSize = 14.sp,
                fontFamily = gmarketSans
            )
        }

        // 이미지를 박스 안에 꽉 채워서 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(8.dp)
                .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .background(Color.White) // 배경색 추가
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Map image",
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        // 설명 부분을 카드로 따로 배치
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 4.dp,
            backgroundColor = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = description,
                    fontFamily = gmarketSans,
                    fontSize = 18.sp, // 폰트 크기 키움
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // 텍스트 양옆 여백 추가
                    color = Color.Black
                )
            }
        }

        // 버튼을 카드 밑에 배치
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val shortLocation = location.takeLast(3).substring(0, 2)
                navController.navigate("attraction?city=$shortLocation")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "+ 주변 관광지",
                fontFamily = gmarketSans
            )
        }
    }
}
