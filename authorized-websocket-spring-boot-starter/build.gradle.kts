plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "9.0.0"
    id("org.springframework.boot") version "3.5.6"
}

apply(plugin = "io.spring.dependency-management")

repositories {
    mavenCentral()
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-websocket")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    archiveClassifier = ""
}

tasks.bootJar {
    enabled = false
}

tasks.withType<GenerateModuleMetadata>().configureEach {
    suppressedValidationErrors.set(setOf("dependencies-without-versions"))
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/KirillSmirnow/authorized-websocket-spring-boot-starter")
            credentials {
                username = "KirillSmirnow"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("github") {
            from(components["java"])
        }
    }
}
