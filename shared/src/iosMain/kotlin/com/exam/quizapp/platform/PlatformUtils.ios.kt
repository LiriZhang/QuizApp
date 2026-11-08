@file:OptIn(ExperimentalForeignApi::class)

package com.exam.quizapp.platform

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import androidx.compose.ui.interop.UIKitViewController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import com.exam.quizapp.shared.resources.Res
import com.exam.quizapp.shared.resources.placeholder1
import com.exam.quizapp.shared.resources.error_image
import platform.posix.memcpy
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image as SkiaImage
import kotlinx.cinterop.BetaInteropApi
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

import platform.Foundation.NSURLSession
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSString
import platform.Foundation.NSURLCache
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.setHTTPMethod
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice

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


private val activeAudioPlayers = mutableListOf<AVAudioPlayer>()

private fun findResourcePath(fileName: String): String? {
    try {
        val bundle = NSBundle.mainBundle
        val targetFileName = fileName.substringAfterLast('/') // ví dụ: "te.mp3"
        val nameWithoutExt = targetFileName.substringBefore('.') // ví dụ: "te"
        val ext = targetFileName.substringAfterLast('.') // ví dụ: "mp3"

        // 1. Try standard pathForResource với đúng tên và extension chính xác
        var path = bundle.pathForResource(nameWithoutExt, ext)
        if (path != null) return path

        // 2. Try searching in compose-resources
        path = bundle.pathForResource(nameWithoutExt, ext, inDirectory = "compose-resources/files")
        if (path != null) return path

        path = bundle.pathForResource(nameWithoutExt, ext, inDirectory = "compose-resources")
        if (path != null) return path

        // 3. Dynamic search in bundle using NSFileManager with EXACT filename matching
        val resourcePath = bundle.resourcePath ?: return null
        val enumerator = NSFileManager.defaultManager.enumeratorAtPath(resourcePath) ?: return null
        var element: Any? = enumerator.nextObject()
        while (element != null) {
            val subPath = element as? String
            if (subPath != null) {
                val currentFileName = subPath.substringAfterLast('/')
                // SO SÁNH TUYỆT ĐỐI (EXACT MATCH) THAY VÌ CHỨA KÝ TỰ (CONTAINS)
                if (currentFileName == targetFileName) {
                    return "$resourcePath/$subPath"
                }
            }
            element = enumerator.nextObject()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}

private fun playSoundFile(filename: String) {
    try {
        val audioSession = AVAudioSession.sharedInstance()
        audioSession.setCategory(
            AVAudioSessionCategoryPlayback,
            error = null
        )

        val path = findResourcePath(filename)
        if (path == null) {
            println("Không tìm thấy file audio trong bundle iOS: $filename")
            return
        }

        val url = NSURL.fileURLWithPath(path)
        val player = AVAudioPlayer(contentsOfURL = url, error = null).apply {
            prepareToPlay()
            play()
        }
        activeAudioPlayers.add(player)
        if (activeAudioPlayers.size > 10) {
            activeAudioPlayers.removeAt(0)
        }
    } catch (e: Exception) {
        println("Lỗi phát âm thanh iOS: ${e.message}")
        e.printStackTrace()
    }
}

actual fun playSound(isCorrect: Boolean) {
    val fileName = if (isCorrect) "files/ting_nhe.mp3" else "files/tetete.mp3"
    playSoundFile(fileName)
}

actual fun playVoice(isCorrect: Boolean) {
    val fileName = if (isCorrect) "files/ting.mp3" else "files/at_o.mp3"
    playSoundFile(fileName)
}

private val feedbackGenerator by lazy {
    UIImpactFeedbackGenerator(style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
}

actual fun triggerVibration() {
    try {
        feedbackGenerator.prepare()
        feedbackGenerator.impactOccurred()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
fun setupIosMediaCache() {
    try {
        val memoryCapacity = (50 * 1024 * 1024).toULong()
        val diskCapacity = (250 * 1024 * 1024).toULong()
        val cache = NSURLCache(
            memoryCapacity = memoryCapacity,
            diskCapacity = diskCapacity,
            diskPath = "ios_quiz_media_cache"
        )
        NSURLCache.setSharedURLCache(cache)
    } catch (e: Exception) {
        println("Lỗi cấu hình NSURLCache: ${e.message}")
    }
}

actual suspend fun fetchTextFromUrl(url: String): String {
    TextCacheManager.get(url)?.let { return it }

    require(url.startsWith("https://")) { "URL không hợp lệ" }

    val nsUrl = NSURL.URLWithString(url)
        ?: throw IllegalArgumentException("Không tạo được URL")

    val request = NSMutableURLRequest.requestWithURL(nsUrl).apply {
        setHTTPMethod("GET")
        setTimeoutInterval(30.0)
    }

    return suspendCancellableCoroutine { continuation ->
        val task = NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
            try {
                if (error != null) {
                    throw IllegalStateException("NETWORK ERROR: ${error.localizedDescription}")
                }
                val httpResponse = response as? NSHTTPURLResponse
                    ?: throw IllegalStateException("Không nhận được HTTP response")

                if (httpResponse.statusCode !in 200L..299L) {
                    throw IllegalStateException("HTTP ERROR: ${httpResponse.statusCode}")
                }
                val responseData = data ?: throw IllegalStateException("Không có dữ liệu TXT")

                @OptIn(BetaInteropApi::class)
                val nsText = NSString.create(data = responseData, encoding = NSUTF8StringEncoding)
                    ?: throw IllegalStateException("Không thể giải mã nội dung UTF-8")

                val result = nsText.toString().removePrefix("\uFEFF").trim()
                if (result.isBlank()) {
                    throw IllegalStateException("Nội dung TXT trống")
                }

                TextCacheManager.put(url, result)

                if (continuation.isActive) {
                    continuation.resume(result)
                }
            } catch (e: Exception) {
                if (continuation.isActive) {
                    continuation.resumeWithException(e)
                }
            }
        }
        continuation.invokeOnCancellation { task.cancel() }
        task.resume()
    }
}

@Composable
actual fun getBannerAdHeight(): Dp = getStatusBarHeight()

actual fun getDeviceId(): String {
    val defaults = NSUserDefaults.standardUserDefaults
    var deviceId = defaults.stringForKey("device_uuid")
    if (deviceId.isNullOrEmpty()) {
        deviceId = UIDevice.currentDevice.identifierForVendor?.UUIDString()
            ?: NSUUID().UUIDString()
        defaults.setObject(deviceId, forKey = "device_uuid")
    }
    return deviceId ?: "unknown_ios"
}

private object ImageCacheManager {
    private val memoryCache = mutableMapOf<String, ByteArray>()

    fun getMemory(url: String): ByteArray? = memoryCache[url]

    fun putMemory(url: String, bytes: ByteArray) {
        if (bytes.isNotEmpty()) {
            memoryCache[url] = bytes
        }
    }

    private fun getSafeFileName(url: String): String {
        val sanitized = url.substringAfterLast('/').take(30).replace(Regex("[^A-Za-z0-9]"), "_")
        return "${url.hashCode()}_$sanitized"
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    fun getDiskBytes(url: String): ByteArray? {
        val safeName = getSafeFileName(url)
        val cacheDir = NSHomeDirectory() + "/Library/Caches/image_cache"
        val filePath = "$cacheDir/$safeName"
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(filePath)) return null

        val nsData = NSData.create(contentsOfFile = filePath) ?: return null
        if (nsData.length == 0UL) return null

        val byteArray = ByteArray(nsData.length.toInt())
        byteArray.usePinned { pinned ->
            memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
        }
        return byteArray
    }

    @OptIn(ExperimentalForeignApi::class)
    fun saveToDisk(url: String, data: NSData) {
        try {
            val safeName = getSafeFileName(url)
            val cacheDir = NSHomeDirectory() + "/Library/Caches/image_cache"
            val fileManager = NSFileManager.defaultManager
            if (!fileManager.fileExistsAtPath(cacheDir)) {
                fileManager.createDirectoryAtPath(cacheDir, true, null, null)
            }
            val filePath = "$cacheDir/$safeName"
            fileManager.createFileAtPath(filePath, contents = data, attributes = null)
        } catch (e: Exception) {
            println("Lỗi lưu cache ảnh: ${e.message}")
        }
    }
}

private suspend fun fetchImageBytesFromUrl(url: String): ByteArray {
    try {
        ImageCacheManager.getMemory(url)?.let { return it }
        ImageCacheManager.getDiskBytes(url)?.let {
            ImageCacheManager.putMemory(url, it)
            return it
        }

        val nsUrl = NSURL.URLWithString(url) ?: return byteArrayOf()
        val request = NSMutableURLRequest.requestWithURL(nsUrl).apply {
            setHTTPMethod("GET")
            setTimeoutInterval(20.0)
        }

        return suspendCancellableCoroutine { continuation ->
            val task = NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, error ->
                try {
                    if (error != null || data == null) {
                        if (continuation.isActive) continuation.resume(byteArrayOf())
                        return@dataTaskWithRequest
                    }
                    val httpResponse = response as? NSHTTPURLResponse
                    if (httpResponse != null && httpResponse.statusCode !in 200L..299L) {
                        if (continuation.isActive) continuation.resume(byteArrayOf())
                        return@dataTaskWithRequest
                    }

                    @OptIn(ExperimentalForeignApi::class)
                    val bytes = ByteArray(data.length.toInt()).apply {
                        if (isNotEmpty()) {
                            usePinned { pinned ->
                                memcpy(pinned.addressOf(0), data.bytes, data.length)
                            }
                        }
                    }

                    if (bytes.isNotEmpty()) {
                        ImageCacheManager.putMemory(url, bytes)
                        ImageCacheManager.saveToDisk(url, data)
                    }

                    if (continuation.isActive) {
                        continuation.resume(bytes)
                    }
                } catch (_: Throwable) {
                    if (continuation.isActive) {
                        continuation.resume(byteArrayOf())
                    }
                }
            }
            continuation.invokeOnCancellation {
                task.cancel()
            }
            task.resume()
        }
    } catch (_: Throwable) {
        return byteArrayOf()
    }
}

@Composable
actual fun ImageFromUrl(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier
) {
    var imageBitmap by remember(imageUrl) { mutableStateOf<ImageBitmap?>(null) }
    var isError by remember(imageUrl) { mutableStateOf(false) }

    LaunchedEffect(imageUrl) {
        imageBitmap = null
        isError = false
        if (imageUrl.isBlank()) {
            isError = true
            return@LaunchedEffect
        }
        try {
            val bytes = fetchImageBytesFromUrl(imageUrl)
            if (bytes.isNotEmpty()) {
                imageBitmap = SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
            } else {
                isError = true
            }
        } catch (_: Throwable) {
            isError = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .background(Color.LightGray.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageBitmap != null -> {
                Image(
                    bitmap = imageBitmap!!,
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
            isError -> {
                Image(
                    painter = painterResource(Res.drawable.error_image),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
            else -> {
                Image(
                    painter = painterResource(Res.drawable.placeholder1),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@Composable
actual fun ZoomableImageFromUrl(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier
) {
    ImageFromUrl(
        imageUrl = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
    )
}

actual fun isUserLoggedIn(): Boolean {
    return NSUserDefaults.standardUserDefaults.boolForKey("is_logged_in")
}

actual fun setUserLoggedIn(loggedIn: Boolean) {
    NSUserDefaults.standardUserDefaults.setBool(loggedIn, forKey = "is_logged_in")
}
