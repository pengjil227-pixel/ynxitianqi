plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.itcast.hmweather"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.itcast.hmweather"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            //设置支持的SO库架构（开发者可以根据需要，选择一个或多个平台的so）
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }

        vectorDrawables.useSupportLibrary = true

        manifestPlaceholders += mapOf(
            "applicationLabel" to "@string/app_name"
        )
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // 协程
    implementation (libs.kotlinx.coroutines.android)

    implementation (libs.easypermissions)

    // Retrofit + okhttp
    implementation (libs.retrofit)
    implementation (libs.converter.gson) // Gson解析
    implementation (libs.logging.interceptor) // 网络日志

    implementation(libs.baserecyclerviewadapterhelper4)

    implementation (libs.greenrobot.eventbus) // 使用最新版本

    // 或者批量添加所有JAR
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation (libs.glide)

    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.eddsa)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.media3.ui)
    implementation (libs.circleimageview)
    implementation(libs.androidx.media3.exoplayer) // 检查最新版本
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}