package com.project.givuandtake.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class WebViewActivity : ComponentActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        webView.apply {
            webChromeClient = WebChromeClient()
            webViewClient = WebViewClient()

            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.domStorageEnabled = true

            addJavascriptInterface(JsBridge(), "AndroidBridge")

            // 카카오 주소 검색 API를 호출하는 HTML 파일 로드
            loadUrl("file:///android_asset/kakao_address.html")
        }
    }

    // JavaScript에서 Kotlin으로 데이터를 전달하는 인터페이스
    inner class JsBridge {
        @JavascriptInterface
        fun setAddress(zoneCode: String, roadAddress: String, jibunAddress: String) {
            val intent = Intent().apply {
                putExtra("zoneCode", zoneCode)
                putExtra("roadAddress", roadAddress)
                putExtra("jibunAddress", jibunAddress)
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
