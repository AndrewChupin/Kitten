plugins {
    kotlin("jvm")
    id("convention.publish")
}

extensions.getByType<JavaPluginExtension>().run {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    api(project(":api"))
}
