package com.songbai.futurex.activity.mine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.view.safety.GestureUnLockSudoKuView;
import com.songbai.futurex.view.safety.NineGridView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SafeActivity extends AppCompatActivity {

    @BindView(R.id.nineGridView)
    NineGridView mNineGridView;
    @BindView(R.id.passErrorHint)
    TextView mPassErrorHint;
    @BindView(R.id.lockView)
    GestureUnLockSudoKuView mLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
        ButterKnife.bind(this);
    }
}
