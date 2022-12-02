package io.github.untactorder.kmqtt.clientwrap

import android.content.Context
import android.content.Intent
import io.github.untactorder.kmqtt.clientwrap.AndroidKMQTTClientService.Companion as ClientService


/**
 * @throws IllegalArgumentException when this function is called from non Android Context environment.
 */
actual fun Any.getKMQTTClient(
    brokerURI: BrokerURI,
    connectionOptions: MQTTConnectOptions,
    statusManager: MQTTStatusManager
): KMQTTClientWrapper {
    val context = this
    if (context !is Context) {
        throw IllegalArgumentException("This function must be called from Android Context.")
    }
    return KMQTTClientWrapper(brokerURI, connectionOptions, statusManager, context)
}


actual class KMQTTClientWrapper(
    val brokerURI: BrokerURI,
    val connectionOptions: MQTTConnectOptions,
    private var statusManager: MQTTStatusManager,
    context: Context
) {
    private val client: PahoMQTTClient

    init {
        val uri = brokerURI.toString(connectionOptions.tlsConfig.tlsEnabled)
        val registered = AndroidKMQTTClientService.getClient(uri)
        client = registered ?: PahoMQTTClient(brokerURI, connectionOptions, statusManager)
        if (registered == null) {
            AndroidKMQTTClientService.registerClient(uri, client)
            val intent = Intent(context, AndroidKMQTTClientService::class.java)
            intent.putExtra(ClientService.INTENT_STR_BROKER_URI, uri)
            context.startService(intent)
        }
    }

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