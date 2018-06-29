package com.songbai.futurex.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.sbai.httplib.CookieManger;
import com.songbai.futurex.AppJs;
import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.auth.LoginActivity;
import com.songbai.futurex.utils.Network;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.songbai.futurex.utils.Network.registerNetworkChangeReceiver;
import static com.songbai.futurex.utils.Network.unregisterNetworkChangeReceiver;


public class WebActivity extends BaseActivity {
    public static final String TAG = "WebActivity";
    public static final String INFO_HTML_META = "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no\">";

    public static final String EX_URL = "url";
    public static final String EX_TITLE = "title";
    public static final String EX_HTML = "html";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.progressbar)
    ProgressBar mProgress;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.refreshButton)
    Button mRefreshButton;
    @BindView(R.id.errorPage)
    LinearLayout mErrorPage;


    private boolean mLoadSuccess;
    protected String mPageUrl;
    protected String mTitle;
    protected String mPureHtml;

    private BroadcastReceiver mNetworkChangeReceiver;
    private WebViewClient mWebViewClient;

    private boolean mHasCloseView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        mNetworkChangeReceiver = new NetworkReceiver();
        mLoadSuccess = true;
        initData(getIntent());
        initWebView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LoginActivity.REQ_LOGIN:
                    // init cookies
                    mWebView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            syncCookies(mPageUrl);
                            mWebView.reload();
                        }
                    }, 200);
                    break;
            }
        }
    }

    protected void initData(Intent intent) {
        mTitle = intent.getStringExtra(EX_TITLE);
        mPageUrl = intent.getStringExtra(EX_URL);
        mPureHtml = intent.getStringExtra(EX_HTML);

        tryToFixPageUrl();
    }

    private void tryToFixPageUrl() {
        if (!TextUtils.isEmpty(mPageUrl)) {
            if (!mPageUrl.startsWith("http")) { // http or https
                mPageUrl = "http://" + mPageUrl;
            }
        }
    }

    protected void initWebView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(false);
        }

        // init cookies
        syncCookies(mPageUrl);

        // init webSettings
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUserAgentString(webSettings.getUserAgentString()
                + " ###" + getString(R.string.android_web_agent) + "/2.0");
        //mWebView.getSettings().setAppCacheEnabled(true);l
        //webSettings.setAppCachePath(getExternalCacheDir().getPath());
        webSettings.setAllowFileAccess(true);

        // performance improve
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);

        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.clearFormData();
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);
        mWebView.addJavascriptInterface(new AppJs(this), "AppJs");
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        mWebViewClient = new WebViewClient();
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgress.setVisibility(View.GONE);
                } else {
                    if (mProgress.getVisibility() == View.GONE) {
                        mProgress.setVisibility(View.VISIBLE);
                    }
                    mProgress.setProgress(newProgress);
                }
            }
        });
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                WebView.HitTestResult result = mWebView.getHitTestResult();
                if (result != null) {
                    int type = result.getType();
                    if (type == WebView.HitTestResult.IMAGE_TYPE) {
                    }
                }
                return false;
            }
        });
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimeType, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        loadPage();
    }

    protected void syncCookies(String pageUrl) {
        String rawCookie = null;
        rawCookie = CookieManger.getInstance().getLastCookie();
        Log.d(TAG, "syncCookies: " + rawCookie + ", " + pageUrl);

        if (!TextUtils.isEmpty(rawCookie) && !TextUtils.isEmpty(pageUrl)) {
            CookieManager.getInstance().setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().acceptThirdPartyCookies(mWebView);
            }
            String[] cookies = rawCookie.split("\n");
            for (String cookie : cookies) {
                CookieManager.getInstance().setCookie(pageUrl, cookie);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().flush();
            }
            Log.d(TAG, "getCookies: " + CookieManager.getInstance().getCookie(pageUrl));
            boolean sync = !TextUtils.isEmpty(CookieManager.getInstance().getCookie(pageUrl));
            Log.d(TAG, "syncCookies: " + sync);
        }
    }

    protected void loadPage() {
        if (!TextUtils.isEmpty(mPageUrl)) {
            mWebView.loadUrl(mPageUrl);
        } else if (!TextUtils.isEmpty(mPureHtml)) {
            openWebView(mPureHtml);
        }
    }

    private void openWebView(String urlData) {
        String content;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = INFO_HTML_META + "<body>" + mPureHtml + "</body>";
        } else {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = getHtmlData(urlData);
        }
        mWebView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style>" + INFO_HTML_META + "</head>";
        return "<html>" + head + bodyHTML + "</html>";
    }

    @OnClick(R.id.refreshButton)
    public void onViewClicked() {
        mWebView.reload();
    }

    public void updateTitleText(String titleContent) {
        if (isNeedViewTitle()) {
            mTitle = titleContent;
            mTitleBar.setTitle(mTitle);
        }
    }

    protected class WebViewClient extends android.webkit.WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mLoadSuccess = true;
            mPageUrl = url;
            if (!Network.isNetworkAvailable() && TextUtils.isEmpty(mPureHtml)) {
                mLoadSuccess = false;
                mWebView.stopLoading();
            }
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            if (sslError.getPrimaryError() == SslError.SSL_INVALID) {// 个别6.0 7.0手机ssl校验过程遇到了bug
                sslErrorHandler.proceed();
            } else {
                sslErrorHandler.cancel();
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mLoadSuccess) {
                mWebView.setVisibility(View.VISIBLE);
                mErrorPage.setVisibility(View.GONE);
            } else {
                mWebView.setVisibility(View.GONE);
                mErrorPage.setVisibility(View.VISIBLE);
            }
            if (isNeedViewTitle()) {
                String titleText = view.getTitle();
                if (!TextUtils.isEmpty(titleText) && !url.contains(titleText)) {
                    mTitle = titleText;
                }
                mTitleBar.setTitle(mTitle);
            } else {
                mTitleBar.setTitle(mTitle);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String requestUrl = request.getUrl().toString();
                if (mPageUrl.equalsIgnoreCase(requestUrl) && error.getErrorCode() <= ERROR_UNKNOWN) {
                    mLoadSuccess = false;
                }
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (mPageUrl.equalsIgnoreCase(failingUrl) && errorCode <= ERROR_UNKNOWN) {
                mLoadSuccess = false;
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (onShouldOverrideUrlLoading(view, url)) {
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private boolean isNeedViewTitle() {
        return true;
    }

    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
        mWebView.onPause();
    }

    public void screenShot() {
//        mWebView.buildDrawingCache();
//        Bitmap bitmap = mWebView.getDrawingCache();
//        if (bitmap == null) {
//            Picture snapShot = mWebView.capturePicture();
//            bitmap = Bitmap.createBitmap(mWebView.getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            snapShot.draw(canvas);
//        }
//        if (bitmap == null) {
//            Log.d(TAG, "获取图片失败");
//            return;
//        }
//        ShareDialog.with(this)
//                .setTitle(getString(R.string.share_to))
//                .hasWeiBo(false)
//                .setBitmap(bitmap)
//                .setShareImageOnly(true)
//                .show();
    }

    private class NetworkReceiver extends Network.NetworkChangeReceiver {

        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE && !mLoadSuccess) {
                if (mWebView != null) {
                    mWebView.reload();
                }
            }
        }
    }
}
