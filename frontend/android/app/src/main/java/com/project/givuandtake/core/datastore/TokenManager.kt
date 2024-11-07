package com.project.givuandtake.core.datastore

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject


object TokenManager {

    private const val PREFS_NAME = "auth"
    private const val ACCESS_TOKEN_KEY = "accessToken"
    private const val REFRESH_TOKEN_KEY = "refreshToken"



    // 토큰 저장
    fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(ACCESS_TOKEN_KEY, accessToken)
        editor.putString(REFRESH_TOKEN_KEY, refreshToken)
        editor.apply()
    }

    // 액세스 토큰 가져오기
    fun getAccessToken(context: Context): String? {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(ACCESS_TOKEN_KEY, null)
    }

    // 리프레시 토큰 가져오기
    fun getRefreshToken(context: Context): String? {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(REFRESH_TOKEN_KEY, null)
    }

    // 토큰 삭제
    fun clearTokens(context: Context) {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
    // JWT 토큰에서 userId 가져오기
    fun getUserIdFromToken(context: Context): String? {
        val accessToken = getAccessToken(context)
        return accessToken?.let {
            try {
                // JWT 토큰의 payload 부분 디코딩
                val parts = it.split(".")
                if (parts.size != 3) {
                    return null
                }

                // Base64 디코딩
                val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
                val jsonObject = JSONObject(payload)

                // userId 추출 (JWT의 필드 이름이 'userId'로 가정)
                return jsonObject.getString("userId")
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // JWT 토큰에서 userName 가져오기
    fun getUserNameFromToken(context: Context): String? {
        val accessToken = getAccessToken(context)
        return accessToken?.let {
            try {
                // JWT 토큰의 payload 부분 디코딩
                val parts = it.split(".")
                if (parts.size != 3) {
                    return null
                }

                // Base64 디코딩
                val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
                val jsonObject = JSONObject(payload)

                // userName 추출 (JWT의 필드 이름이 'userName'으로 가정)
                return jsonObject.getString("userName")
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
