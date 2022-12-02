package io.github.untactorder.kmqtt.clientwrap

/* import MQTT 3 Exception */
import io.github.untactorder.kmqtt.clientwrap.MQTTVersion.*
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
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence as MemoryPersistence3
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence as Mqtt3DefaultFilePersistence
import org.eclipse.paho.client.mqttv3.MqttClient as PahoMqtt3Client
import org.eclipse.paho.client.mqttv3.MqttConnectOptions as PahoConnectOptions3
/* import MQTT 5 Client */
import org.eclipse.paho.mqttv5.client.IMqttToken as IMqtt5Token
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse as Mqtt5DisconnectResponse
import org.eclipse.paho.mqttv5.client.MqttCallback as PahoMqtt5Callback
import org.eclipse.paho.mqttv5.common.MqttMessage as PahoMqtt5Message
import org.eclipse.paho.mqttv5.common.packet.MqttProperties as Mqtt5Properties
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence as MemoryPersistence5
import org.eclipse.paho.mqttv5.client.persist.MqttDefaultFilePersistence as Mqtt5DefaultFilePersistence
import org.eclipse.paho.mqttv5.client.MqttClient as PahoMqtt5Client
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions as PahoConnectOptions5

import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext


class PahoMQTTClient(
    val brokerURI: BrokerURI,
    val connectionOptions: MQTTConnectOptions,
    private var statusManager: MQTTStatusManager,
    persistenceType: Int = PERSISTENCE_NONE
) {
    companion object {
        const val PERSISTENCE_NONE = 0  // no auto save
        const val PERSISTENCE_DB = 1  // data auto save to database file
        const val PERSISTENCE_RAM = 2  // data auto save to ram
    }

    private val client: Any

    init {
        val version = connectionOptions.mqttVersion
        val uri = brokerURI.toString(connectionOptions.tlsConfig.tlsEnabled)
        client = when (version) {
            VERSION_3_1_1 -> PahoMqtt3Client(uri, connectionOptions.clientId, when (persistenceType) {
                PERSISTENCE_DB -> Mqtt3DefaultFilePersistence()
                PERSISTENCE_RAM -> MemoryPersistence3()
                else -> null
            })
            VERSION_5 -> PahoMqtt5Client(uri, connectionOptions.clientId, when (persistenceType) {
                PERSISTENCE_DB -> Mqtt5DefaultFilePersistence()
                PERSISTENCE_RAM -> MemoryPersistence5()
                else -> null
            })
        }
    }

    val isConnected: Boolean
        get() = when (client) {
            is PahoMqtt5Client -> client.isConnected
            is PahoMqtt3Client -> client.isConnected
            else -> false
        }

    suspend fun connect() {
        try {
            if (client is PahoMqtt3Client) {
                client.connect(connectionOptions.toPahoConnectOption3())
                client.setCallback(statusManager)
            } else if (client is PahoMqtt5Client) {
                client.connect(connectionOptions.toPahoConnectOption5())
                client.setCallback(statusManager)
            }
        } catch (e: Mqtt3Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while connecting.", e.cause) else throw e
        } catch (e: Mqtt5Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while connecting.", e.cause) else throw e
        }
    }

    suspend fun setNewStatusManager(manager: MQTTStatusManager) {
        statusManager = manager
        if (client is PahoMqtt3Client) client.setCallback(statusManager)
        else if (client is PahoMqtt5Client) client.setCallback(statusManager)
    }

    suspend fun publish(topic: String, payload: MQTTPacketPayload, timeout: Int) {
        try {
            if (client is PahoMqtt3Client) {
                client.publish(topic, payload.value.toByteArray(), payload.qos.level, payload.retained)
            } else if (client is PahoMqtt5Client) {
                client.publish(topic, payload.value.toByteArray(), payload.qos.level, payload.retained)
            }
        } catch (e: Mqtt3Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while publishing.", e.cause) else throw e
        } catch (e: Mqtt5Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while publishing.", e.cause) else throw e
        }
    }

    suspend fun subscribe(topic: String, qos: MQTTQoS) {
        try {
            if (client is PahoMqtt3Client) {
                client.subscribe(topic, qos.level)
            } else if (client is PahoMqtt5Client) {
                client.subscribe(topic, qos.level)
            }
        } catch (e: Mqtt3Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while subscribing.", e.cause) else throw e
        } catch (e: Mqtt5Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while subscribing.", e.cause) else throw e
        }
    }

    suspend fun unsubscribe(vararg topics: String) {
        try {
            if (client is PahoMqtt3Client) {
                client.unsubscribe(topics)
            } else if (client is PahoMqtt5Client) {
                client.unsubscribe(topics)
            }
        } catch (e: Mqtt3Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while unsubscribing.", e.cause) else throw e
        } catch (e: Mqtt5Exception) {
            val errorCode = MQTTError.valueOf(e.reasonCode)
            if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while unsubscribing.", e.cause) else throw e
        }
    }

    suspend fun disconnect() {
        if (isConnected) {
            try {
                if (client is PahoMqtt3Client) {
                    client.disconnect()
                    client.setCallback(null)
                } else if (client is PahoMqtt5Client) {
                    client.disconnect()
                    client.setCallback(null)
                }
            } catch (e: Mqtt3Exception) {
                val errorCode = MQTTError.valueOf(e.reasonCode)
                if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while disconnecting.", e.cause) else throw e
            } catch (e: Mqtt5Exception) {
                val errorCode = MQTTError.valueOf(e.reasonCode)
                if (errorCode is MQTTError) throw MQTTException(errorCode, e.message ?: "An error occurred while disconnecting.", e.cause) else throw e
            }
        }
    }
}


fun MQTTConnectOptions.toPahoConnectOption5(): PahoConnectOptions5 {
    val conn = this
    return PahoConnectOptions5().apply {
        if (username.isNotEmpty()) userName = username
        if (conn.password.isNotEmpty()) password = conn.password.toByteArray()
        //if (tlsConfig.tlsEnabled) socketFactory = tlsConfig.toSSLContext().socketFactory
        isCleanStart = cleanStart
        willMessage = conn.willMessage
    }
}
/*
fun TLSCertConfig.toSSLContext(): SSLContext {
    val reader = PEMParser(caCertPhrase)
    val cert = JcaX509CertificateConverter().setProvider("BC")
            .getCertificate(reader.readObject() as org.gradle.internal.impldep.org.bouncycastle.cert.X509CertificateHolder)
    reader.close()
// client key and certificates are sent to server so it can authenticate us

    val ks = KeyStore.getInstance(KeyStore.getDefaultType())
    ks.load(null, null)

    val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
    kmf.init(ks, password.toCharArray())

    val tmf: TrustManagerFactory
    tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
    tmf.init(ks)


    val ssl = SSLContext.getInstance("SSL")
    ssl.init()


        /* Step 1 initialize SSL context */
        val sslContext: SSLContext
        sslContext = SSLContext.getInstance("SSL")
        sslContext.init(kmf.keyManagers, tmf.trustManagers, SecureRandom())

        val sslOptions = MqttConnectionOptions()
        sslOptions.setCleanStart(false)
        sslOptions.setSocketFactory(sslContext.socketFactory)
}
*/