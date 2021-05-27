import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32" // Used to create a javadoc jar
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

apply(from = "${rootDir}/scripts/publish.gradle")

val dokkaVersion: String by project

repositories {
    mavenCentral()
    jcenter()
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

dependencies {
    compileOnly("org.jetbrains.dokka:dokka-core:$dokkaVersion")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.dokka:dokka-base:$dokkaVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.dokka:dokka-test-api:$dokkaVersion")
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}
