plugins {
    id("java")
    id("maven-publish")
}

group = "jnova"
version = "0.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.classgraph:classgraph:4.8.179")
    implementation("io.projectreactor:reactor-core:3.8.0-M3")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "cloudsmith"
            url = uri("https://maven.cloudsmith.io/imagineforgee/jnova/maven/")
            credentials {
                username = (findProperty("cloudsmith.user") as String?)
                    ?: System.getenv("CLOUDSMITH_USER")
                password = (findProperty("cloudsmith.apiKey") as String?)
                    ?: System.getenv("CLOUDSMITH_API_KEY")
            }
        }
    }
}