plugins {
    id("java")
}

group = "com.github.toastshaman"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(Testing.junit.jupiter.api)
    testImplementation(Testing.junit.jupiter.params)
    testRuntimeOnly(Testing.junit.jupiter.engine)

    testImplementation("org.assertj:assertj-core:_")
    testImplementation("com.amazonaws:aws-lambda-java-events:_")
    testImplementation("com.amazonaws:aws-lambda-java-core:_")
    testImplementation("com.google.code.gson:gson:_")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}