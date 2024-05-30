plugins {
	alias(libs.plugins.androidApplication)
	alias(libs.plugins.jetbrainsKotlinAndroid)
	alias(libs.plugins.serialization)
	alias(libs.plugins.ksp)
}

android {
	namespace = "dev.drsn.karrots"
	compileSdk = 34

	defaultConfig {
		applicationId = "dev.drsn.karrots"
		minSdk = 25
		targetSdk = 34
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	splits {
		abi {
			reset()
			isUniversalApk = true
		}
	}

	buildTypes {
		debug {
			applicationIdSuffix = ".debug"
			manifestPlaceholders["appName"] = "Debug"
		}

		release {
			isMinifyEnabled = true
			isShrinkResources = true
			manifestPlaceholders["appName"] = "Karrots"
			signingConfig = signingConfigs.getByName("debug")
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

	kotlinOptions {
		jvmTarget = "1.8"
		freeCompilerArgs += "-Xcontext-receivers"
	}

	buildFeatures {
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.1" // "1.5.0"
	}

	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1}"
		}
	}
}

dependencies {

	ksp(libs.room.compiler)
	implementation(libs.room.runtime)
	annotationProcessor(libs.room.compiler)

	implementation(libs.ktor.client.cio)
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.encoding)
	implementation(libs.ktor.serialization.kotlinx.json)
	implementation(libs.ktor.client.content.negotiation)

	implementation(libs.media3.muxer)
	implementation(libs.media3.common)
	implementation(libs.media3.effect)
	implementation(libs.media3.session)
	implementation(libs.media3.decoder)
	implementation(libs.media3.exoplayer)
	implementation(libs.media3.transformer)

	implementation(libs.coil.compose)

	implementation(libs.ui.core)
	implementation(libs.ui.graphics)

	testImplementation(libs.junit.core)
	androidTestImplementation(libs.junit.ext)

	implementation(libs.compose.material3)
	implementation(libs.compose.materiaIcons)
	implementation(libs.compose.paging)
	implementation(libs.compose.activity)
	implementation(libs.compose.navigation)

	implementation(platform(libs.compose.bom))
	androidTestImplementation(platform(libs.compose.bom))

	implementation(libs.ktx.core)
	implementation(libs.ktx.palette)
	implementation(libs.ktx.lifecycleRuntime)

	androidTestImplementation(libs.espresso.core)
}