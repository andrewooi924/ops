plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

// Where the app looks for catalog updates. The Catalog workflow publishes both
// files to the "catalog" release on GitHub. For testing, a debug build can point
// elsewhere: ./gradlew installDebug -PcatalogBase=http://10.0.2.2:8765
val catalogBase = (findProperty("catalogBase") as String?)
    ?: "https://github.com/andrewooi924/ops/releases/download/catalog"

android {
    namespace = "com.optcg.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.optcg.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"

        buildConfigField("String", "CATALOG_URL", "\"$catalogBase/catalog.json.gz\"")
        buildConfigField("String", "CATALOG_VERSION_URL", "\"$catalogBase/version.txt\"")
        // Hosted image URLs come from the catalog's image_base, so changing image hosts
        // needs no app update. Without it the app loads Bandai's original images.
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    androidResources {
        // The bundled catalog is already gzipped. (It isn't named .gz because the
        // packager would unzip it and drop the extension.)
        noCompress += "gzip"
    }
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.work.runtime)
    implementation(libs.coil.compose)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.android)
    implementation(libs.okhttp)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
}
