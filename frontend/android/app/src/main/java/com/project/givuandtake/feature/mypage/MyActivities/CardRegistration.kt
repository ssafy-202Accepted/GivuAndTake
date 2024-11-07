package com.project.givuandtake.feature.mypage.MyActivities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.project.givuandtake.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File

var imageCapture: ImageCapture? = null
fun cropToCardArea(bitmap: Bitmap, boxLeft: Int, boxTop: Int, boxWidth: Int, boxHeight: Int): Bitmap? {
    Log.d("BoxSize", "Box Width: $boxWidth, Box Height: $boxHeight")
    return try {
        Bitmap.createBitmap(bitmap, boxLeft, boxTop, boxWidth, boxHeight)
    } catch (e: Exception) {
        Log.e("cropToCardArea", "Error while cropping image: ${e.message}")
        null
    }
}


fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(format, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}

fun sendImageToApi(bitmap: Bitmap, imageFormat: String, onResponse: (String) -> Unit, onError: (Exception) -> Unit) {
    val client = OkHttpClient()

    val compressFormat = when (imageFormat.lowercase()) {
        "png" -> Bitmap.CompressFormat.PNG
        "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
        else -> throw IllegalArgumentException("Unsupported image format: $imageFormat")
    }
    val base64Image = bitmapToBase64(bitmap, compressFormat)

    val jsonBody = JSONObject().apply {
        put("version", "V2")
        put("requestId", "test")
        put("timestamp", "0")
        put("images", JSONArray().apply {
            put(JSONObject().apply {
                put("format", imageFormat)
                put("data", base64Image)
                put("name", "Starting JSON creationt")
            })
        })
    }

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val body = RequestBody.create(mediaType, jsonBody.toString())

    val request = Request.Builder()
        .url("https://nolz8iywzq.apigw.ntruss.com/custom/v1/34650/4e936747d4186eba0ef498ce75b20fec58d668ff4a682b3435185019c6558c75/document/credit-card")
        .post(body)
        .addHeader("Content-Type", "application/json")
        .addHeader("X-OCR-SECRET", "V0xwdUhOY1p5RUdYaFRqV2pYT0hFc1FWaGhtRU1wTUM=") // 여기에 실제 OCR SECRET 값을 넣어야 함
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("API_ERROR", "Request failed: ${e.message}", e)
                    onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseString = response.body?.string()
                    Log.d("API_RESPONSE", "Response: $responseString")

                    if (response.isSuccessful) {
                        responseString?.let { onResponse(it) }
                    } else {
                        Log.e("API_ERROR", "Request failed with status code: ${response.code}")
                        onError(Exception("Request failed with status code: ${response.code}"))
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error: ${e.message}", e)
            onError(e)
        }
    }
}

fun capturePhoto(context: Context, onImageCaptured: (Bitmap) -> Unit) {
    val photoFile = File(context.filesDir, "card_image.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture?.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                onImageCaptured(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Image capture failed", exception)
            }
        }
    )
}


@Composable
fun RequestCameraPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
            if (granted) {
                onPermissionGranted()
            }
        }
    )

    LaunchedEffect(hasPermission) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        } else {
            onPermissionGranted()
        }
    }
}


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onCameraControlReady: (androidx.camera.core.CameraControl) -> Unit,
    onPreviewViewSizeReady: (Size) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProvider = cameraProviderFuture.get()
    }

    cameraProvider?.let { provider ->
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                imageCapture = ImageCapture.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build()

                try {
                    provider.unbindAll()
                    val camera = provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                    onCameraControlReady(camera.cameraControl)
                } catch (e: Exception) {
                    Log.e("CameraX", "Use case binding failed", e)
                }

                previewView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    val previewWidth = previewView.width.toFloat()
                    val previewHeight = previewView.height.toFloat()
                    if (previewWidth > 0 && previewHeight > 0) {
                        onPreviewViewSizeReady(Size(previewWidth, previewHeight))
                    }
                }

                previewView
            },
            modifier = modifier.fillMaxSize()
        )
    }
}

fun rotateImageBy90Degrees(bitmap: Bitmap): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(90f)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun parseCreditCardResponse(response: String): Pair<String, String> {
    val jsonObject = JSONObject(response)
    val imagesArray = jsonObject.getJSONArray("images")
    val creditCardObject = imagesArray.getJSONObject(0).getJSONObject("creditCard").getJSONObject("result")
    val cardNumber = creditCardObject.getJSONObject("number").getString("text")
    val validThru = creditCardObject.getJSONObject("validThru").getString("text")
    return Pair(cardNumber, validThru)
}

