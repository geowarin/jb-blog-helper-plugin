plugins {
    kotlin("jvm") version "1.9.22"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.geowarin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<org.jetbrains.intellij.tasks.RunIdeTask> {
    autoReloadPlugins.set(true)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2023.3.4")
    plugins.set(listOf("markdown"))
    updateSinceUntilBuild.set(false)
}

tasks {
    patchPluginXml {
        changeNotes.set("""
            Add change notes here.<br>
            <em>most HTML tags may be used</em>        """.trimIndent())
    }
}
