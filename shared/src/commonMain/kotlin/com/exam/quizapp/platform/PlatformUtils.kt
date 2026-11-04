package com.exam.quizapp.platform

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
expect fun PlatformVideoPlayer(videoUrl: String, modifier: Modifier = Modifier)

@Composable
expect fun PlatformAudioPlayer(audioUrl: String, modifier: Modifier = Modifier)

@Composable
expect fun PlatformBannerAd(modifier: Modifier = Modifier)
expect fun playSound(isCorrect: Boolean)

expect fun playPlatformSound(isCorrect: Boolean)

expect fun playVoice(isCorrect: Boolean)

expect fun triggerVibration()

expect suspend fun fetchTextFromUrl(url: String): String

/*fun normalizeText(input: String): String {
    val srcM = "áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵđ"
    val desM = "aaaaaaaaaaaaaaaaaeeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyyyd"
    var result = input.lowercase()
    for (i in srcM.indices) {
        result = result.replace(srcM[i], desM[i])
        result = result.replace(srcM[i].uppercaseChar(), desM[i])
    }
    return result.replace(Regex("\\s+"), " ").trim()
}*/

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
