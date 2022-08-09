plugins {
    `java-library`
}

subprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
    }

    tasks.test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    dependencies {
        testImplementation(Testing.junit.jupiter.api)
        testImplementation(Testing.junit.jupiter.params)
        testRuntimeOnly(Testing.junit.jupiter.engine)
        testImplementation(Testing.mockito.junitJupiter)
        testImplementation("org.assertj:assertj-core:_")
    }
}