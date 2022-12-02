package io.github.untactorder.kmqtt.clientwrap

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.digieng.kmqtt.client.R


/**
 * This is a service that is used to keep the app alive in the background.
 * This is required for the MQTT client to work properly.
 * - All Companion Objects should be modified before the Service is instantiated if you want to change them. -
 * @throw IllegalArgumentException when PahoMQTTClient is not registered before the Service is started.
 */
open class AndroidKMQTTClientService : Service() {
    companion object {
        private val clientMap: MutableMap<String, PahoMQTTClient> = mutableMapOf()
        fun getClient(brokerURI: String) = clientMap[brokerURI]

        fun registerClient(brokerURI: String, client: PahoMQTTClient) {
            if (clientMap.containsKey(brokerURI)) {
                throw IllegalArgumentException("Client for $brokerURI already exists")
            }
            clientMap[brokerURI] = client
        }

        var CHANNEL_ID = "Android KMQTT Client Foreground Service"
        var CHANNEL_NAME = "Android KMQTT Client Foreground Service"
        var CHANNEL_DESCRIPTION = "This is a service that is used to keep the app alive in the background. This is required for the MQTT client to work properly."
        var NOTIFICATION_ID = 8883
        var NOTIFICATION_TITLE = "Android KMQTT Client Service"
        var NOTIFICATION_CONTENT = "Android KMQTT Client Service is running."
        var NOTIFICATION_SMALLICON = R.drawable.router_fill1_wght600_grad0_opsz48

        const val INTENT_STR_BROKER_URI = "broker_uri"
    }

    private var isAlreadyStarted = false

    override fun onCreate() {
        super.onCreate()
        Log.d(this::class.simpleName, "MQTT Client Service is created.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isAlreadyStarted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
                val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(NOTIFICATION_TITLE)
                    .setContentText(NOTIFICATION_CONTENT)
                    .setSmallIcon(NOTIFICATION_SMALLICON)
                    .build()
                Log.d(this::class.simpleName, "Foreground MQTT Client Service is started.")
                startForeground(NOTIFICATION_ID, notification)
            } else {
                Log.d(this::class.simpleName, "Background MQTT Client Service is started.")
            }
        }
        isAlreadyStarted = true

        intent?.let {
            val brokerURI = it.getStringExtra(INTENT_STR_BROKER_URI)
            if (brokerURI != null) {
                getClient(brokerURI) ?: throw IllegalArgumentException("Client is not registered.")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.enableLights(false)
        notificationChannel.enableVibration(false)
        notificationChannel.description = CHANNEL_DESCRIPTION

        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        isAlreadyStarted = false
        CoroutineScope(Dispatchers.IO).launch {
            clientMap.forEach {
                it.value.disconnect()
            }
        }
        clientMap.clear()
        Log.d(this::class.simpleName, "MQTT Client Service is destroyed.")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
