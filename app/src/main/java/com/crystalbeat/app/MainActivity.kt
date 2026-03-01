package com.crystalbeat.app

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.webkit.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

  private var fileCallback: ValueCallback<Array<Uri>>? = null

  private val pickFiles = registerForActivityResult(
    ActivityResultContracts.OpenMultipleDocuments()
  ) { uris ->
    fileCallback?.onReceiveValue(uris.toTypedArray())
    fileCallback = null
  }

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val wv = findViewById<WebView>(R.id.webview)

    wv.webViewClient = WebViewClient()

    wv.webChromeClient = object : WebChromeClient() {
      override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
      ): Boolean {
        fileCallback?.onReceiveValue(null)
        fileCallback = filePathCallback
        // audio/* sometimes behaves inconsistently across pickers; * / * is safer
        pickFiles.launch(arrayOf("*/*"))
        return true
      }
    }

    wv.settings.apply {
      javaScriptEnabled = true
      domStorageEnabled = true
      mediaPlaybackRequiresUserGesture = false

      allowFileAccess = true
      allowContentAccess = true
      allowFileAccessFromFileURLs = true
      allowUniversalAccessFromFileURLs = true
    }

    wv.loadUrl("file:///android_asset/index.html")
  }

  override fun onDestroy() {
    super.onDestroy()
    fileCallback?.onReceiveValue(null)
    fileCallback = null
  }
}
