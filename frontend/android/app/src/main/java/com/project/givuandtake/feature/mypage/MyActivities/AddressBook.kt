package com.project.givuandtake.feature.mypage.MyActivities

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.Address.AddressApi
import com.project.givuandtake.core.data.Address.AddressData
import com.project.givuandtake.core.data.Address.UserAddress
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.launch
import retrofit2.Response

@Composable
fun AddressItem(address: UserAddress) {
    Column() {
        Row(
            modifier = Modifier
                .padding(start = 15.dp, end = 15.dp, top = 8.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (address.addressName == "우리집") {
                Icon(
                    painter = painterResource(id = R.drawable.house),
                    contentDescription = "Icon",
                    tint = Color(0xFFA093DE),
                    modifier = Modifier.size(24.dp)
                )
            } else if (address.addressName == "회사" ) {
                Icon(
                    painter = painterResource(id = R.drawable.company),
                    contentDescription = "Icon",
                    tint = Color(0xFFA093DE),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.human),
                    contentDescription = "Icon",
                    tint = Color(0xFFA093DE),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column() {
                Row() {
                    Text(text = address.addressName, fontSize = 16.sp)
                    if (address.representative) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                                .background(
                                    Color(0xFFFF6F6F),
                                    shape = RoundedCornerShape(50)
                                ) // 빨간색 배경과 둥근 모서리
                                .padding(horizontal = 20.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "대표 주소지",
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Text(text = "${address.roadAddress} ${address.detailAddress}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = address.jibunAddress, fontSize = 13.sp, color = Color.Gray)
            }
        }
        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}


class AddressViewModel : ViewModel() {

    private val _addresses = mutableStateOf<List<UserAddress>>(emptyList())
    val addresses: State<List<UserAddress>> = _addresses

    fun fetchUserAddresses(token: String) {
        viewModelScope.launch {
            try {
                val response: Response<AddressData> = AddressApi.api.getAddressData("$token")
                if (response.isSuccessful) {
                    val addresses = response.body()?.data
                    addresses?.let {
                        _addresses.value = it
                    }
                } else {
                    Log.e("UserAddresses", "Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserAddresses", "Exception: ${e.message}")
            }
        }
    }
}


@Composable
fun AddressBook(navController: NavController) {
    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    val viewModel: AddressViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.fetchUserAddresses(accessToken)
    }

    val addresses by viewModel.addresses

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
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "주소록",
                textAlign = TextAlign.Center,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "편집",
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier.clickable{ navController.navigate("addressbookupdate")}
            )
        }

        Box(
            modifier = Modifier
                .padding(10.dp)
                .border(
                    border = BorderStroke(2.dp, Color(0xFFA093DE)),
                    shape = RoundedCornerShape(15.dp)
                )
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
                .fillMaxWidth()
                .clickable { navController.navigate("addresssearch") }
        ) {
            Row() {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "Icon",
                    tint = Color(0xFFA093DE),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "주소를 입력하세요",
                    color = Color.Gray,
                    style = MaterialTheme.typography.body1
                )
            }
        }

        Box(
            modifier = Modifier.clickable { navController.navigate("addressmapsearch") }
        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_gps_fixed_24),
                    contentDescription = "Icon",
                    tint = Color(0xFFA093DE),
                    modifier = Modifier.size(24.dp)

                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "현재 위치로 선정",
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_keyboard_arrow_right_24),
                    contentDescription = "Icon",
                    tint = Color(0xFFA093DE),
                    modifier = Modifier.size(24.dp)

                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            color = Color(0xFFF2F2F2),
            thickness = 15.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (addresses.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(addresses) { address ->
                    AddressItem(address)
                }
            }
        } else {
            Text(text = "등록된 주소가 없습니다.", modifier = Modifier.padding(16.dp))
        }
    }
}

//devU01TX0FVVEgyMDI0MDkyNDIzMTE1NTExNTEwODI=