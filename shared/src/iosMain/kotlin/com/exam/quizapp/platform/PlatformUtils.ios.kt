@file:OptIn(ExperimentalForeignApi::class)

package com.exam.quizapp.platform

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.asset
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.rate
import platform.AVFoundation.seekToTime
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

@Composable
actual fun PlatformVideoPlayer(videoUrl: String, modifier: Modifier) {
    val url = remember(videoUrl) { NSURL.URLWithString(videoUrl) }
    val player: AVPlayer? = remember(url) { url?.let { AVPlayer(uRL = it) } }

    DisposableEffect(player) {
        player?.play()
        onDispose {
            player?.pause()
        }
    }

    UIKitViewController(
        factory = {
            AVPlayerViewController().apply {
                this.player = player
                this.showsPlaybackControls = true
            }
        },
        modifier = modifier
    )
}

@Composable
actual fun PlatformAudioPlayer(audioUrl: String, modifier: Modifier) {
    val url = remember(audioUrl) { NSURL.URLWithString(audioUrl) }
    val player: AVPlayer? = remember(url) { url?.let { AVPlayer(uRL = it) } }

    var isPlaying by remember(audioUrl) { mutableStateOf(false) }
    var currentSpeed by remember(audioUrl) { mutableFloatStateOf(1.0f) }
    var progress by remember(audioUrl) { mutableFloatStateOf(0f) }
    var durationMs by remember(audioUrl) { mutableLongStateOf(0L) }
    var currentPositionMs by remember(audioUrl) { mutableLongStateOf(0L) }

    DisposableEffect(player) {
        onDispose {
            player?.pause()
        }
    }

    LaunchedEffect(isPlaying, player) {
        while (isPlaying && player != null) {
            val currentSeconds = CMTimeGetSeconds(player.currentTime())
            if (!currentSeconds.isNaN() && currentSeconds >= 0) {
                currentPositionMs = (currentSeconds * 1000).toLong()
            }
            player.currentItem?.asset?.duration?.let { dur ->
                val durSeconds = CMTimeGetSeconds(dur)
                if (!durSeconds.isNaN() && durSeconds > 0) {
                    durationMs = (durSeconds * 1000).toLong()
                }
            }
            if (durationMs > 0) {
                progress = (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
            }
            delay(100)
        }
    }

    val speeds = listOf(0.8f, 1.0f, 1.25f, 1.5f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 3.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    if (isPlaying) {
                        player?.pause()
                        isPlaying = false
                    } else {
                        player?.play()
                        player?.rate = currentSpeed
                        isPlaying = true
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            IconButton(
                onClick = {
                    player?.seekToTime(CMTimeMakeWithSeconds(0.0, 1))
                    player?.play()
                    player?.rate = currentSpeed
                    isPlaying = true
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "Replay",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatAudioTime(currentPositionMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatAudioTime(durationMs),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                onClick = {
                    val currentIndex = speeds.indexOf(currentSpeed)
                    val nextIndex = (if (currentIndex != -1) currentIndex + 1 else 0) % speeds.size
                    val nextSpeed = speeds[nextIndex]
                    currentSpeed = nextSpeed
                    if (isPlaying) {
                        player?.rate = nextSpeed
                    }
                },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(start = 2.dp)
            ) {
                Text(
                    text = "${if (currentSpeed % 1.0f == 0f) currentSpeed.toInt().toString() else currentSpeed.toString()}x",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun formatAudioTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
}

@Composable
actual fun PlatformBannerAd(modifier: Modifier) {
    // iOS Banner Ad stub (excluded as requested)
}

actual fun playPlatformSound(isCorrect: Boolean) {
    playSound(isCorrect)
}

private val activeAudioPlayers = mutableListOf<AVAudioPlayer>()

private fun playSoundFile(filename: String) {
    try {
        val lastSlash = filename.lastIndexOf('/')
        val name = if (lastSlash >= 0) filename.substring(lastSlash + 1, filename.lastIndexOf('.')) else filename.substringBefore('.')
        val ext = if (filename.contains('.')) filename.substringAfterLast('.') else "mp3"

        val path = NSBundle.mainBundle.pathForResource(name, ext, inDirectory = "compose-resources/files")
            ?: NSBundle.mainBundle.pathForResource(name, ext)
        if (path != null) {
            val url = NSURL.fileURLWithPath(path)
            val player = AVAudioPlayer(contentsOfURL = url, error = null).apply {
                prepareToPlay()
                play()
            }
            activeAudioPlayers.add(player)
            activeAudioPlayers.removeAll { !it.isPlaying() }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

actual fun playSound(isCorrect: Boolean) {
    val fileName = if (isCorrect) "files/ting_nhe.mp3" else "files/te.mp3"
    playSoundFile(fileName)
}

actual fun playVoice(isCorrect: Boolean) {
    val fileName = if (isCorrect) "files/ting.mp3" else "files/at_o.mp3"
    playSoundFile(fileName)
}

actual fun triggerVibration() {
    val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
    generator.prepare()
    generator.impactOccurred()
}

private val ktorClient by lazy { HttpClient() }

actual suspend fun fetchTextFromUrl(url: String): String {
    return try {
        ktorClient.get(url).bodyAsText()
    } catch (e: Exception) {
        ""
    }
}

@Composable
actual fun getBannerAdHeight(): Dp = 0.dp
