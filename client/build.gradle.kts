plugins {
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "26.0.1"
    modules = listOf("javafx.controls")
}

group = "org.tcs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("org.postgresql:postgresql:42.7.11")
}

tasks.test {
    useJUnitPlatform()
}