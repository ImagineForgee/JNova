plugins {
    id("java")
}


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.register("generateAllJavadocs") {
    dependsOn(
        subprojects.mapNotNull { it.tasks.findByName("generateModuleJavadoc") }
    )
}
