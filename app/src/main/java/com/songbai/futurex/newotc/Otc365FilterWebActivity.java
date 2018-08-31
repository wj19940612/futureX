package com.songbai.futurex.newotc;

import android.webkit.WebView;

import com.songbai.futurex.BuildConfig;
import com.songbai.futurex.activity.WebActivity;

/**
 * @author yangguangda
 * @date 2018/8/31
 */
public class Otc365FilterWebActivity extends WebActivity {
    @Override
    protected boolean onShouldOverrideUrlLoading(WebView view, String url) {
        boolean contains = url.contains(BuildConfig.HOST);
        if (contains) {
            finish();
        }
        return contains;
    }
}
