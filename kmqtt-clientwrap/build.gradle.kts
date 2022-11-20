import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation

group = project.properties["lib.organization"]!!
version = rootProject.properties["lib.version"]!!

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("signing")
}

val dokkaHtml by tasks.getting(DokkaTask::class)

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

val dokkaJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)
        groupId = rootProject.extra["lib.organization"] as String
        version = rootProject.extra["lib.version"] as String
        artifactId = "kmqtt-clientwrap"

        pom {
            name.set("KMQTT-ClientWrapper")
            description.set("A MQTT (5.0 / 3.1.1) Client Wrapper for Kotlin Multiplatform Mobile.")
            url.set("https://github.com/UntactOrder/KMQTT-ClientWrapper/tree/main")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://raw.githubusercontent.com/UntactOrder/KMQTT-ClientWrapper/main/LICENSE")
                }
            }
            developers {
                developer {
                    id.set("UntactOrder")
                    name.set("UntactOrder Developers")
                    email.set("untactorder@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:github.com/UntactOrder/KMQTT-ClientWrapper.git")
                developerConnection.set("scm:git:ssh://github.com/UntactOrder/KMQTT-ClientWrapper.git")
                url.set("https://github.com/UntactOrder/KMQTT-ClientWrapper/tree/main")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        rootProject.extra["signing_key_id"] as String,
        rootProject.extra["signing_secret_key"] as String,
        rootProject.extra["signing_password"] as String
    )
    sign(publishing.publications)
}

kotlin {
    android {
        publishLibraryVariants("release", "debug")
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    /*
    cocoapods {
        pod("CocoaMQTT") {
            version = "~> 2.0.6"
        }
    }
    val iosPlatformList = listOf(iosArm64(), iosX64(), iosSimulatorArm64())
    iosPlatformList.forEach {
        it.binaries.framework {
            baseName = "kmqtt-clientwrap"
        }
    }


    val posixPlatformList= listOf(linuxX64(), macosX64()).plus(iosPlatformList)
    val childrenMainList = posixPlatformList.map { child ->
        child.compilations.getByName(KotlinCompilation.Companion.MAIN_COMPILATION_NAME).defaultSourceSet }
    val childrenTestList = posixPlatformList.map { child ->
        child.compilations.getByName(KotlinCompilation.Companion.TEST_COMPILATION_NAME).defaultSourceSet }

    // Ref: https://kotlinlang.org/docs/multiplatform-configure-compilations.html#configure-interop-with-native-languages
    // Library: https://github.com/eclipse/paho.mqtt.c/releases
    // Def-file describing the native API.
    // The default path is src/nativeInterop/cinterop/<interop-name>.def
    val pahoMqttDir = "src/nativeInterop"
    val enableMqttLogging = true
    listOf(mingwX64()).plus(posixPlatformList).forEach {
        val main by it.compilations.getting {
            val mqtt by cinterops.creating {
                extraOpts("-libraryPath", "$pahoMqttDir/lib/logging${if (enableMqttLogging) "Enabled" else "Disabled"}")
                includeDirs("$pahoMqttDir/include")
            }
        }
    }*/

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common", rootProject.extra["kotlin.version"] as String))
                implementation("com.soywiz.korlibs.korio:korio:3.3.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("androidx.test.ext:junit-ktx:1.1.3")
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting
        val desktopTest by getting
        val jvmMain by creating {
            dependsOn(commonMain)
            androidMain.dependsOn(this)
            desktopMain.dependsOn(this)
            dependencies {
                val pahoJavaVersion = rootProject.extra["paho.mqtt.java.client.version"] as String
                implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:${pahoJavaVersion}")
                implementation("org.eclipse.paho:org.eclipse.paho.mqttv5.client:${pahoJavaVersion}")
                //implementation(kotlin("stdlib-jdk8"))
            }
        }

        /*val mingwX64Main by getting
        val posixMain by creating {
            childrenMainList.forEach { child -> child.dependsOn(this) }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
            posixMain.dependsOn(this)
            mingwX64Main.dependsOn(this)
        }
        val mingwX64Test by getting
        val posixTest by creating {
            childrenTestList.forEach { child -> child.dependsOn(this) }
        }
        val nativeTest by creating {
            dependsOn(commonTest)
            posixTest.dependsOn(this)
            mingwX64Test.dependsOn(this)
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain) //dependsOn(posixMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
        val macosX64Main by getting
        val linuxX64Main by getting
        */
    }
}

android {
    compileSdk = (rootProject.extra["android.sdk.target.version"] as String).toInt()
    buildToolsVersion = rootProject.extra["android.build_tool.version"] as String
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    namespace = "org.digieng.kmqtt.client"
    defaultConfig {
        minSdk = (rootProject.extra["android.sdk.min.version"] as String).toInt()
        targetSdk = (rootProject.extra["android.sdk.target.version"] as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        resources {
            merges += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/LICENSE*"
            )
            pickFirsts += "/bundle.properties"
        }
    }
}
