import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.1.10"
    //id("com.google.devtools.ksp")
}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.timor.kidsstory"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.timor.kidsstory"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties["GEMINI_API_KEY"]}\"")
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
    buildFeatures {
        compose = true
        buildConfig = true
    }


}

dependencies {
    implementation(libs.androidx.navigation.compose)
    implementation(libs.accompanist.pager)
    implementation(libs.google.accompanist.pager.indicators)
    implementation(libs.coil.compose)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Ktor
    implementation("io.ktor:ktor-client-android:2.3.12")
    implementation("io.ktor:ktor-client-cio:2.3.12")
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")
    implementation("io.ktor:ktor-client-logging:2.3.12")
    implementation("io.ktor:ktor-client-okhttp:2.3.12")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")

    // hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")


    // Logger
    implementation("com.orhanobut:logger:2.2.0")

    // Gemini - ai
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // room db
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    // roomdb 사용시 Kotlin 컴파일러가 생성하는 메타데이터를 읽어들일수있도록 metadata-jvm 강제 업데이트
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:2.1.0")

    // EPUB 추출기 모듈 추가(TODO: devimplementation으로 리팩토링)
    implementation(project(":epub-extractor"))
}

// EPUB 리소스 추출 태스크
// 태스크 의존성으로 설정
tasks.register("extractEpubResources") {
    dependsOn(":epub-extractor:fatJar")

    doLast {
        val epubSourceDir = rootProject.file("epub-source").absolutePath
        val outputAssetsDir = file("src/main/assets").absolutePath
        val metadataFile = file("src/main/assets/metadata/stories-metadata.json").absolutePath

        // Fat JAR 경로
        val epubExtractorJar = project(":epub-extractor").layout.buildDirectory
            .dir("libs").get().asFile.listFiles()
            ?.firstOrNull { it.name.contains("fat") && it.name.endsWith(".jar") }
            ?.absolutePath ?: throw GradleException("epub-extractor Fat JAR not found")

        exec {
            executable = "java"
            args = listOf(
                "-jar", epubExtractorJar,
                epubSourceDir,
                outputAssetsDir,
                metadataFile
            )
        }
    }
}