package io.github.untactorder.test.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * @see <a href="URL">https://medium.com/mobile-app-development-publication/android-jetpack-compose-inset-padding-made-easy-5f156a790979</a>
 * @see <a href="URL">https://google.github.io/accompanist/insets/</a>
 */
@Preview
@Composable
fun AndroidApp() {
    App(Modifier.fillMaxSize().systemBarsPadding())
}
