package com.songbai.futurex.newotc;

import android.webkit.WebView;

import com.songbai.futurex.activity.WebActivity;

/**
 * @author yangguangda
 * @date 2018/8/31
 */
public class Otc365FilterWebActivity extends WebActivity {
    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        boolean contains = url.contains("otc/otcback/v1/syncBackMethod.do?company_order_num=");
        if (contains) {
            finish();
        }
        return contains;
    }
}
