package io.github.untactorder.kmqtt.clientwrap

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


/**
 * This is a service that is used to keep the app alive in the background.
 * This is required for the MQTT client to work properly.
 * @throw IllegalArgumentException
 */
open class AndroidKMQTTClientService : Service() {
    companion object {
        private val clientMap: MutableMap<String, AndroidKMQTTClient> = mutableMapOf()
        fun getClient(brokerURI: String) = clientMap[brokerURI]

        fun registerClient(brokerURI: String, client: AndroidKMQTTClient) {
            if (clientMap.containsKey(brokerURI)) {
                throw IllegalArgumentException("Client for $brokerURI already exists")
            }
            clientMap[brokerURI] = client
        }

        const val INTENT_STR_CHANNEL_ID = "channel_id"
        const val INTENT_INT_NOTIFICATION_ID = "notification_id"
        const val INTENT_STR_NOTIFICATION_TITLE = "notification_title"
        const val INTENT_STR_NOTIFICATION_CONTENT = "notification_content"
        const val INTENT_STR_BROKER_URI = "broker_uri"
    }

    private var channelId = "android kmqtt client service"
    private var notificationId = 8883
    private var notificationTitle = "Android KMQTT Client Service"
    private var notificationContent = "Android KMQTT Client Service is running."
    private lateinit var brokerURI: String
    private lateinit var client: AndroidKMQTTClient

    override fun onCreate() {
        super.onCreate()
        Log.d(this::class.simpleName, "MQTT Client Service is created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!::client.isInitialized) {
            intent?.let {
                channelId = it.getStringExtra(INTENT_STR_CHANNEL_ID) ?: channelId
                notificationId = it.getIntExtra(INTENT_INT_NOTIFICATION_ID, notificationId)
                notificationTitle = it.getStringExtra(INTENT_STR_NOTIFICATION_TITLE) ?: notificationTitle
                notificationContent = it.getStringExtra(INTENT_STR_NOTIFICATION_CONTENT) ?: notificationContent
                brokerURI = it.getStringExtra(INTENT_STR_BROKER_URI)
                    ?: throw IllegalArgumentException("Broker URI is not specified.")
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
                val notification = NotificationCompat.Builder(this, channelId)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationContent)
                    .build()
                Log.d(this::class.simpleName, "Foreground MQTT Client Service is started.")
                startForeground(notificationId, notification)
            } else {
                Log.d(this::class.simpleName, "Background MQTT Client Service is started.")
            }

            client = getClient(brokerURI) ?: throw IllegalArgumentException("Client is not registered.")
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            channelId,
            notificationTitle,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.description = notificationContent

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        client.disconnect()
        clientMap.remove(brokerURI)
        Log.d(this::class.simpleName, "MQTT Client Service is destroyed.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}