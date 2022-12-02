package io.github.untactorder.kmqtt.clientwrap


actual fun getPlatformName(): String {
    return "Android_${android.os.Build.VERSION.SDK_INT}"
}
