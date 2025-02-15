// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.14.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    // KtLint - Static code analysis
    // https://github.com/pinterest/ktlint/releases
    val ktlint by configurations.creating

    dependencies {
        // KtLint - Static code analysis
        // https://github.com/pinterest/ktlint/releases
        ktlint("com.pinterest:ktlint:0.42.1") {
            attributes {
                attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            }
        }
    }

    // KtLint - Static code analysis
    // https://github.com/pinterest/ktlint/releases
    tasks.register<JavaExec>("ktlint") {
        group = "verification"
        description = "Check Kotlin code style."
        classpath = ktlint
        main = "com.pinterest.ktlint.Main"
        args("--android", "src/**/*.kt")
    }
}