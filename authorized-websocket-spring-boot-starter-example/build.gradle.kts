plugins {
    java
    id("io.freefair.lombok") version "9.0.0"
    id("org.springframework.boot") version "3.5.6"
}

apply(plugin = "io.spring.dependency-management")

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":authorized-websocket-spring-boot-starter"))
}
