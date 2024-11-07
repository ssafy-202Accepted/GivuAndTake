package com.project.givuandtake.feature.mypage.MyDonation

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import com.project.givuandtake.R
import com.project.givuandtake.core.apis.GiftComment.GiftCommentPostApi
import com.project.givuandtake.core.data.Gift.GiftData
import com.project.givuandtake.core.data.GiftComment.GiftCommentPostData
import com.project.givuandtake.core.datastore.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class WriteGiftReviewViewModel : ViewModel() {

    fun postGiftReview(
        context: Context,
        navController: NavController,
        giftIdx: Int,
        token: String,
        giftCommentData: GiftCommentPostData,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                Log.d("WriteGiftReview", "API 호출 시작 - 리뷰 작성")
                Log.d("WriteGiftReview", "리뷰 데이터: ${Gson().toJson(giftCommentData)}")

                // 이미지가 선택되지 않은 경우 null을 보냄
                val imagePart = imageUri?.let { uri ->
                    getFilePartFromUri(context, uri, "reviewImage")
                }
                Log.d("WriteGiftReview", "이미지 파일: $imagePart")

                // API 호출
                val response = GiftCommentPostApi.postGiftCommentWithImage(
                    token,
                    giftCommentData,
                    imagePart // null일 경우 그대로 null로 전달
                )

                if (response.isSuccessful) {
                    Log.d("WriteGiftReview", "API 호출 성공 - 응답 코드: ${response.code()}")
                    Log.d("adfadf", "$giftIdx")
                    withContext(Dispatchers.Main) {
                        navController.navigate("mypage") // 메인 스레드에서 네비게이션 수행
                    }
                } else {
                    Log.d("WriteGiftReview", "API 호출 실패 - 응답 코드: ${response.code()}, 메시지: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("WriteGiftReview", "API 호출 중 오류 발생: ${e.message}")
            }
        }
    }

    private fun getFilePartFromUri(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val fileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
            val file = File(context.cacheDir, getFileName(context, uri))
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            MultipartBody.Part.createFormData(partName, file.name, requestFile)
        } catch (e: Exception) {
            Log.e("WriteGiftReview", "파일 변환 중 오류 발생: ${e.message}")
            null
        }
    }

    // Uri에서 파일 이름 추출
    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "unknown_file"
    }
}

@Composable
fun WriteGiftReview(navController: NavController, navBackStackEntry: NavBackStackEntry) {
    val gson = remember { Gson() }
    val viewModel: WriteGiftReviewViewModel = viewModel()


    // 전달받은 gift JSON 데이터를 객체로 변환
    val giftJson = navBackStackEntry.arguments?.getString("gift")
    val gift: GiftData? = gson.fromJson(giftJson, GiftData::class.java)

    val giftreviewcontent = remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    Log.d("asdfasdf", "$selectedImageUri")

    val context = LocalContext.current
    val accessToken = "Bearer ${TokenManager.getAccessToken(context)}"

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri // 선택된 이미지 Uri 저장
    }

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
                    .weight(0.3f)
            )

            Spacer(modifier = Modifier.weight(0.7f))

            Text(
                text = "후기 쓰기",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                ,fontSize = 20.sp
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Box(
            modifier = Modifier
                .padding(20.dp)
                .border(2.dp,Color(0xFFA093DE), shape = RoundedCornerShape(20.dp))
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                .background(Color(0xFFFBFAFF))
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(data = gift?.giftThumbnail),
                    contentDescription = null,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column() {
                    Text(
                        text = gift!!.giftName.replace("+", " "),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.location),
                            contentDescription = "Icon",
                            tint = Color(0xFFA093DE),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(text = gift.regionName, fontSize = 16.sp, color = Color(0xFF8368DC))
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${formatPrice(gift!!.price)} ₩",
                        fontSize = 16.sp,
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .border(2.dp,Color(0xFFB3C3F4), shape = RoundedCornerShape(20.dp))
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                .background(Color(0xFFFBFAFF))
                .clip(RoundedCornerShape(20.dp))
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (selectedImageUri.value != null) {
                // 선택된 이미지가 있을 때
                Image(
                    painter = rememberImagePainter(data = selectedImageUri.value),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 선택된 이미지가 없을 때
                Row(
                    modifier = Modifier.clickable {
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_camera_alt_24),
                        contentDescription = "Icon",
                        tint = Color(0xFFA093DE),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이에 간격 추가

                    Text(
                        text = "사진 첨부"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = giftreviewcontent.value,
            onValueChange = { newValue ->
                if (newValue.length <= 6000) {
                    giftreviewcontent.value = newValue
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(horizontal = 20.dp)
                .border(2.dp,Color(0xFFB3C3F4), shape = RoundedCornerShape(20.dp))
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFFBFAFF)),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFB3C3F4),
                unfocusedBorderColor = Color(0xFFB3C3F4)
            ),
        )
        Text(
            text = "${giftreviewcontent.value.length}/6000",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End).padding(end = 30.dp, top = 10.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 15.dp, end = 15.dp, bottom = 30.dp)
        ) {
            Button(
                onClick = {
                    gift?.let {
                        val giftCommentData = GiftCommentPostData(
                            reviewContent = giftreviewcontent.value,
                            giftIdx = it.giftIdx,
                            orderIdx = it.orderIdx
                        )
                        viewModel.postGiftReview(
                            context = context,
                            navController,
                            giftIdx = gift.giftIdx,
                            token = accessToken,
                            giftCommentData = giftCommentData,
                            imageUri = selectedImageUri.value
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(25.dp))
                    .height(55.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFB3C3F4))
            ) {
                Text(text = "후기 작성 완료", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}