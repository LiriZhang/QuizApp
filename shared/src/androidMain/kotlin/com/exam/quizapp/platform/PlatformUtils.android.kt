@file:OptIn(UnstableApi::class)

package com.exam.quizapp.platform

import android.app.Activity
import android.content.Context
import android.provider.Settings
import android.content.res.Configuration
import android.content.res.Resources
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import java.io.File
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.exam.quizapp.shared.resources.Res
import com.exam.quizapp.shared.resources.placeholder1
import com.exam.quizapp.shared.resources.error_image
import org.jetbrains.compose.resources.painterResource
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.net.URL
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import java.net.URLEncoder
import java.text.Normalizer
import java.util.UUID

//HÀM HIỂN THỊ CLIP TỪ URL
@Composable
actual fun PlatformVideoPlayer(videoUrl: String, modifier: Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current

    var aspectRatio by remember(videoUrl) { mutableFloatStateOf(16f / 9f) }
    var isLoading by remember(videoUrl) { mutableStateOf(true) }

    val activity = context as? Activity
    val window = activity?.window

    LaunchedEffect(configuration.orientation) {
        window?.let { w ->
            val windowInsetsController = WindowCompat.getInsetsController(w, w.decorView)
            if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                windowInsetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
            setMediaItem(mediaItem)
            addListener(object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    if (videoSize.width > 0 && videoSize.height > 0) {
                        aspectRatio = when {
                            videoSize.width > videoSize.height -> 16f / 9f
                            videoSize.width == videoSize.height -> 1f
                            else -> 9f / 16f
                        }
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    isLoading = (playbackState == ExoPlayer.STATE_BUFFERING)
                }
            })
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.playWhenReady = true
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    Box(
        modifier = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Modifier
                .fillMaxWidth()
                .height(configuration.screenHeightDp.dp)
                .background(Color.Black)
        } else {
            modifier.aspectRatio(aspectRatio)
        },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            update = { playerView ->
                if (playerView.player != exoPlayer) {
                    playerView.player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        }
    }
}
//HÀM HIỂN THỊ ÂM THÀNH TỪ URL
@Composable
actual fun PlatformAudioPlayer(audioUrl: String, modifier: Modifier) {
    val context = LocalContext.current

    var isPlaying by remember(audioUrl) { mutableStateOf(false) }
    var currentSpeed by remember(audioUrl) { mutableFloatStateOf(1.0f) }
    var progress by remember(audioUrl) { mutableFloatStateOf(0f) }
    var durationMs by remember(audioUrl) { mutableLongStateOf(0L) }
    var currentPositionMs by remember(audioUrl) { mutableLongStateOf(0L) }

    val exoPlayer = remember(audioUrl) {
        val cacheFactory = AudioCacheManager.getCacheDataSourceFactory(context)
        val mediaSourceFactory = DefaultMediaSourceFactory(cacheFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(audioUrl)))
                prepare()
            }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    if (exoPlayer.duration > 0) {
                        durationMs = exoPlayer.duration
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    isPlaying = false
                    progress = 1f
                    currentPositionMs = durationMs
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPositionMs = exoPlayer.currentPosition
            if (exoPlayer.duration > 0) {
                durationMs = exoPlayer.duration
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
            // Nút Play / Pause
            IconButton(
                onClick = {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    } else {
                        if (exoPlayer.playbackState == Player.STATE_ENDED) {
                            exoPlayer.seekTo(0)
                        }
                        exoPlayer.play()
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

            // Nút Replay (Phát lại từ đầu)
            IconButton(
                onClick = {
                    exoPlayer.seekTo(0)
                    exoPlayer.play()
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

            // Thanh tiến trình & thời gian phát
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

            // Nút chỉnh tốc độ (0.8x, 1.0x, 1.25x, 1.5x)
            Surface(
                onClick = {
                    val currentIndex = speeds.indexOf(currentSpeed)
                    val nextIndex = (if (currentIndex != -1) currentIndex + 1 else 0) % speeds.size
                    val nextSpeed = speeds[nextIndex]
                    currentSpeed = nextSpeed
                    exoPlayer.playbackParameters = PlaybackParameters(nextSpeed)
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

object AppContextHolder {
    var context: Context? = null
}

// Hàm của âm thanh có tính đúng sai
actual fun playSound(isCorrect: Boolean) {
    AppContextHolder.context?.let { ctx ->
        val fileName = if (isCorrect) "files/ting_nhe.mp3" else "files/te.mp3"
        playSoundFromAssets(ctx, fileName)
    }
}

// Hàm của âm thanh có tính đúng sai nhưng chỉ sử dụng như hàm hằng
actual fun playVoice(isCorrect: Boolean) {
    AppContextHolder.context?.let { ctx ->
        val fileName = if (isCorrect) "files/ting.mp3" else "files/at_o.mp3"
        playSoundFromAssets(ctx, fileName)
    }
}

private fun playSoundFromAssets(ctx: Context, fileName: String) {
    try {
        val uriStr = Res.getUri(fileName)
        val assetPath = uriStr
            .removePrefix("file:///android_asset/")
            .removePrefix("asset:///")
            .removePrefix("file:///")

        val afd = try {
            ctx.assets.openFd(assetPath)
        } catch (_: Exception) {
            ctx.assets.openFd(fileName)
        }

        afd.use { fd ->
            MediaPlayer().apply {
                setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
                prepare()
                setOnCompletionListener { release() }
                start()
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
// Hàm Rung lên nếu làm sai
actual fun triggerVibration() {
    AppContextHolder.context?.let { ctx ->
        val vibrator = ContextCompat.getSystemService(ctx, Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator?.vibrate(vibrationEffect)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(100)
        }
    }
}
//Mục đích: Tự động lưu bản sao audio xuống bộ nhớ tạm của thiết bị
// (tối đa 100MB). Khi người học bấm nghe lại câu hỏi, audio sẽ phát tức thì 0 giây trễ và không tốn băng thông 3G/4G.
object AudioCacheManager {
    @Volatile
    private var simpleCache: SimpleCache? = null

    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val cache = simpleCache ?: synchronized(this) {
            simpleCache ?: run {
                val cacheDir = File(context.cacheDir, "audio_quiz_cache")
                val evictor = LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024) // Cache tối đa 100MB
                val databaseProvider = StandaloneDatabaseProvider(context)
                SimpleCache(cacheDir, evictor, databaseProvider).also { simpleCache = it }
            }
        }

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}

private val ktorClient by lazy { HttpClient() }

actual suspend fun fetchTextFromUrl(url: String): String {
    TextCacheManager.get(url)?.let { return it }
    return try {
        val result = ktorClient.get(url).bodyAsText().trim()
        if (result.isNotBlank()) {
            TextCacheManager.put(url, result)
        }
        result
    } catch (e: Exception) {
        ""
    }
}

@Composable
actual fun ImageFromUrl(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .background(Color.LightGray.copy(alpha = 0.2f)),
        contentScale = ContentScale.FillWidth,
        placeholder = painterResource(Res.drawable.placeholder1),
        error = painterResource(Res.drawable.error_image)
    )
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
    val prefs = AppContextHolder.context?.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
    return prefs?.getBoolean("is_logged_in", false) ?: false
}

actual fun setUserLoggedIn(loggedIn: Boolean) {
    val prefs = AppContextHolder.context?.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
    prefs?.edit()?.putBoolean("is_logged_in", loggedIn)?.apply()
}

actual fun getLoggedInPhone(): String {
    val prefs = AppContextHolder.context?.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
    return prefs?.getString("logged_in_phone", "") ?: ""
}

actual fun setLoggedInPhone(phone: String) {
    val prefs = AppContextHolder.context?.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
    prefs?.edit()?.putString("logged_in_phone", phone)?.apply()
}

actual fun getDeviceId(): String {
    val context = AppContextHolder.context ?: return "unknown_android"
    val prefs = context.getSharedPreferences("device_id_prefs", Context.MODE_PRIVATE)
    var deviceId = prefs.getString("device_uuid", null)
    if (deviceId.isNullOrEmpty()) {
        deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            .takeIf { !it.isNullOrEmpty() }
            ?: UUID.randomUUID().toString()
        prefs.edit().putString("device_uuid", deviceId).apply()
    }
    return deviceId
}

actual suspend fun uploadQuizResultToDrive(
    url: String,
    phone: String,
    quizName: String,
    score: Int,
    totalQuestions: Int,
    resultId: Int,
    fileContent: String
): String {
    return try {
        val safeQuizName = quizName.replace(Regex("[\\\\/:*?\"<>|]"), "_")
        val fileName = "${safeQuizName}_id${resultId}.txt"

        val encodedPhone = URLEncoder.encode(phone, "UTF-8")
        val encodedFileName = URLEncoder.encode(fileName, "UTF-8")
        val encodedContent = URLEncoder.encode(fileContent, "UTF-8")
        
        val fullUrl = "$url?phone=$encodedPhone&fileName=$encodedFileName&content=$encodedContent"
        fetchTextFromUrl(fullUrl)
    } catch (e: Exception) {
        "{\"status\":\"error\",\"message\":\"${e.message}\"}"
    }
}
