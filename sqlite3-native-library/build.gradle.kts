import java.util.Properties

plugins {
    id("com.android.library") version "8.0.1"
    id("maven-publish")
    id("signing")
}

group = "net.codeedu"
version = "3.43.1"
description = "Native sqlite3 library without JNI bindings"

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = 33
    ndkVersion = "25.2.9519653"

    namespace = "net.codeedu.sqlite3_native_library"

    defaultConfig {
        minSdk = 16

        ndk {
            abiFilters += setOf("arm64-v8a","armeabi-v7a")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    externalNativeBuild {
        cmake {
            path = file("cpp/CMakeLists.txt")
        }
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

val secretsFile = rootProject.file("local.properties")
val secretProperties = Properties()

if (secretsFile.exists()) {
    secretsFile.reader().use { secretProperties.load(it) }

    secretProperties.forEach { key, value ->
        if (key is String && key.startsWith("signing")) {
            ext[key] = value
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/nay-kang/sqlite-native-libraries")

                developers {
                    developer {
                        id.set("naykang")
                        name.set("Nay Kang")
                        email.set("kunvunk@gmail.com")
                    }
                }

                licenses {
                    license {
                        name.set("Public Domain")
                        url.set("https://www.sqlite.org/copyright.html")
                    }
                }

                scm {
                    connection.set("scm:git:github.com/nay-kang/sqlite-native-libraries.git")
                    developerConnection.set("scm:git:ssh://github.com/nay-kang/sqlite-native-libraries.git")
                    url.set("https://github.com/nay-kang/sqlite-native-libraries")
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = secretProperties.getProperty("ossrhUsername")
                password = secretProperties.getProperty("ossrhPassword")
            }
        }

        maven {
            name = "here"
            url = uri("build/here/")
        }
    }
}

signing {
//    useGpgCmd()
    sign(publishing.publications)
}

tasks.withType<AbstractPublishToMaven>() {
    dependsOn("assembleRelease")
}
