buildscript {
    val localProp = java.util.Properties()
    localProp.load(file("local.properties").inputStream())

    extra["organization"] = project.properties["lib.organization"]
    extra["version"] = project.properties["lib.version"]

    extra["ossrh_username"] = localProp.getProperty("ossrh.username") ?: ""
    extra["ossrh_password"] = localProp.getProperty("ossrh.password") ?: ""
    extra["sonatype_staging_profile_id"] = localProp.getProperty("sonatype.staging.profile_id") ?: ""
    extra["signing_key_id"] = localProp.getProperty("signing.key_id") ?: "your-key-id"
    extra["signing_secret_key"] = localProp.getProperty("signing.secret_key") ?: "your-secret-key"
    extra["signing_password"] = localProp.getProperty("signing.password") ?: "your-password"

    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

group = project.properties["lib.organization"]!!
version = project.properties["lib.version"]!!

plugins {
    kotlin("multiplatform").apply(false)
    kotlin("native.cocoapods").apply(false)
    kotlin("android").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("org.jetbrains.dokka").apply(false)
    id("io.github.gradle-nexus.publish-plugin").apply(true)
}

nexusPublishing {
    repositories {
        create("myNexus") {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            stagingProfileId.set(project.extra["sonatype_staging_profile_id"] as String)
            username.set(project.extra["ossrh_username"] as String)
            password.set(project.extra["ossrh_password"] as String)
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        google()

        maven {
            setUrl("https://jitpack.io")
            setUrl("https://kotlin.bintray.com/kotlinx")
            setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
