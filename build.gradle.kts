plugins {

    application
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    testImplementation(libs.junit)
    implementation(libs.guava)
    implementation("com.github.demidko:aot:2025.11.25")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

