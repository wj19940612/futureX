package com.songbai.futurex.fragment.mine;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.songbai.futurex.ExtraKeys;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.Introduce;
import com.songbai.futurex.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.songbai.futurex.activity.WebActivity.INFO_HTML_META;

public class PlatformIntroFragment extends UniqueActivity.UniFragment {

    public static final int STYLE_PLATFORM = 1;
    public static final int STYLE_COMPANY = 2;
    public static final int STYLE_SERVICE_AGREEMENT = 3;

    public static final String PLATFORM_CODE = "MPlatformIntroduction";
    public static final String COMPANY_CODE = "MCompanyProfile";
    public static final String SERVICE_AGREEMENT_CODE = "ServiceAgreement";

    @BindView(R.id.platformIntroduction)
    WebView mPlatformIntroduction;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    private Unbinder mBind;

    private int mStyle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_platform_introduce, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
        initData(extras);
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        initTitle();
        initWebView(mPlatformIntroduction);
        loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void initData(Bundle extras) {
        mStyle = extras.getInt(ExtraKeys.INTRODUCE_STYLE);
    }

    private void initTitle(){
        if (mStyle == STYLE_PLATFORM) {
            mTitleBar.setTitle(R.string.platform_introduction);
        }else if (mStyle == STYLE_COMPANY){
            mTitleBar.setTitle(R.string.company_introduction);
        }else{
            mTitleBar.setTitle(R.string.service_agreement);
        }
    }

    protected void initWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUserAgentString(webSettings.getUserAgentString()
                + " ###" + getString(R.string.android_web_agent) + "/2.0");
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

    private void loadData() {
        String code;
        if(mStyle == STYLE_PLATFORM){
            code = PLATFORM_CODE;
        }else if(mStyle == STYLE_COMPANY){
            code = COMPANY_CODE;
        }else{
            code = SERVICE_AGREEMENT_CODE;
        }
        Apic.requestPlatformIntroduce(code).tag(TAG).callback(new Callback<Resp<Introduce>>() {
            @Override
            protected void onRespSuccess(Resp<Introduce> resp) {
                updatePlatform(resp.getData());
            }
        }).fireFreely();
    }

    private void updatePlatform(Introduce data) {
        openWebView(data.getContent(), mPlatformIntroduction);
    }

    private void openWebView(String urlData, WebView webView) {
        String content;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            content = INFO_HTML_META + "<body>" + urlData + "</body>";
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
                "                  margin-top: 50px !important;\n" +
                "                  margin-left: 35px !important;\n" +
                "                  margin-right: 35px !important;\n" +
                "                }\n" +
                "                * {\n" +
                "                  text-align:justify;\n" +
                "                  font-size: 15px !important;\n" +
                "                  font-family: 'PingFangSC-Regular' !important;\n" +
                "                  color: #666666 !important;\n" +
                "                  background-color: white !important;\n" +
                "                  letter-spacing: 1px !important;\n" +
                "                  line-height: 18px !important;\n" +
                "                  white-space: break-all !important;\n" +
                "                  word-wrap: break-word;\n" +
                "                }\n" +
                "                img{max-width:100% !important; width:auto; height:auto;}\n" +
                "            </style>\n" + "</head>";
        return textHead;
    }
}
