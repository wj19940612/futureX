package com.songbai.futurex;

import android.os.Bundle;

import com.songbai.futurex.activity.BaseActivity;
import com.songbai.futurex.view.RadioButtonHeader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.header)
    RadioButtonHeader mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mHeader.notify(2);
        mHeader.notify(0, 10);
        mHeader.notify(1, 8);
        mHeader.notify(5, 8);
    }
}
