import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinCompiler)
    alias(libs.plugins.kotlinKsp)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.jgit)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val repo = jgit.repo()
val commitCount = (repo?.commitCount("refs/remotes/origin/master") ?: 2024) + 2024
val latestTag = repo?.latestTag?.removePrefix("v") ?: "1.0.1-SNAPSHOT"

val verCode by extra(commitCount)
val verName by extra(latestTag)
val androidTargetSdkVersion by extra(37)
val androidMinSdkVersion by extra(26)

android {
    namespace = "cn.xihan.age"
    compileSdk = androidTargetSdkVersion

    signingConfigs {
        create("xihantest") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    defaultConfig {
        minSdk = androidMinSdkVersion
        versionCode = verCode
        versionName = verName

        resourceConfigurations.addAll(listOf("zh"))
        signingConfig = signingConfigs.getByName("xihantest")

        buildConfigField("long", "BUILD_TIMESTAMP", "${System.currentTimeMillis()}L")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = false
            isJniDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            isPseudoLocalesEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    hilt.enableAggregatingTask = true

    packaging {
        resources {
            excludes += mutableSetOf(
                "META-INF/*******",
                "**/*.txt",
                "**/*.xml",
                "**/*.properties",
                "DebugProbesKt.bin",
                "kotlin-tooling-metadata.json"
            )

        }
        dex.useLegacyPackaging = true
    }
    lint.abortOnError = false
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.expandProjection", "true")
    arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
    arg("Ktorfit_Errors", "1")
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.multidex)
    implementation(libs.webkit)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.lifecycle.common.java8)
//    implementation(libs.work.runtime)
//    implementation(libs.work.runtime.ktx)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    implementation(libs.paging.runtime)
    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)
    implementation(libs.media3.ui)
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.datasource.okhttp)
    ksp(libs.room.compiler)

//    implementation(libs.kotlinx.atomicfu)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.json.okio)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.lottie.compose)
    implementation(libs.ktorfit.lib)
    implementation(libs.ktorfit.converters.flow )
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization.json)

    implementation(libs.orbit.core)
    implementation(libs.orbit.compose)
    implementation(libs.orbit.viewmodel)
    implementation(libs.dagger.hilt.android)
//    implementation(libs.hilt.work)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.kotlin.metadata.jvm)
    ksp(libs.dagger.hilt.compiler)
//    kapt(libs.hilt.compiler)
    implementation(libs.dialogx) {
        exclude(group = "com.android.support")
        exclude(group = "com.google.android.material")
    }
    implementation(libs.dialogx.materialyou) {
        exclude(group = "com.android.support")
        exclude(group = "com.google.android.material")
    }
    implementation(libs.landscapist.coil) {
        exclude(group = "io.coil-kt")
    }
    implementation(libs.whatif)
//    implementation(libs.xxpermissions) {
//        exclude(group = "com.android.support")
//    }
    implementation(libs.timber)
//    implementation(libs.happy.dns)
    implementation(libs.channel) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-android-extensions-runtime")
    }
    implementation(libs.mmkv.static)
    implementation(libs.coil)
    implementation(libs.coil.video)
    implementation(libs.coil.compose)
    implementation(libs.startup)
    implementation(libs.ihsanbal.logging.interceptor) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "org.json")
    }
//    implementation(libs.composeicons.fontwwesome)

    implementation(libs.jsoup)

    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.util)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.foundation)
    implementation(libs.runtime)
    implementation(libs.animation)
    implementation(libs.navigation.compose)
//    implementation(libs.material3.window.size)
    implementation(libs.accompanist.swiperefresh)
//    implementation(libs.accompanist.placeholder.material)

//    implementation(libs.accompanist.webview)
    implementation(libs.android.material)
    implementation(libs.compose.material.icons.core)
    implementation(libs.compose.material.icons.extended)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
//    androidTestImplementation(libs.work.testing)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
