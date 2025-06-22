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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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

tasks.register<Javadoc>("generateModuleJavadoc") {
    setDestinationDir(file("${rootProject.projectDir}/docs-site/static/api/${project.name}"))
    source = sourceSets["main"].allJava
    classpath = sourceSets["main"].compileClasspath
    isFailOnError = false
}