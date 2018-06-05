package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.SupportLocal;

import java.util.ArrayList;

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
        getSupportLocal();
    }

    private void getSupportLocal() {
        Apic.getSupportLocal()
                .callback(new Callback<Resp<ArrayList<SupportLocal>>>() {
                    @Override
                    protected void onRespSuccess(Resp<ArrayList<SupportLocal>> resp) {
                        mLanguageAdapter.setList(resp.getData());
                        mLanguageAdapter.notifyDataSetChanged();
                    }
                })
                .fire();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class SupportLanguageAdapter extends RecyclerView.Adapter {
        private ArrayList<SupportLocal> mList = new ArrayList<>();

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_support_language, parent, false);
            return new SupportLanguageHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SupportLanguageHolder) {
                ((SupportLanguageHolder) holder).bindData(mList.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public void setList(ArrayList<SupportLocal> list) {
            mList.clear();
            mList.addAll(list);
        }

        static class SupportLanguageHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.language)
            TextView mLanguage;
            @BindView(R.id.check)
            ImageView mCheck;

            SupportLanguageHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            private void bindData(SupportLocal supportLocal) {
                mLanguage.setText(supportLocal.getName());
            }
        }
    }
}
