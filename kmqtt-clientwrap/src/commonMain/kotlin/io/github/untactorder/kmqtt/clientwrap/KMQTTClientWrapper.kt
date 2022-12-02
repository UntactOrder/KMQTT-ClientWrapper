package io.github.untactorder.kmqtt.clientwrap


expect fun Any.getKMQTTClient(
    brokerURI: BrokerURI,
    connectionOptions: MQTTConnectOptions,
    statusManager: MQTTStatusManager
): KMQTTClientWrapper


/**
 * Belows are expected params
 * @param brokerURI: The URI of the broker to connect to. if client uses SSL/TLS then the protocol should be "mqtts://where.the.broker.is:8883". (non tls: "mqtt://where.the.broker.is:1883")
 * @param connectionOptions: The connection options to use when connecting to the broker.
 * @param statusManager: The status manager to use when connecting to the broker.
 */
expect class KMQTTClientWrapper {
    val isConnected: Boolean  // if client is connected to broker then will be marked as true

    /**
     * Connect to MQTT Broker which is specified in constructor.
     * @throws MQTTException if connection failed.
     */
    suspend fun connect()

    /**
     * Set new callbacks to MQTT Client.
     */
    suspend fun setNewStatusManager(manager: MQTTStatusManager)

    /**
     * Publish a topic message to connected MQTT Broker.
     * @param topic The MQTT topic to be published.
     * @param payload MQTT Message payload to be published.
     * @param timeout Millisecond timeout to wait for publish to complete.
     * @throws MQTTException if publication failed.
     */
    suspend fun publish(topic: String, payload: MQTTPacketPayload, timeout: Int = 2000)

    /**
     * Subscribe to a topic of connected MQTT Broker.
     * @param topic The MQTT topic to be subscribed.
     * @param qos MQTT Quality of Service setting.
     * @throws MQTTException if subscription failed.
     */
    suspend fun subscribe(topic: String, qos: MQTTQoS = MQTTQoS.DEFAULT)

    /**
     * Unsubscribe to one or more topic(s) of connected MQTT Broker.
     * @param topics The MQTT topic(one or more) to be subscribed.
     * @throws MQTTException if un-subscription failed.
     */
    suspend fun unsubscribe(vararg topics: String)

    /**
     * Disconnect from MQTT Broker.
     * @throws MQTTException if operation failed.
     */
    suspend fun disconnect()
}
