plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    id("com.google.gms.google-services") version "4.5.0" apply false
    id("com.codingfeline.buildkonfig") version "0.20.0" apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.androidxRoom3) apply false
}
