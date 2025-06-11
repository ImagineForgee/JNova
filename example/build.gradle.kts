plugins {
    java
}

group = "jnova.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(project(":jnova-annotations"))
    implementation(project(":jnova-core"))
    implementation(project(":jnova-tcp-server"))
    implementation("io.projectreactor:reactor-core:3.8.0-M3")
}