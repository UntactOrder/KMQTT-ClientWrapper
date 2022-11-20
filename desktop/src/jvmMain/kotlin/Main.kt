import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.untactorder.test.common.DesktopApp

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        undecorated = false
    ) {
        DesktopApp()
    }
}
