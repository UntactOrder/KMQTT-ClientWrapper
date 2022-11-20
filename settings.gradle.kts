pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()

        maven {
            setUrl("https://jitpack.io")
            setUrl("https://kotlin.bintray.com/kotlinx")
            setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("native.cocoapods").version(extra["kotlin.version"] as String)
        kotlin("android").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["android.gradle_plugin.version"] as String)
        id("com.android.library").version(extra["android.gradle_plugin.version"] as String)
        /**
         * Compose Gradle Plugin
         * https://plugins.gradle.org/plugin/org.jetbrains.compose
         */
        id("org.jetbrains.compose").version(extra["compose.plugin.version"] as String)
        /**
         * Dokka Gradle Plugin
         */
        id("org.jetbrains.dokka").version(extra["kotlin.version"] as String)
        /**
         * Sonatype Nexus Publish Plugin
         */
        id("io.github.gradle-nexus.publish-plugin").version(extra["nexus.publish-plugin.version"] as String)
        id("org.jetbrains.kotlin.android") version "1.7.20"
    }
}

rootProject.name = "KMQTT-ClientWrapper"

include(":android", ":desktop", ":common")
include(":kmqtt-clientwrap")
