plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version "2.0.20"
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}