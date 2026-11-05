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
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFoundation.AVAssetTrack
import platform.AVFoundation.AVMediaTypeVideo
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import platform.Foundation.NSURLSession
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.setHTTPMethod

@Composable
actual fun PlatformVideoPlayer(videoUrl: String, modifier: Modifier) {
    val url = remember(videoUrl) { NSURL.URLWithString(videoUrl) }
    val player: AVPlayer? = remember(url) { url?.let { AVPlayer(uRL = it) } }

    var aspectRatio by remember(videoUrl) { mutableFloatStateOf(16f / 9f) }

    LaunchedEffect(player) {
        while (player != null) {
            val asset = player.currentItem?.asset
            if (asset != null) {
                val size = asset.naturalSize()
                val w = size.useContents { width }
                val h = size.useContents { height }
                if (w > 0.0 && h > 0.0) {
                    aspectRatio = when {
                        w > h -> 16f / 9f
                        w == h -> 1f
                        else -> 9f / 16f
                    }
                    break
                }
            }
            delay(300)
        }
    }

    DisposableEffect(player) {
        player?.play()
        onDispose {
            player?.pause()
        }
    }

    Box(
        modifier = modifier.aspectRatio(aspectRatio),
        contentAlignment = Alignment.Center
    ) {
        UIKitViewController(
            factory = {
                AVPlayerViewController().apply {
                    this.player = player
                    this.showsPlaybackControls = true
                }
            },
            update = { controller ->
                if (controller.player != player) {
                    controller.player = player
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
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
        // Cấu hình AVAudioSession để iOS cho phép phát âm thanh ngay cả ở chế độ silent
        try {
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.setCategory(
                AVAudioSessionCategoryPlayback,
                error = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val lastSlash = filename.lastIndexOf('/')
        val name = if (lastSlash >= 0) filename.substring(lastSlash + 1, filename.lastIndexOf('.')) else filename.substringBefore('.')
        val ext = if (filename.contains('.')) filename.substringAfterLast('.') else "mp3"

        // Tìm kiếm file resource trong các thư mục bundle của iOS
        val path = NSBundle.mainBundle.pathForResource(name, ext, inDirectory = "compose-resources/files")
            ?: NSBundle.mainBundle.pathForResource(name, ext, inDirectory = "compose-resources")
            ?: NSBundle.mainBundle.pathForResource(name, ext)

        if (path != null) {
            val url = NSURL.fileURLWithPath(path)
            val player = AVAudioPlayer(contentsOfURL = url, error = null).apply {
                prepareToPlay()
                play()
            }
            activeAudioPlayers.add(player)
            // Giới hạn danh sách player để tránh rò rỉ bộ nhớ nhưng vẫn giữ lại các player đang phát
            if (activeAudioPlayers.size > 10) {
                activeAudioPlayers.removeAt(0)
            }
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
    try {
        val generator = UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
        generator.prepare()
        generator.impactOccurred()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
private val ktorClient by lazy {
    HttpClient(Darwin) {
        expectSuccess = false

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
    }
}


actual suspend fun fetchTextFromUrl(url: String): String {

    println("NATIVE_TEST_0: START")

    require(url.startsWith("https://")) {
        "URL không hợp lệ"
    }

    val nsUrl = NSURL.URLWithString(url)
        ?: throw IllegalArgumentException("Không tạo được URL")

    val request = NSMutableURLRequest.requestWithURL(nsUrl).apply {
        setHTTPMethod("GET")
        setTimeoutInterval(30.0)
    }

    println("NATIVE_TEST_1: REQUEST CREATED")

    return suspendCancellableCoroutine { continuation ->

        val task = NSURLSession.sharedSession.dataTaskWithRequest(request) {
                data, response, error ->

            try {
                println("NATIVE_TEST_2: CALLBACK RECEIVED")

                // Kiểm tra lỗi kết nối
                if (error != null) {
                    throw IllegalStateException(
                        "NETWORK ERROR: ${error.localizedDescription}"
                    )
                }

                // Kiểm tra HTTP response
                val httpResponse = response as? NSHTTPURLResponse
                    ?: throw IllegalStateException(
                        "Không nhận được HTTP response"
                    )

                println("NATIVE_TEST_3: HTTP ${httpResponse.statusCode}")

                if (httpResponse.statusCode < 200L ||
                    httpResponse.statusCode >= 300L
                ) {
                    throw IllegalStateException(
                        "HTTP ERROR: ${httpResponse.statusCode}"
                    )
                }

                // Kiểm tra dữ liệu
                val responseData = data
                    ?: throw IllegalStateException("Không có dữ liệu TXT")

                println("NATIVE_TEST_4: DATA RECEIVED")

                // Giải mã UTF-8 bằng Foundation của Apple
                val nsText = NSString.create(
                    data = responseData,
                    encoding = NSUTF8StringEncoding
                ) ?: throw IllegalStateException(
                    "Không thể giải mã nội dung UTF-8"
                )

                val result = nsText.toString()
                    .removePrefix("\uFEFF")
                    .trim()

                if (result.isBlank()) {
                    throw IllegalStateException("Nội dung TXT trống")
                }

                println("NATIVE_TEST_5: DECODE SUCCESS")
                println("NATIVE_TEST_LENGTH: ${result.length}")

                if (continuation.isActive) {
                    continuation.resume(result)
                }

            } catch (e: Exception) {

                println("NATIVE_TEST_ERROR: ${e.message}")
                e.printStackTrace()

                if (continuation.isActive) {
                    continuation.resumeWithException(e)
                }
            }
        }

        // Nếu màn hình bị hủy hoặc coroutine bị hủy,
        // hủy luôn yêu cầu mạng.
        continuation.invokeOnCancellation {
            task.cancel()
        }

        println("NATIVE_TEST_6: TASK RESUME")

        task.resume()
    }
}
/*actual suspend fun fetchTextFromUrl(url: String): String {
    println("IOS_TEST_0: START")

    try {
        // 1. Kiểm tra URL
        require(url.startsWith("https://")) {
            "URL không hợp lệ"
        }

        // 2. Gửi yêu cầu đến máy chủ
        println("IOS_TEST_1: SENDING REQUEST")

        val response = ktorClient.get(url)

        // 3. Nhận phản hồi HTTP
        println("IOS_TEST_2: RESPONSE RECEIVED")
        println("HTTP STATUS: ${response.status.value}")
        println("CONTENT TYPE: ${response.headers[HttpHeaders.ContentType]}")
        println("CONTENT LENGTH: ${response.headers[HttpHeaders.ContentLength]}")

        if (response.status.value !in 200..299) {
            throw IllegalStateException(
                "HTTP ERROR: ${response.status.value}"
            )
        }

        // 4. Đọc dữ liệu dạng byte
        println("IOS_TEST_3: READING BYTES")

        val bytes = response.bodyAsBytes()

        println("IOS_TEST_4: BYTES RECEIVED")
        println("BYTE COUNT: ${bytes.size}")

        if (bytes.isEmpty()) {
            throw IllegalStateException("File TXT rỗng")
        }

        // 5. Giải mã UTF-8
        println("IOS_TEST_5: DECODING UTF-8")

        val text = bytes.decodeToString(
            throwOnInvalidSequence = true
        ).removePrefix("\uFEFF").trim()

        println("IOS_TEST_6: DECODE SUCCESS")
        println("TEXT LENGTH: ${text.length}")

        if (text.isBlank()) {
            throw IllegalStateException("Nội dung TXT trống")
        }

        println("IOS_TEST_7: COMPLETE")

        return text

    } catch (e: CancellationException) {
        println("IOS_TEST_CANCELLED")
        throw e

    } catch (e: Exception) {
        println("IOS_TEST_ERROR: ${e.message}")
        e.printStackTrace()
        throw e
    }
}
private val ktorClient by lazy {
    HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 15_000
        }
    }
}

actual suspend fun fetchTextFromUrl(url: String): String {
    return try {
        val response = ktorClient.get(url)
        if (response.status.value in 200..299) {
            val text = response.bodyAsText()
            text.trim()
        } else {
            ""
        }
    } catch (e: Throwable) {
        // Bắt mọi Throwable (bao gồm cả NSException hoặc Native crash từ CFNetwork)
        e.printStackTrace()
        ""
    }
}*/

@Composable
actual fun getBannerAdHeight(): Dp = 0.dp
