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

expect suspend fun fetchTextFromUrl2(url: String): String
expect suspend fun fetchTextFromUrl3(url: String): String

expect suspend fun uploadQuizResultToDrive(
    url: String,
    phone: String,
    quizName: String,
    score: Int,
    totalQuestions: Int,
    resultId: Int,
    fileContent: String
): String

expect fun getDeviceId(): String

expect fun getLoggedInPhone(): String

expect fun setLoggedInPhone(phone: String)


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

/**
 * Hàm mã hóa URL component chuẩn UTF-8 tương thích trên cả Android, iOS và các nền tảng khác.
 */
fun encodeURLComponent(value: String): String {
    val bytes = value.encodeToByteArray()
    return buildString {
        for (b in bytes) {
            val u = b.toInt() and 0xFF
            val c = u.toChar()
            if (c in 'a'..'z' || c in 'A'..'Z' || c in '0'..'9' || c in "-_.~") {
                append(c)
            } else {
                append('%')
                val hex = u.toString(16).uppercase()
                if (hex.length < 2) append('0')
                append(hex)
            }
        }
    }
}

