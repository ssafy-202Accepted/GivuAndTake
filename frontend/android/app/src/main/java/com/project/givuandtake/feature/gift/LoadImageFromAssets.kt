package com.project.givuandtake.feature.gift.mainpage

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import java.io.InputStream

@Composable
fun LoadImageFromAssets(fileName: String) {
    val context = LocalContext.current
    val image = remember { loadImageFromAssets(context, fileName) }

    image?.let {
        Image(bitmap = it, contentDescription = null, modifier = Modifier.fillMaxSize())
    }
}

fun loadImageFromAssets(context: Context, fileName: String): ImageBitmap? {
    return try {
        val inputStream: InputStream = context.assets.open(fileName)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
