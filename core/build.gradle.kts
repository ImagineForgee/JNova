plugins {
    id("java")
}

group = "jnova"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.classgraph:classgraph:4.8.179")
    implementation("io.projectreactor:reactor-core:3.8.0-M3")
}
