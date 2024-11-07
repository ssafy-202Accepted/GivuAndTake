plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
//    id("kotlin-kapt")
    id("com.google.devtools.ksp")

}

android {
    namespace = "com.project.givuandtake"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.givuandtake"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // 토스 위젯
    implementation("com.github.tosspayments:payment-sdk-android:0.1.15")

    // 직렬화
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation ("androidx.camera:camera-core:1.1.0")
    implementation ("androidx.camera:camera-camera2:1.1.0")
    implementation ("androidx.camera:camera-lifecycle:1.1.0")
    implementation ("androidx.camera:camera-view:1.1.0")

    // Room 추가
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    kapt("androidx.room:room-compiler:2.6.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")
    implementation("com.android.billingclient:billing-ktx:7.1.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    implementation("com.naver.maps:map-sdk:3.19.1")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.code.gson:gson:2.8.8")
    // Retrofit - 네트워크 요청 라이브러리
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson - JSON 데이터를 변환하는 라이브러리
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp - 네트워크 요청 로깅 (선택 사항)
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.0")

    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-crashlytics:19.1.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.github.skydoves:landscapist-glide:1.4.6")
    implementation("com.google.firebase:firebase-auth:23.0.0")

    //Coil 추가
    implementation("io.coil-kt:coil-compose:2.4.0")

    // datastore 추가
    implementation("androidx.datastore:datastore-preferences:1.1.1")


    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.5")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material:1.7.0")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.ui:ui-test-android:1.7.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}