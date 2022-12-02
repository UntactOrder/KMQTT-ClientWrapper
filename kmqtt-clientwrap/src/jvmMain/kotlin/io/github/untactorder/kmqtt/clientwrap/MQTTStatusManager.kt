package io.github.untactorder.kmqtt.clientwrap

/* import MQTT 3 Exception */
import org.eclipse.paho.client.mqttv3.MqttException as Mqtt3Exception
import org.eclipse.paho.client.mqttv3.MqttSecurityException as Mqtt3SecurityException
import org.eclipse.paho.client.mqttv3.MqttPersistenceException as Mqtt3PersistenceException
/* import MQTT 5 Exception */
import org.eclipse.paho.mqttv5.common.MqttException as Mqtt5Exception
import org.eclipse.paho.mqttv5.common.MqttSecurityException as Mqtt5SecurityException
import org.eclipse.paho.mqttv5.common.MqttPersistenceException as Mqtt5PersistenceException
/* import MQTT 3 Client */
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken as IMqtt3DeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback as PahoMqtt3Callback
import org.eclipse.paho.client.mqttv3.MqttMessage as PahoMqtt3Message
/* import MQTT 5 Client */
import org.eclipse.paho.mqttv5.client.IMqttToken as IMqtt5Token
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse as Mqtt5DisconnectResponse
import org.eclipse.paho.mqttv5.client.MqttCallback as PahoMqtt5Callback
import org.eclipse.paho.mqttv5.common.MqttMessage as PahoMqtt5Message
import org.eclipse.paho.mqttv5.common.packet.MqttProperties as Mqtt5Properties


actual open class MQTTStatusManager actual constructor(
    val onConnectComplete: (reconnect: Boolean, serverURI: String?) -> Unit,
    val onMessageArrived: (topic: String, message: MQTTPacketPayload) -> Unit,
    val onConnectionLost: (properties: MQTTProperties) -> Unit,
    val onTopicPublishSuccess: (messageId: Int) -> Unit,
    val onErrorOccurred: (exception: MQTTException?) -> Unit,
    val onAuthPacketsReceived: (properties: MQTTProperties) -> Unit
) : PahoMqtt3Callback, PahoMqtt5Callback {

    /* Callbacks for Paho Java MQTT 5.0 Library */
    override fun messageArrived(topic: String, msg: PahoMqtt3Message) {
        onMessageArrived(topic, msg.toPacketPayload())
    }
    override fun connectionLost(cause: Throwable) {
        onConnectionLost(MQTTProperties(null, cause.message, null))
    }
    override fun deliveryComplete(token: IMqtt3DeliveryToken) {
        onTopicPublishSuccess(token.messageId)
    }

    /* Callbacks for Paho Java MQTT 5.0 Library */
    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        onConnectComplete(reconnect, serverURI)
    }
    override fun messageArrived(topic: String, msg: PahoMqtt5Message) {
        onMessageArrived(topic, msg.toPacketPayload())
    }
    override fun disconnected(disconnectResponse: Mqtt5DisconnectResponse?) {
        onConnectionLost(MQTTProperties(
            disconnectResponse?.returnCode?.let { MQTTError.valueOf(it) },
            disconnectResponse?.reasonString,
            disconnectResponse?.serverReference
        ))
    }
    override fun deliveryComplete(token: IMqtt5Token?) {
        token?.let { onTopicPublishSuccess(it.messageId) }
    }
    override fun mqttErrorOccurred(exception: Mqtt5Exception?) {
        onErrorOccurred(MQTTError.valueOf(exception?.reasonCode ?: 0)
            ?.let { MQTTException(it, exception?.message, exception?.cause) })
    }
    override fun authPacketArrived(reasonCode: Int, properties: Mqtt5Properties?) {
        onAuthPacketsReceived(MQTTProperties(
            MQTTError.valueOf(reasonCode), properties?.reasonString, properties?.serverReference,
            properties?.contentType, properties?.responseTopic, properties?.assignedClientIdentifier,
            properties?.authenticationMethod, properties?.responseInfo
        ))
    }
}


fun PahoMqtt3Message.toPacketPayload() = MQTTPacketPayload(
    this.payload.decodeToString(), this.id, MQTTQoS.valueOf(this.qos), this.isRetained, this.isDuplicate)
fun PahoMqtt5Message.toPacketPayload() = MQTTPacketPayload(
    this.payload.decodeToString(), this.id, MQTTQoS.valueOf(this.qos), this.isRetained, this.isDuplicate)
