package io.github.untactorder.kmqtt.clientwrap


actual fun Any.getKMQTTClient(
    brokerURI: BrokerURI,
    connectionOptions: MQTTConnectOptions,
    statusManager: MQTTStatusManager
): KMQTTClientWrapper = KMQTTClientWrapper(brokerURI, connectionOptions, statusManager)


actual class KMQTTClientWrapper(
    val brokerURI: BrokerURI,
    val connectionOptions: MQTTConnectOptions,
    private var statusManager: MQTTStatusManager
) {
    private var client: PahoMQTTClient = PahoMQTTClient(brokerURI, connectionOptions, statusManager)

    actual val isConnected: Boolean = client.isConnected

    actual suspend fun connect() {
        client.connect()
    }

    actual suspend fun setNewStatusManager(manager: MQTTStatusManager) {
        statusManager = manager
        client.setNewStatusManager(statusManager)
    }

    actual suspend fun publish(topic: String, payload: MQTTPacketPayload, timeout: Int) {
        client.publish(topic, payload, timeout)
    }

    actual suspend fun subscribe(topic: String, qos: MQTTQoS) {
        client.subscribe(topic, qos)
    }

    actual suspend fun unsubscribe(vararg topics: String) {
        client.unsubscribe(*topics)
    }

    actual suspend fun disconnect() {
        client.disconnect()
    }
}