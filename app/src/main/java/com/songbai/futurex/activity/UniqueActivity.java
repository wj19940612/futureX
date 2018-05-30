package com.songbai.futurex.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.songbai.futurex.R;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.utils.Launcher;

/**
 * Modified by john on 2018/5/30
 * <p>
 * Description: 独特的 Activity，仅仅用于显示单个 fragment
 */
public class UniqueActivity extends BaseActivity {

    private String mFragmentName;
    private Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData(getIntent());

        setContentView(R.layout.activity_unique);

        Fragment fragment = Fragment.instantiate(getActivity(), mFragmentName, mExtras);

        if (fragment instanceof UniFragment) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commitNow();
        } else {
            throw new ClassCastException(fragment.getClass().getSimpleName() + " should extends UniqueActivity.UniFragment");
        }
    }

    private void initData(Intent intent) {
        mFragmentName = intent.getStringExtra("frag");
        mExtras = intent.getExtras();
    }

    public static Launcher launcher(Context context, Class<?> fragmentClazz) {
        return Launcher.with(context, UniqueActivity.class)
                .putExtra("frag", fragmentClazz.getCanonicalName());
    }

    public static Launcher launcher(Fragment fragment, Class<?> fragmentClazz) {
        return Launcher.with(fragment, UniqueActivity.class)
                .putExtra("frag", fragmentClazz.getCanonicalName());
    }

    public static abstract class UniFragment extends BaseFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            onCreateWithExtras(savedInstanceState, getArguments());
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            onPostActivityCreated(savedInstanceState);
        }

        protected abstract void onCreateWithExtras(Bundle savedInstanceState, Bundle extras);

        protected abstract void onPostActivityCreated(Bundle savedInstanceState);
    }
}
