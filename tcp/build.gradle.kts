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
    implementation(project(":annotations"))
    implementation("io.github.classgraph:classgraph:4.8.179")
    implementation("io.projectreactor:reactor-core:3.8.0-M3")
}

tasks.test {
    useJUnitPlatform()
}