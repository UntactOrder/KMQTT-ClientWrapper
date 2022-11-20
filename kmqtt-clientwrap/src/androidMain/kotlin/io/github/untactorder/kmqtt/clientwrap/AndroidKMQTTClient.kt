package io.github.untactorder.kmqtt.clientwrap

import android.content.Intent


class AndroidKMQTTClient {
    val intent = Intent(context, MyService::class.java)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

actual class KMQTTClientWrapper {
    val intent = Intent(context, AndroidKMQTTClientService::class.java)
}