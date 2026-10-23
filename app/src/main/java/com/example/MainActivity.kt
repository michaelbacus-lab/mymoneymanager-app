package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
          MoneyManagerWebView()
        }
      }
    }
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MoneyManagerWebView() {
  var webViewRef by remember { mutableStateOf<WebView?>(null) }

  BackHandler(enabled = true) {
    if (webViewRef?.canGoBack() == true) {
      webViewRef?.goBack()
    }
  }

  AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = { context ->
      WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          databaseEnabled = true
          allowFileAccess = true
          allowContentAccess = true
          @Suppress("DEPRECATION")
          allowFileAccessFromFileURLs = true
          @Suppress("DEPRECATION")
          allowUniversalAccessFromFileURLs = true
          mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
          safeBrowsingEnabled = false
          cacheMode = WebSettings.LOAD_DEFAULT
          useWideViewPort = true
          loadWithOverviewMode = true
          displayZoomControls = false
          builtInZoomControls = false
        }
        webViewClient = object : WebViewClient() {
          override fun onReceivedError(
            view: WebView?,
            request: android.webkit.WebResourceRequest?,
            error: android.webkit.WebResourceError?
          ) {
            super.onReceivedError(view, request, error)
            android.util.Log.w("MoneyManagerWebView", "Web resource error: ${error?.description} for ${request?.url}")
          }
        }
        webChromeClient = object : WebChromeClient() {
          override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
            android.util.Log.d(
              "MoneyManagerWebView",
              "${consoleMessage?.message()} [${consoleMessage?.sourceId()}:${consoleMessage?.lineNumber()}]"
            )
            return true
          }
        }
        loadUrl("file:///android_asset/index.html")
        webViewRef = this
      }
    },
    update = { webView ->
      webViewRef = webView
    }
  )
}
