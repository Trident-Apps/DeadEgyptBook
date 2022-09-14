package ar.tvpla.ui.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ar.tvpla.databinding.WebFragmentBinding
import ar.tvpla.ui.activities.GameActivity
import ar.tvpla.ui.model.Url
import ar.tvpla.ui.model.UrlDatabase
import ar.tvpla.ui.viewmodel.EgyptViewModel
import ar.tvpla.ui.viewmodel.EgyptViewModelFactory
import ar.tvpla.utils.Checker

class WebFragment : Fragment() {

    lateinit var webView: WebView
    lateinit var viewModel: EgyptViewModel
    private var messageAb: ValueCallback<Array<Uri?>>? = null
    private var _binding: WebFragmentBinding? = null
    private val binding get() = _binding!!
    private var isRedirected: Boolean = false
    private var pageisLoaded: Boolean = true
    private val checker = Checker()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val url = arguments?.getString("url")
        _binding = WebFragmentBinding.inflate(inflater, container, false)

        val viewModelFactory = EgyptViewModelFactory(
            requireActivity().application,
            UrlDatabase(requireActivity().applicationContext)
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[EgyptViewModel::class.java]

        Log.d("customWeb", url.toString())

        webView = binding.webView
        webView.loadUrl(url!!)
        webView.webViewClient = LocalClient()
        webView.settings.userAgentString = System.getProperty("http.agent")
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = false
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                messageAb = filePathCallback
                selectImageIfNeed()
                return true
            }

            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                val newWebView = WebView(requireContext())
                newWebView.webChromeClient = this
                newWebView.settings.domStorageEnabled = true
                newWebView.settings.javaScriptCanOpenWindowsAutomatically = true
                newWebView.settings.javaScriptEnabled = true
                newWebView.settings.setSupportMultipleWindows(true)
                val transport = resultMsg?.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        view!!.loadUrl(url!!)
                        return true
                    }
                }
                return true
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView.canGoBack()
        webView.setOnKeyListener(View.OnKeyListener { view, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK
                && event.action == MotionEvent.ACTION_UP && webView.canGoBack()
            ) {
                webView.goBack()
                return@OnKeyListener true
            }
            false
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            messageAb?.onReceiveValue(null)
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (messageAb == null) return

            messageAb!!.onReceiveValue(
                WebChromeClient.FileChooserParams.parseResult(
                    resultCode,
                    data
                )
            )
            messageAb = null
        }
    }

    private fun selectImageIfNeed() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = INTENT_TYPE
        startActivityForResult(
            Intent.createChooser(intent, CHOOSER_TITLE), RESULT_CODE
        )
    }

    private inner class LocalClient : WebViewClient() {
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
           // isRedirected = false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (viewModel.getUrlSync() == null && !url.toString().contains("deadegyptbook.online")) {
                url?.let { url ->
                    if (url == BASE_URL) {
                        with(Intent(requireActivity(), GameActivity::class.java)) {
                            startActivity(this)
                        }
                    } else {
                        when(viewModel.getUrlSync()){
                            null -> {
                                Log.d("custom", viewModel.getUrlFromDb().value.toString())
                                Log.d("custom", "null")
                                viewModel.saveUrlToDb(Url(url = url))
                            }
                            else -> {
                                Log.d("custom", "not null")

                            }

                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val INTENT_TYPE = "image/*"
        const val CHOOSER_TITLE = "Image Chooser"
        const val BASE_URL = "https://deadegyptbook.online/"
        const val PATTERN = "(http|https):\\/\\/deadegyptbook.online\\/"
        const val RESULT_CODE = 1
    }
}