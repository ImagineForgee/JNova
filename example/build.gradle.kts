plugins {
    java
    application
}

group = "jnova.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.projectreactor:reactor-core:3.8.0-M3")
    implementation(project(":core"))
    implementation(project(":tcp"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "jnova.example.Main"
}

tasks.test {
    useJUnitPlatform()
}