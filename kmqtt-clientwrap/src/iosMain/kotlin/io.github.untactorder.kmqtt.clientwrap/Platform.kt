package io.github.untactorder.kmqtt.clientwrap

import platform.UIKit.UIDevice


actual fun getPlatformName(): String {
    return UIDevice.currentDevice.systemName() + "_" + UIDevice.currentDevice.systemVersion
}
