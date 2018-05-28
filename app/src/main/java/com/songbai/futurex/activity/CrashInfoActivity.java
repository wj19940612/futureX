package com.songbai.futurex.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.songbai.futurex.ExKeys;
import com.songbai.futurex.MainActivity;
import com.songbai.futurex.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrashInfoActivity extends AppCompatActivity {


    @BindView(R.id.crashInfo)
    TextView mCrashInfo;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.cut)
    Button mCut;
    @BindView(R.id.restartApp)
    Button mRestartApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_info);
        ButterKnife.bind(this);

        Throwable throwable = (Throwable) getIntent().getSerializableExtra(ExKeys.CRASH_INFO);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        try {
            stringWriter.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mCrashInfo.setText(stringWriter.toString());

    }

    @OnClick({R.id.cut, R.id.restartApp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cut:
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", mCrashInfo.getText().toString());
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                }
                break;
            case R.id.restartApp:
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                finish();

                break;
        }
    }
}
