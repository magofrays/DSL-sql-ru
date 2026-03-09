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
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.apache.commons:commons-lang3:3.20.0")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

