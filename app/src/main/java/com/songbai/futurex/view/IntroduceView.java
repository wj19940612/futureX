package com.songbai.futurex.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.WebActivity;
import com.songbai.futurex.model.CoinIntroduce;
import com.songbai.futurex.utils.DateUtil;
import com.songbai.futurex.utils.LanguageUtils;
import com.songbai.futurex.utils.ToastUtil;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroduceView extends LinearLayout {
    public static final int LANGUAGE_CHINESE = 1;
    public static final int LANGUAGE_TW = 2;
    public static final int LANGUAGE_EN = 3;

    @BindView(R.id.releaseTime)
    TextView mReleaseTime;
    @BindView(R.id.distributionCount)
    TextView mDistributionCount;
    @BindView(R.id.circulationCount)
    TextView mCirculationCount;
    @BindView(R.id.raisePrice)
    TextView mRaisePrice;
    @BindView(R.id.whitePaper)
    TextView mWhitePaper;
    @BindView(R.id.officialNetwork)
    TextView mOfficialNetwork;
    @BindView(R.id.blockQuery)
    TextView mBlockQuery;
    @BindView(R.id.webView)
    WebView mWebView;
    @BindView(R.id.name)
    TextView mName;

    public IntroduceView(Context context) {
        this(context, null);
    }

    public IntroduceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntroduceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_introduce, this, true);
        ButterKnife.bind(this);
        initWebView(mWebView);
    }

    protected void initWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUserAgentString(webSettings.getUserAgentString()
                + " ###" + getContext().getString(R.string.android_web_agent) + "/2.0");
        //mWebView.getSettings().setAppCacheEnabled(true);l
        //webSettings.setAppCachePath(getExternalCacheDir().getPath());
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        // performance improve
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBlockNetworkImage(false);//解决图片不显示
        webView.clearHistory();
        webView.clearCache(true);
        webView.clearFormData();
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //硬件加速 有些API19手机不支持
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
//            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//        } else {
//            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//        }
        webView.setLayerType(View.LAYER_TYPE_NONE, null);
        // mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 以下 默认同时加载http和https
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setDrawingCacheEnabled(true);
        webView.setBackgroundColor(0);
    }

    private void openWebView(String urlData, WebView webView) {
        String content;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = WebActivity.INFO_HTML_META + "<body>" + urlData + "</body>";
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = getHtmlData(urlData);
        }
        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = getTextHead();
        return "<html>" + head + "<body style:'height:auto;max-width: 100%; width:auto;'>" + bodyHTML + "</body></html>";
    }

    private String getTextHead() {
        String textHead = "<head>" +
                "            <meta charset='utf-8'>\n" +
                "            <meta name='viewport' content='width=device-width, user-scalable=no, initial-scale=1.0001, minimum-scale=1.0001, maximum-scale=1.0001, shrink-to-fit=no'>\n" +
                "            <style type='text/css'>\n" +
                "                body {\n" +
                "                  margin-top: 10px !important;\n" +
                "                  margin-left: 12px !important;\n" +
                "                  margin-right: 12px !important;\n" +
                "                }\n" +
                "                * {\n" +
                "                  text-align:justify;\n" +
                "                  font-size: 16px !important;\n" +
                "                  font-family: 'PingFangSC-Regular' !important;\n" +
                "                  color: #494949 !important;\n" +
                "                  background-color: #F5F5F5 !important;\n" +
                "                  letter-spacing: 1px !important;\n" +
                "                  line-height: 31px !important;\n" +
                "                  white-space: break-all !important;\n" +
                "                  word-wrap: break-word;\n" +
                "                }\n" +
                "                img{max-width:100% !important; width:auto; height:auto;}\n" +
                "            </style>\n" + "</head>";
        return textHead;
    }

    public void updateData(CoinIntroduce coinIntroduce) {
        Locale locale = LanguageUtils.getUserLocale(getContext());
        int localeSign;
        if ("zh".equals(locale.getLanguage())) {
            if ("CN".equals(locale.getCountry())) {
                localeSign = LANGUAGE_CHINESE;
            } else {
                localeSign = LANGUAGE_TW;
            }
        } else {
            localeSign = LANGUAGE_EN;
        }
        //name
        if (!TextUtils.isEmpty(coinIntroduce.getName()) && !TextUtils.isEmpty(coinIntroduce.getEnName()) && (localeSign == LANGUAGE_CHINESE || localeSign == LANGUAGE_TW)) {
            mName.setText(coinIntroduce.getName() + "(" + coinIntroduce.getEnName() + ")");
        } else if (!TextUtils.isEmpty(coinIntroduce.getEnName()) && localeSign == LANGUAGE_EN) {
            mName.setText(coinIntroduce.getEnName());
        }

        //publishTime
        if (coinIntroduce.getPublishDate() != 0) {
            mReleaseTime.setText(DateUtil.formatNoticeTime(coinIntroduce.getPublishDate()));
        }

        //publishCount
        if (!TextUtils.isEmpty(coinIntroduce.getPublishCount())) {
            mDistributionCount.setText(coinIntroduce.getPublishCount());
        }

        //circulationCount
        if (!TextUtils.isEmpty(coinIntroduce.getCirculateCount())) {
            mCirculationCount.setText(coinIntroduce.getCirculateCount());
        }

        //raisePrice
        if (!TextUtils.isEmpty(coinIntroduce.getPublishPrice())) {
            mRaisePrice.setText(coinIntroduce.getPublishPrice());
        }

        //whitePaper
        if (!TextUtils.isEmpty(coinIntroduce.getWhitepaperAddr())) {
            mWhitePaper.setText(coinIntroduce.getWhitepaperAddr());
        }

        //officialNet
        if (!TextUtils.isEmpty(coinIntroduce.getOfficalAddr())) {
            mOfficialNetwork.setText(coinIntroduce.getOfficalAddr());
        }

        //blockQuery
        if (!TextUtils.isEmpty(coinIntroduce.getBlockStation())) {
            mBlockQuery.setText(coinIntroduce.getBlockStation());
        }

        if (!TextUtils.isEmpty(coinIntroduce.getDetail())) {
            openWebView(coinIntroduce.getDetail(), mWebView);
        }
    }

    @OnClick({R.id.whitePaper, R.id.officialNetwork, R.id.blockQuery})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.whitePaper:
                copyWxNumber(mWhitePaper);
                break;
            case R.id.officialNetwork:
                copyWxNumber(mOfficialNetwork);
                break;
            case R.id.blockQuery:
                copyWxNumber(mBlockQuery);
                break;
        }
    }

    private void copyWxNumber(TextView view) {
        if (TextUtils.isEmpty(view.getText())) {
            return;
        }
        // 获取系统剪贴板
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建一个剪贴数据集，包含一个普通文本数据条目（需要复制的数据）
        ClipData clipData = ClipData.newPlainText(null, view.getText().toString());
        // 把数据集设置（复制）到剪贴板
        clipboard.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }
}
