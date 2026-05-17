import java.util.Properties
import java.io.FileInputStream

plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.compose")
  id("org.jetbrains.kotlin.plugin.serialization")
}

android {
  namespace = "com.animalartstudio.kids"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.animalartstudio.kids"
    minSdk = 26
    targetSdk = 35
    versionCode = 4
    versionName = "0.3.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    val p = project.rootProject.file("local.properties")
    val base =
        if (p.exists()) {
          Properties()
              .apply { load(FileInputStream(p)) }
              .getProperty("animal_art_studio_url", "http://10.0.2.2:8080/")
        } else {
          "http://10.0.2.2:8080/"
        }
    buildConfigField("String", "ANIMAL_ART_STUDIO_URL", "\"${base.trimEnd('/').replace("\\", "\\\\")}\"")
    // Self-contained APK: run coaching in-process, no Ktor backend.
    // Override via -PofflineBuild=false to produce a LAN-backed build.
    val offline = (project.findProperty("offlineBuild") as? String)?.toBoolean() ?: true
    buildConfigField("Boolean", "OFFLINE_BUILD", offline.toString())
  }
  buildTypes {
    release {
      isMinifyEnabled = false
      // Self-contained installable APK for sideloading (replace with a release keystore for Play Store).
      signingConfig = signingConfigs.getByName("debug")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { compose = true; buildConfig = true }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  val composeBom = platform("androidx.compose:compose-bom:2024.10.00")
  implementation(composeBom)
  androidTestImplementation(composeBom)
  implementation("androidx.core:core-ktx:1.15.0")
  implementation("androidx.activity:activity-compose:1.9.3")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.compose.material:material-icons-extended")
  implementation("androidx.navigation:navigation-compose:2.8.3")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  // REVIEW_NOTES C-8: persisted parent settings.
  implementation("androidx.datastore:datastore-preferences:1.1.1")
  debugImplementation("androidx.compose.ui:ui-tooling")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.2.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
