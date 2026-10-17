package com.exam.quizapp.platform

// FILE NÀY CHỈ CHỨA CÁC HÀM LIÊN QUAN ĐỂN QUẢNG CÁO TIÊU NGỮ
// CHỈ SỬ DỤNG TRONG FILE MainActivity

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun BannerAdView() {
    var adLoaded by remember { mutableStateOf(false) }
    var adView: AdView? by remember { mutableStateOf(null) }

    fun loadAd(adView: AdView?) {
        adView?.loadAd(AdRequest.Builder().build())
    }

    val adListener = createAdListener { loaded ->
        adLoaded = loaded
        if (!loaded) {
            Handler(Looper.getMainLooper()).postDelayed({
                loadAd(adView)
            }, 5000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (adLoaded) getStatusBarHeight() else 0.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentWidth()
                .heightIn(
                    min = if (adLoaded) 48.dp else 0.dp,
                    max = 100.dp
                )
                .wrapContentHeight(),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-3940256099942544/9214589741"
                    setAdListener(adListener)
                    loadAd(AdRequest.Builder().build())
                    adView = this
                }
            },
            update = { updatedAdView ->
                updatedAdView.visibility = if (adLoaded) View.VISIBLE else View.GONE
                adView = updatedAdView
            }
        )
    }
}

@Composable
actual fun getBannerAdHeight(): Dp {
    var adLoaded by remember { mutableStateOf(false) }
    var adView: AdView? by remember { mutableStateOf(null) }

    fun loadAd(adView: AdView?) {
        adView?.loadAd(AdRequest.Builder().build())
    }

    val adListener = createAdListener { loaded ->
        adLoaded = loaded
        if (!loaded) {
            Handler(Looper.getMainLooper()).postDelayed({
                loadAd(adView)
            }, 10000)
        }
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-3940256099942544/9214589741"
            setAdListener(adListener)
            loadAd(AdRequest.Builder().build())
            adView = this
        }
    }

    return if (adLoaded) 0.dp else getStatusBarHeight()
}

// Hàm tạo listener quảng cáo
@Composable
fun createAdListener(onAdLoaded: (Boolean) -> Unit): AdListener {
    return object : AdListener() {
        override fun onAdLoaded() {
            super.onAdLoaded()
            onAdLoaded(true)
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
            super.onAdFailedToLoad(adError)
            onAdLoaded(false)
        }
    }
}
