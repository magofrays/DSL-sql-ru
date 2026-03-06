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
    implementation("ru.stachek66.nlp:mystem-scala:0.1.6")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

