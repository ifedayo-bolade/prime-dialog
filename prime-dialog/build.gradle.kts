import org.gradle.api.publish.maven.MavenPublication

plugins {
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.android.library)
}

android {
    namespace = "ifedayo.bolade.primedialog"
    compileSdk = 37

    defaultConfig {
        minSdk = 23

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.github.ifedayo-bolade"
            artifactId = "prime-dialog"
            version = "1.0.2"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("PrimeDialog")
                description.set("A highly customizable Android dialog library.")
                url.set("https://github.com/ifedayo-bolade/PrimeDialog")
            }
        }
    }
}

dependencies {
    implementation(libs.kenburnsview)
    implementation(libs.androidx.appcompat)
}