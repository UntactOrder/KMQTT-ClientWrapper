# Kotlin MQTT Client Wrapper

This lib is a MQTT (5.0 / 3.1.1) Client Wrapper for Kotlin Multiplatform Mobile written in Kotlin 1.7.10.

- **jvm** by paho.mqtt.java - https://github.com/eclipse/paho.mqtt.java
- **iosX64** by CocoaMQTT - https://github.com/emqx/CocoaMQTT
- **iosArm64**
- **iosSimulatorArm64**

<pre>
commonMain  <- jvmMain  <- androidMain
                        <- desktopMain
            <- iosMain  <- iosX64Main
                        <- iosArm64Main
                        <- iosSimulatorArm64Main
</pre>
