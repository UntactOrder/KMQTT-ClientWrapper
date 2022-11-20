package io.github.untactorder.kmqtt.clientwrap

import jdk.jfr.Unsigned


/*
 * MQTT QoS Level
 * DEFAULT is QOS_AT_MOST_ONCE
 */
enum class MQTTQoS(val level: Int) {
    QOS_AT_MOST_ONCE(0x00),
    QOS_AT_LEAST_ONCE(0x01),
    QOS_EXACTLY_ONCE(0x02);

    companion object {
        val DEFAULT = QOS_AT_MOST_ONCE

        /* If level is not matched to any listed enums, then this will return MQTTQoS.DEFAULT */
        fun valueOf(level: Int): MQTTQoS = values().find { it.level == level } ?: DEFAULT
    }
}

/*
 * MQTT Versions
 * DEFAULT is VERSION_5
 */
enum class MQTTVersion(val version: Int) {
    // VERSION_3_1(0x03),  // CocoaMQTT does not support MQTT 3.1
    VERSION_3_1_1(0x04),
    VERSION_5(0x05);

    companion object {
        val DEFAULT = VERSION_5

        /* If level is not matched to any listed enums, then this will return MqttVersion.DEFAULT */
        fun valueOf(version: Int): MQTTVersion = MQTTVersion.values().find { it.version == version } ?: DEFAULT
    }
}

/*
enum class TLSVersion(val version: Int) {
    // TLS_1_0(0x01),  // Do not use TLS 1.0 anymore.
    // TLS_1_1(0x02),  // Do not use TLS 1.1 anymore.
    TLS_1_2(0x03),
    TLS_1_3(0x04)
}
*/

/*
 * MQTT Connect Options
 */
data class MQTTConnectOptions(
    val username: String = "",  // Username for authentication
    val password: String = "",  // Password for authentication
    val tlsConfig: TLSCertConfig = TLSCertConfig(),  // TLS Config
    val mqttVersion: MQTTVersion = MQTTVersion.DEFAULT,  // MQTT Version
    val cleanStart: Boolean = true,  // If true, session is not retained. No subscriptions or undelivered messages are stored
    val willMessage: String? = null,  // Will message
    val willMessageDestination: String? = null,  // Will message destination
    val connectionTimeout: Int = 60,  // Connection timeout in seconds
    val autoReconnect: Boolean = false,  // Auto reconnect when connection is lost
    @Unsigned val retryInterval: Short = 0,  // Auto reconnect time interval in seconds (uses unsigned Short type because of CocoaMQTT)
    @Unsigned val keepAliveInterval: Short = 60,  // Keep alive interval in seconds (uses unsigned Short type because of CocoaMQTT)
)

/*
 * MQTT TLS Settings
 */
data class TLSCertConfig(
    val tlsEnabled: Boolean = false,  // TLS encryption enabled or not
    val caCertPhrase: String? = null,  // CA certificate (ex: self-signed root ca)
    val clientCertPhrase: String? = null,  // Client certificate passphrase
    val clientPrivateKeyPassword: String? = null,  // Client private key password
)
