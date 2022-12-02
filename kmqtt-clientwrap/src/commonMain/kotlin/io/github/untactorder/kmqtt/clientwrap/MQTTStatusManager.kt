package io.github.untactorder.kmqtt.clientwrap


open class MQTTException(
    val reasonCode: MQTTError = MQTTError.CLIENT_EXCEPTION,
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception()

open class MqttSecurityException: MQTTException()
open class MqttPersistenceException: MQTTException()


enum class MQTTError(val errorCode: Int) {
    CLIENT_EXCEPTION(0),
    INVALID_PROTOCOL_VERSION(1),
    INVALID_CLIENT_ID(2),
    BROKER_UNAVAILABLE(3),
    UNEXPECTED_ERROR(6),
    SUBSCRIBE_FAILED(80),
    CLIENT_TIMEOUT(32000),
    NO_MESSAGE_IDS_AVAILABLE(32001),
    WRITE_TIMEOUT(32002),
    CLIENT_CONNECTED(32100),
    CLIENT_ALREADY_DISCONNECTED(32101),
    CLIENT_DISCONNECTING(32102),
    SERVER_CONNECT_ERROR(32103),
    CLIENT_NOT_CONNECTED(32104),
    SOCKET_FACTORY_MISMATCH(32105),
    SSL_CONFIG_ERROR(32106),
    CLIENT_DISCONNECT_PROHIBITED(32107),
    INVALID_MESSAGE(32108),
    REASON_CODE_CONNECTION_LOST(32109),
    CONNECT_IN_PROGRESS(32110),
    CLIENT_CLOSED(32111),
    TOKEN_INUSE(32201),
    MAX_INFLIGHT(32202),
    DISCONNECTED_BUFFER_FULL(32203),

    /* MQTT 5.0 Packet Reason Codes */
    INVALID_IDENTIFIER(50000),
    INVALID_RETURN_CODE(50001),
    MALFORMED_PACKET(50002),
    UNSUPPORTED_PROTOCOL_VERSION(50003),
    INVALID_TOPIC_ALAS(50004),
    DUPLICATE_PROPERTY(50005),

    /* MQTT Security Exception (Auth Status can be 0, 4, 5) */
    AUTHENTICATION_FAILED(4),
    NOT_AUTHORIZED(5),

    /* MQTT Persistence Exception */
    PERSISTENCE_IN_USE(32200);  // Persistence is already in use

    companion object {
        fun valueOf(errorCode: Int): MQTTError? = MQTTError.values().find { it.errorCode == errorCode }
    }
}


expect open class MQTTStatusManager(
    onConnectComplete: (reconnect: Boolean, serverURI: String?) -> Unit,  // MQTT 5.0 Only
    onMessageArrived: (topic: String, message: MQTTPacketPayload) -> Unit,
    onConnectionLost: (properties: MQTTProperties) -> Unit,
    onTopicPublishSuccess: (messageId: Int) -> Unit,
    onErrorOccurred: (exception: MQTTException?) -> Unit,  // MQTT 5.0 Only
    onAuthPacketsReceived: (properties: MQTTProperties) -> Unit  // MQTT 5.0 Only
)


data class MQTTPacketPayload(
    val value: String,  // Message payload
    val messageId: Int,  // Identifier of the message
    val qos: MQTTQoS,  // Quality of Service
    val retained: Boolean = false,  // Message is Retained (or should be retained) by Broker or not
    val isDuplicate: Boolean = false  // Duplicate Message or not (qos) | only works with subscribe
)


data class MQTTProperties(
    val returnCode: MQTTError?,  // Error code
    val reasonString: String?,  // Error message
    val newServerURI: String?,  // How to reconnect to the broker
    val contentType: String? = null,  // Content type of the message
    val responseTopic: String? = null,  // Topic to name for response message
    val assignedClientIdentifier: String? = null,  // Assigned client identifier
    val authenticationMethod: String? = null,  // Authentication method
    val responseInfo: String? = null  // Response information
)
