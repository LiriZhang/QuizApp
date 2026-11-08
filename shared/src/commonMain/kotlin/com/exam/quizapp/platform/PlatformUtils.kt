package com.exam.quizapp.platform

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

object TextCacheManager {
    private val memoryCache = mutableMapOf<String, String>()

    fun get(url: String): String? = memoryCache[url]

    fun put(url: String, text: String) {
        if (text.isNotBlank()) {
            memoryCache[url] = text
        }
    }

    fun clear() {
        memoryCache.clear()
    }
}

@Composable
expect fun PlatformVideoPlayer(videoUrl: String, modifier: Modifier = Modifier)

@Composable
expect fun PlatformAudioPlayer(audioUrl: String, modifier: Modifier = Modifier)

/*@Composable
expect fun PlatformBannerAd(modifier: Modifier = Modifier)*/

@Composable
expect fun ImageFromUrl(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
)

@Composable
expect fun ZoomableImageFromUrl(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
)

expect fun playSound(isCorrect: Boolean)

/*expect fun playPlatformSound(isCorrect: Boolean)*/

expect fun playVoice(isCorrect: Boolean)

expect fun triggerVibration()

expect suspend fun fetchTextFromUrl(url: String): String

expect fun getDeviceId(): String


@Composable
fun getStatusBarHeight(): Dp {
    val density = LocalDensity.current
    return with(density) {
        WindowInsets.statusBars.getTop(this).toDp()
    }
}

@Composable
fun getNavigationBarHeight(): Dp {
    val density = LocalDensity.current
    return with(density) {
        WindowInsets.navigationBars.getBottom(this).toDp()
    }
}

@Composable
expect fun getBannerAdHeight(): Dp

expect fun isUserLoggedIn(): Boolean

expect fun setUserLoggedIn(loggedIn: Boolean)
