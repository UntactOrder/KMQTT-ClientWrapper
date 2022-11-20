package io.github.untactorder.test.common

actual fun getPlatformName(): String {
    return "Android ${android.os.Build.VERSION.SDK_INT}"
}
