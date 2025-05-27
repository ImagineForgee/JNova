plugins {
    id("java")
}

group = "com.github.imagineforgee.jnova"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation("io.projectreactor:reactor-core:3.8.0-M3")
}

tasks.test {
    useJUnitPlatform()
}