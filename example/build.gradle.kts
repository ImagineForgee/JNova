plugins {
    java
}

group = "jnova.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":annotations"))
    implementation(project(":core"))
    implementation(project(":tcp"))
    implementation("io.projectreactor:reactor-core:3.8.0-M3")
}