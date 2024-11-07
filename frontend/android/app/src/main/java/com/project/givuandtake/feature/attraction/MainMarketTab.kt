package com.project.givuandtake.feature.attraction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.MarketRetrofitInstance
import com.project.givuandtake.core.data.MarketProperties
import com.project.givuandtake.core.data.TraditionalMarketData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SquareMarketItem(
    marketName: String,
    address: String,
    parkingAvailable: Boolean,
    restroomAvailable: Boolean
) {
    Box(
        modifier = Modifier
            .size(180.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = address,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 8.dp, bottom=0.dp)
                    .weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
//                modifier = Modifier.padding(vertical = 1.dp)
            ) {
                if (parkingAvailable) {
                    Image(
                        painter = painterResource(id = R.drawable.parkingo),
                        contentDescription = "Parking available",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.parkingx),
                        contentDescription = "Parking available",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (restroomAvailable) {
                    Image(
                        painter = painterResource(id = R.drawable.toileto),
                        contentDescription = "Restroom available",
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.toiletx),
                        contentDescription = "Restroom available",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Text(
                text = marketName,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .weight(2f)
            )
        }
    }
}

@Composable
fun MarketItem(
    marketName: String,
    address: String,
    parkingAvailable: Boolean,
    restroomAvailable: Boolean,
    openPeriod: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .padding(19.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = address, fontSize = 12.sp, color = Color.Gray)
                Text(text = marketName, fontSize = 25.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold,)
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val periods = openPeriod.split("+")
                    periods.forEach { period ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFA093DE), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = period,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Column{
                if (parkingAvailable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.parkingo),
                        contentDescription = "Parking available",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.parkingx),
                        contentDescription = "Parking available",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (restroomAvailable) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.toileto),
                        contentDescription = "Restroom available",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.toiletx),
                        contentDescription = "Restroom available",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

fun getMarketData(displayedCity:String, onResult: (List<MarketProperties>) -> Unit) {
    val apiKey = "F5ED5493-0442-3CE8-81F3-0B307BFF22B4"
    val request = "GetFeature"
    val data = "LT_P_TRADSIJANG"
    val geomFilter = "BOX(124.60,33.00,131.87,38.63)"
    val attrFilter = "adr_road:like:$displayedCity"

    MarketRetrofitInstance.api.getMarketData(request, apiKey, data, geomFilter, attrFilter).enqueue(object : Callback<TraditionalMarketData> {
        override fun onResponse(call: Call<TraditionalMarketData>, response: Response<TraditionalMarketData>) {
            if (response.isSuccessful) {
                val marketPropertiesList = response.body()?.response?.result?.featureCollection?.features?.map {
                    it.properties
                } ?: emptyList()
                onResult(marketPropertiesList)
            } else {
                Log.e("Vworld", "Error: ${response.code()}")
                onResult(emptyList())
            }
        }

        override fun onFailure(call: Call<TraditionalMarketData>, t: Throwable) {
            Log.e("Vworld", "Failed to get market data", t)
            onResult(emptyList())
        }
    })
}
@Composable
fun MainMarketTab(displayedCity: String) {
    var marketProperties by remember { mutableStateOf(listOf<MarketProperties>()) }

    LaunchedEffect(Unit) {
        getMarketData(displayedCity) { properties ->
            marketProperties = properties
        }
    }


    Text(
        text = "우리 고향 상설 전통시장",
        fontSize = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        val filteredMarkets = marketProperties.filter { it.opn_per == "매일" }

        if (filteredMarkets.isEmpty()) {
            item {
                Text(text = "상설 전통시장이 열리지 않아요 😥", fontSize = 18.sp, color = Color.Red, modifier = Modifier.padding(10.dp))
            }
        } else {
            items(filteredMarkets) { market ->
                SquareMarketItem(
                    marketName = market.name,
                    address = market.adr_road,
                    parkingAvailable = market.park == "Y",
                    restroomAvailable = market.toilet == "Y",
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "우리 고향 정기 전통시장",
        fontSize = 20.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    Column(modifier = Modifier.fillMaxSize()) {
        val filteredMarkets = marketProperties.filter { it.opn_per != "매일" }

        when {
            filteredMarkets.isEmpty() -> {
                Text(text = "정기 전통시장이 열리지 않아요 😥", fontSize = 18.sp, color = Color.Red, modifier = Modifier.padding(10.dp))
            }
            else -> {
                filteredMarkets.take(3).forEach { market ->
                    MarketItem(
                        marketName = market.name,
                        address = market.adr_road,
                        parkingAvailable = market.park == "Y",
                        restroomAvailable = market.toilet == "Y",
                        openPeriod = market.opn_per
                    )
                    Spacer(modifier = Modifier.height(8.dp)) // 리스트 간격 추가
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}