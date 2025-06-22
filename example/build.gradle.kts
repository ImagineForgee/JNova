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
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")
}