package com.songbai.futurex.fragment.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.MainActivity;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.local.SupportLang;
import com.songbai.futurex.utils.LanguageUtils;
import com.songbai.futurex.utils.Launcher;
import com.songbai.futurex.utils.UmengCountEventId;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/30
 */
public class SettingLanguageFragment extends UniqueActivity.UniFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;
    private SupportLanguageAdapter mLanguageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_language, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {

    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLanguageAdapter = new SupportLanguageAdapter();
        mRecyclerView.setAdapter(mLanguageAdapter);
        mLanguageAdapter.setOnLanguageClickListener(new SupportLanguageAdapter.OnLanguageClickListener() {
            @Override
            public void onLanguageClick(SupportLang supportLang) {
                if ("en".equals(supportLang.getLocale())) {
                    umengEventCount(UmengCountEventId.SETTINGS0003);
                }
                if ("zh_CN".equals(supportLang.getLocale())) {
                    umengEventCount(UmengCountEventId.SETTINGS0001);
                }
                if ("zh_TW".equals(supportLang.getLocale())) {
                    umengEventCount(UmengCountEventId.SETTINGS0002);
                }
                changeLanguage(supportLang);
                LanguageUtils.setApplicationLanguage(getContext());
            }
        });
        getSupportLocal();
    }

    private void getSupportLocal() {
        Apic.getSupportLang().tag(TAG)
                .callback(new Callback<Resp<ArrayList<SupportLang>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<SupportLang>> resp) {
                        mLanguageAdapter.setList(resp.getData());
                        mLanguageAdapter.notifyDataSetChanged();
                    }
                }).fireFreely();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    public void changeLanguage(SupportLang supportLang) {
        String country = supportLang.getCountry();
        Locale locale = new Locale(supportLang.getLang(), TextUtils.isEmpty(country) ? "" : country);
        boolean changed = LanguageUtils.updateLocale(getContext(), locale);
        if (changed) {
            Preference.get().setCurrentLangageStr(supportLang.getName());
            mLanguageAdapter.notifyDataSetChanged();
            Preference.get().setRefreshLanguage(true);
            Launcher.with(getContext(), MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .execute();
        }
    }

    static class SupportLanguageAdapter extends RecyclerView.Adapter {
        private ArrayList<SupportLang> mList = new ArrayList<>();
        static OnLanguageClickListener mOnLanguageClickListener;
        private Context mContext;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_support_language, parent, false);
            return new SupportLanguageHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SupportLanguageHolder) {
                ((SupportLanguageHolder) holder).bindData(mContext, mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<SupportLang> list) {
            mList.clear();
            mList.addAll(list);
        }

        void setOnLanguageClickListener(OnLanguageClickListener onLanguageClickListener) {
            mOnLanguageClickListener = onLanguageClickListener;
        }

        interface OnLanguageClickListener {
            void onLanguageClick(SupportLang supportLang);
        }

        static class SupportLanguageHolder extends RecyclerView.ViewHolder {
            private final View mRootView;
            @BindView(R.id.language)
            TextView mLanguage;
            @BindView(R.id.check)
            ImageView mCheck;

            SupportLanguageHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                mRootView = view;
            }

            private void bindData(Context context, final SupportLang supportLang) {
                Locale locale = LanguageUtils.getUserLocale(context);
                boolean match;
                String country = supportLang.getCountry();
                if (TextUtils.isEmpty(country)) {
                    match = locale.getLanguage().equals(supportLang.getLang());
                } else {
                    match = locale.getLanguage().equals(supportLang.getLang()) && locale.getCountry().equals(country);
                }
                mCheck.setVisibility(match ? View.VISIBLE : View.GONE);
                mLanguage.setText(supportLang.getName());
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnLanguageClickListener != null) {
                            mOnLanguageClickListener.onLanguageClick(supportLang);
                        }
                    }
                });
            }
        }
    }
}
