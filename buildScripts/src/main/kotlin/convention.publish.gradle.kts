plugins {
    `maven-publish`
    signing
}

ext["signing.keyId"] = LocalProperties.get(rootDir, LocalProperties.SIGNING_KEY_ID)
ext["signing.password"] = LocalProperties.get(rootDir, LocalProperties.SIGNING_PASSWORD)
ext["signing.secretKeyRingFile"] = LocalProperties.get(rootDir, LocalProperties.SIGNING_SECRET_KEY_RING_FILE)

publishing {
    repositories {
        maven {
            name = "Sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")

            credentials {
                username = LocalProperties.get(rootDir, LocalProperties.USER)
                password = LocalProperties.get(rootDir, LocalProperties.PASSWORD)
            }
        }

        maven {
            name = "CustomLocal"
            url = uri(LocalProperties.find(rootDir, LocalProperties.PUBLISH_URI))
        }
    }

    publications {
        create<MavenPublication>("release") {
            groupId = "io.github.andrewchupin"
            artifactId = project.path.drop(1).replace(":", "-")
            version = "1.0.0"

            pom {
                name.set("Kitten DI")
                url.set("https://github.com/AndrewChupin/Kitten")
                description.set("Unknown")

                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("AndrewChupin")
                        name.set("Andrew Chupin")
                        email.set("andrewchupin96@gmail.com")
                    }
                }

                scm {
                    url.set("https://github.com/AndrewChupin/Kitten")
                }
            }
        }

        withType<MavenPublication> {
            afterEvaluate {
                project.components.findByName("release")
                    ?.let { from(it) }

                project.components.findByName("java")
                    ?.let { from(it) }
            }
        }
    }
}

signing {
    sign(publishing.publications.getByName("release"))
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}
