import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain
import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinKsp)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.ktorfit)
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "cn.xihan.age"
    compileSdkPreview = "UpsideDownCake"

    androidResources.additionalParameters += arrayOf(
        "--allow-reserved-package-id",
        "--package-id",
        "0x64"
    )

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
        applicationId = "cn.xihan.age"
        minSdk = 24
        targetSdk = 33
        versionCode = 740
        versionName = "7.4.0"

        resourceConfigurations.addAll(listOf("zh"))
        signingConfig = signingConfigs.getByName("xihantest")

        buildConfigField("long", "BUILD_TIMESTAMP", "${System.currentTimeMillis()}L")

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
            arg("dagger.hilt.disableModulesHaveInstallInCheck", "true")
            arg("Ktorfit_Errors", "1")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isJniDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = false
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

    kotlinOptions.jvmTarget = "17"

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions.kotlinCompilerExtensionVersion = "1.4.7-dev-k1.9.0-Beta-bb7dc8b44eb"

    hilt.enableAggregatingTask = true

    packaging {
        resources {
            excludes += mutableSetOf(
                "META-INF/*******",
                "**/*.txt",
                "**/*.xml",
                "**/*.properties",
                "DebugProbesKt.bin",
                "java-tooling-metadata.json",
                "kotlin-tooling-metadata.json"
            )

        }
        dex.useLegacyPackaging = true
    }
    lint.abortOnError = false
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.multidex)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.viewmodel.savedstate)
    implementation(libs.lifecycle.common.java8)
    implementation(libs.work.runtime)
    implementation(libs.work.runtime.ktx)
    androidTestImplementation(libs.work.testing)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    implementation(libs.paging.runtime)
    implementation(libs.paging.runtime.ktx)
    implementation(libs.paging.compose)
    implementation(libs.media3.ui)
//    implementation(libs.media3.common)
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.datasource.okhttp)
//    implementation(libs.datastore)
    implementation(libs.datastore.preferences)
    ksp(libs.room.compiler)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.android)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.core)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json)
    implementation(libs.org.jetbrains.kotlinx.kotlinx.serialization.json.okio)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.io.github.hadiyarajesh.flower.ktorfit.flower.ktorfit)
    implementation(libs.de.jensklingenberg.ktorfit.ktorfit.lib)
    ksp(libs.de.jensklingenberg.ktorfit.ktorfit.ksp)
    implementation(libs.io.ktor.ktor.client.core)
    implementation(libs.io.ktor.ktor.client.okhttp)
    implementation(libs.io.ktor.ktor.client.logging)
    implementation(libs.io.ktor.ktor.client.content.negotiation)
    implementation(libs.io.ktor.ktor.client.serialization.kotlinx.json)

    implementation(libs.org.orbit.mvi.orbit.core)
    implementation(libs.org.orbit.mvi.orbit.compose)
    implementation(libs.org.orbit.mvi.orbit.viewmodel)
    implementation(libs.com.google.dagger.hilt.android)
    implementation(libs.hilt.work)
    implementation(libs.hilt.navigation.compose)
    kapt(libs.com.google.dagger.hilt.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.com.kongzue.dialogx) {
        exclude(group = "com.android.support")
        exclude(group = "com.google.android.material")
    }
    implementation(libs.com.kongzue.dialogx.materialyou) {
        exclude(group = "com.android.support")
        exclude(group = "com.google.android.material")
    }
    implementation(libs.com.skydoves.landscapist.coil) {
        exclude(group = "io.coil-kt")
    }
    implementation(libs.com.skydoves.whatif)
    implementation(libs.com.getactivity.xxpermissions.xxpermissions) {
        exclude(group = "com.android.support")
    }
    implementation(libs.com.parfoismeng.slideback.slideback)
//    implementation(libs.io.github.ltttttttttttt.composeviews)
    implementation(libs.com.jakewharton.timber.timber)
    implementation(libs.com.qiniu.happy.dns)
    implementation(libs.com.github.liangjingkanji.channel)
    implementation(libs.com.tencent.mmkv.static)
    implementation(libs.io.coil.kt.coil)
    implementation(libs.io.coil.kt.coil.video)
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.startup)
    implementation(libs.com.github.ihsanbal.logging.interceptor) {
        exclude(group = "com.squareup.okhttp3")
        exclude(group = "org.json")
    }
//    implementation(libs.jzVideo)
    implementation(libs.jsoup)
//    implementation(libs.htmlunit)

    //    implementation(libs.com.arkivanov.decompose.decompose)
//    implementation(libs.com.arkivanov.decompose.extensions.android)
//    implementation("com.arkivanov.decompose:extensions-compose-jetpack:1.0.0") {
//        exclude(group = "androidx.compose")
//    }

    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.foundation)
    implementation(libs.runtime)
    implementation(libs.animation)
    implementation(libs.navigation.compose)
    implementation(libs.material3.window.size)
    implementation(libs.com.google.accompanist.accompanist.systemuicontroller)
    implementation(libs.com.google.accompanist.accompanist.swiperefresh)
    implementation(libs.com.google.accompanist.accompanist.themeadapter.material3)
//    implementation(libs.com.google.accompanist.accompanist.flowlayout)
//    implementation(libs.com.google.accompanist.accompanist.navigation.animation)
    implementation(libs.com.google.accompanist.accompanist.navigation.material)
    implementation(libs.com.google.accompanist.accompanist.webview)
    implementation(libs.com.google.android.material)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

}

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = libs.versions.ktorfit.get()
}
val service = project.extensions.getByType<JavaToolchainService>()
val customLauncher = service.launcherFor {
    languageVersion.set(JavaLanguageVersion.of("17"))
}
project.tasks.withType<UsesKotlinJavaToolchain>().configureEach {
    kotlinJavaToolchain.toolchain.use(customLauncher)
}

/*
kotlin{
    sourceSets.all {
        languageSettings.apply {
            languageVersion = "2.0"
        }
    }
}

 */

kapt {
    correctErrorTypes = true
}