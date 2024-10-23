// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.devtools.ksp") version "1.9.0-1.0.11"
}

buildscript {
    dependencies {
        classpath (libs.androidx.navigation.safe.args.gradle.plugin)
        classpath("com.google.devtools.ksp:com.google.devtools.ksp.gradle.plugin:1.9.0-1.0.11")
    }
}