@Composable
fun CardRegistration(navController: NavController) {
    var hasCameraPermission by remember { mutableStateOf<Boolean?>(null) }
    var isFlashOn by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var croppedImage by remember { mutableStateOf<Bitmap?>(null) }
    var cardNumber by remember { mutableStateOf<String?>(null) }
    var validThru by remember { mutableStateOf<String?>("")}
    var apiResponse by remember { mutableStateOf<String?>(null) }
    var selectedFormat by remember { mutableStateOf("png") }

    var previewViewSize by remember { mutableStateOf(Size.Zero) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            hasCameraPermission = true
        } else {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    var boxSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    var boxPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

    when (hasCameraPermission) {
        null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
        true -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onCameraControlReady = { control ->
                        cameraControl = control
                    },
                    onPreviewViewSizeReady = { size ->
                        previewViewSize = size
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { navController.popBackStack() }
                    )
                    Spacer(modifier = Modifier.height(150.dp))

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 300.dp, height = 180.dp)
                                .border(
                                    BorderStroke(
                                        3.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFFA093DE), Color.White),
                                            tileMode = TileMode.Repeated
                                        )
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(Color.Transparent)
                                .onGloballyPositioned { coordinates ->
                                    boxSize = coordinates.size.toSize()
                                    boxPosition = coordinates.positionInRoot()
                                }
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "사각형 안에 카드를 맞추고 촬영해주세요",
                            fontSize = 16.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(50.dp))
                        Button(
                            onClick = {
                                try {
                                    capturePhoto(context) { bitmap ->
                                        val rotatedBitmap = rotateImageBy90Degrees(bitmap)
                                        capturedImage = rotatedBitmap  // 90도 회전된 이미지를 저장

                                        if (previewViewSize != Size.Zero) {
                                            val scaleX = capturedImage!!.width.toFloat() / previewViewSize.width
                                            val scaleY = capturedImage!!.height.toFloat() / previewViewSize.height

                                            val boxLeftPx = (boxPosition.x * scaleX).toInt()
                                            val boxTopPx = (boxPosition.y * scaleY).toInt()
                                            val boxWidthPx = (boxSize.width * scaleX).toInt()
                                            val boxHeightPx = (boxSize.height * scaleY).toInt()

                                            if (boxLeftPx + boxWidthPx <= capturedImage!!.width && boxTopPx + boxHeightPx <= capturedImage!!.height) {
                                                croppedImage = Bitmap.createBitmap(capturedImage!!, boxLeftPx, boxTopPx, boxWidthPx, boxHeightPx)
                                            } else {
                                                Log.e("CropError", "Cropping area exceeds the captured image bounds")
                                            }
                                        } else {
                                            Log.d("previewsize", "$previewViewSize.width" +
                                                    "$previewViewSize.height")
                                            Log.e("PreviewSizeError", "PreviewView size is not ready")
                                        }


                                        croppedImage?.let { croppedBitmap ->

                                            sendImageToApi(croppedBitmap, selectedFormat,
                                                onResponse = { response ->
                                                    apiResponse = response  // API 응답 저장
                                                    try {
                                                        val (number, validThruDate) = parseCreditCardResponse(response)
                                                        cardNumber = number ?: ""
                                                        validThru = Uri.encode(validThruDate) ?: ""
                                                        Log.d("adfadf", "$cardNumber")
                                                        CoroutineScope(Dispatchers.Main).launch {
                                                            navController.navigate("cardcustomregistration/${cardNumber}/${validThru}")
                                                        }
                                                    } catch (e: Exception) {
                                                        // 예외가 발생하면 빈 문자열로 처리
                                                        cardNumber = ""
                                                        validThru = ""
                                                        Log.e("ParseError", "Failed to parse credit card response: ${e.message}")
                                                        CoroutineScope(Dispatchers.Main).launch {
                                                            navController.navigate("cardcustomregistration//")
                                                        }
                                                    }
                                                },
                                                onError = { error ->
                                                    Log.e("API_ERROR", "Error: ${error.message}", error)
                                                })
                                        }
//                                        navController.navigate("cardcustomregistration/${cardNumber}/${validThru}")
                                    }
                                } catch (e: Exception) {
                                    // 예외가 발생하면 앱이 종료되지 않도록 로그만 남기고 무시
                                    Log.e("CaptureError", "Error while capturing photo: ${e.message}", e)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD6D6D6)),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_camera_24),
                                contentDescription = "Toggle Flash",
                                tint = if (isFlashOn) Color.Yellow else Color(0xFFA093DE),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(50.dp))

                        val excardnnum = ""
                        val exvalid = Uri.encode("")

                        Button(
                            onClick = { navController.navigate("cardcustomregistration/${excardnnum}/${exvalid}") },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFA093DE)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 64.dp)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "수동으로 카드 입력",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(150.dp))

                        Button(
                            onClick = {
                                isFlashOn = !isFlashOn
                                cameraControl?.enableTorch(isFlashOn) // 플래시 토글
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD6D6D6)),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_flashlight_on_24),
                                contentDescription = "Toggle Flash",
                                tint = if (isFlashOn) Color.Yellow else Color(0xFFA093DE),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
        false -> {
            navController.navigate("cardcustomregistration//")
        }
    }
}


