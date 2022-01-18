plugins {
    java
}

group = "best.boba"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        name = "velocity"
        url = uri("https://nexus.velocitypowered.com/repository/maven-public/")
    }
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    compileOnly("com.velocitypowered:velocity-api:3.0.1")
    compileOnly("org.apache.commons:commons-collections4:4.4")
    annotationProcessor("com.velocitypowered:velocity-api:3.0.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}