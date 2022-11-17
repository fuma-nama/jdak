import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `maven-publish`
    kotlin("jvm") version "1.7.20"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    signing
}

group = "io.github.sonmoosans"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("net.dv8tion:JDA:5.0.0-alpha.22")

    testImplementation("net.dv8tion:JDA:5.0.0-alpha.22")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("JDAK")
                description.set("A Light-Weight, Fast, Flexible, Functional Programming Command framework for JDA written in Kotlin")
                url.set("https://github.com/SonMooSans/jdak")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }
                developers {
                    developer {
                        name.set("SonMooSans")
                        email.set("god63820869@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/SonMooSans/jdak.git")
                    developerConnection.set("scm:git:ssh://github.com/SonMooSans/jdak.git")
                    url.set("https://github.com/SonMooSans/jdak/tree/master")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(project.properties["username"]!!.toString())
            password.set(project.properties["password"]!!.toString())
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}