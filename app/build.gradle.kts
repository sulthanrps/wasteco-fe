import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
// CI passes values through environment variables; local builds read local.properties.
fun configValue(property: String, env: String): String? =
    System.getenv(env)?.takeIf { it.isNotBlank() } ?: localProperties.getProperty(property)

val wastecoApiKey: String = configValue("wasteco.apiKey", "WASTECO_API_KEY") ?: ""
val releaseStoreFile: String? = configValue("wasteco.signing.storeFile", "WASTECO_KEYSTORE_PATH")

android {
    namespace = "com.example.wasteco"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.wasteco"
        minSdk = 33
        targetSdk = 37
        // The release workflow derives both from the git tag (v1.2.3 -> 1.2.3 / 10203).
        versionCode = providers.gradleProperty("wasteco.versionCode").orNull?.toInt() ?: 1
        versionName = providers.gradleProperty("wasteco.versionName").orNull ?: "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "WASTECO_API_KEY", "\"$wastecoApiKey\"")
        buildConfigField("String", "BASE_URL", "\"https://wastecobe.mhqif.my.id/\"")
    }

    signingConfigs {
        if (releaseStoreFile != null) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFile)
                storePassword = configValue("wasteco.signing.storePassword", "WASTECO_KEYSTORE_PASSWORD")
                keyAlias = configValue("wasteco.signing.keyAlias", "WASTECO_KEY_ALIAS")
                keyPassword = configValue("wasteco.signing.keyPassword", "WASTECO_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("release")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}