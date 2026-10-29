package ma.mousayir.tarbiyatdjaj

import android.app.Activity
import android.app.DownloadManager
import android.os.Environment
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.JavascriptInterface
import android.webkit.DownloadListener
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import android.view.View
import android.view.WindowManager
import android.graphics.Color
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    private val filePickerRequest = 101
    private val backupSaveRequest = 102
    private var pendingBackupJson: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(18, 61, 49)
        window.navigationBarColor = Color.rgb(18, 61, 49)
        window.decorView.systemUiVisibility = 0
        webView = WebView(this)
        webView.setBackgroundColor(Color.rgb(246, 249, 246))
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
        webView.settings.setSupportZoom(false)
        webView.addJavascriptInterface(object {
            @JavascriptInterface
            fun saveBackup(json: String) {
                runOnUiThread {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/json"
                        putExtra(Intent.EXTRA_TITLE, "backup-tarbiyat-djaj.json")
                    }
                    pendingBackupJson = json
                    startActivityForResult(intent, backupSaveRequest)
                }
            }
        }, "AndroidBackup")

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@MainActivity.filePathCallback?.onReceiveValue(null)
                this@MainActivity.filePathCallback = filePathCallback
                val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "*/*"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }
                return try {
                    startActivityForResult(intent, filePickerRequest)
                    true
                } catch (e: Exception) {
                    this@MainActivity.filePathCallback = null
                    Toast.makeText(this@MainActivity, "تعذر فتح اختيار الملفات", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            try {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setMimeType(mimeType ?: "application/json")
                request.addRequestHeader("User-Agent", userAgent)
                request.setTitle("backup-tarbiyat-djaj.json")
                request.setDescription("جاري تنزيل النسخة الاحتياطية")
                request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETION
                )
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "backup-tarbiyat-djaj.json"
                )
                val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                manager.enqueue(request)
                Toast.makeText(this, "بدأ تنزيل النسخة الاحتياطية", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this, "تعذر تنزيل النسخة الاحتياطية", Toast.LENGTH_LONG).show()
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val uri = request?.url ?: return false
                return if (uri.scheme == "http" || uri.scheme == "https") {
                    try { startActivity(Intent(Intent.ACTION_VIEW, uri)) } catch (_: Exception) {}
                    true
                } else false
            }
        }
        setContentView(webView)
        webView.loadUrl("file:///android_asset/index.html")
    }

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == filePickerRequest) {
            val results = if (resultCode == RESULT_OK && data?.data != null) arrayOf(data.data!!) else null
            filePathCallback?.onReceiveValue(results)
            filePathCallback = null
        } else if (requestCode == backupSaveRequest) {
            val uri = if (resultCode == RESULT_OK) data?.data else null
            val json = pendingBackupJson
            pendingBackupJson = null
            if (uri != null && json != null) {
                try {
                    contentResolver.openOutputStream(uri)?.bufferedWriter(Charsets.UTF_8)?.use {
                        it.write(json)
                    }
                    Toast.makeText(this, "تم حفظ النسخة الاحتياطية", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "تعذر حفظ النسخة الاحتياطية", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
