package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.httplib.ReqError;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.mine.MyPropertyActivity;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.http.Apic;
import com.songbai.futurex.http.Callback;
import com.songbai.futurex.http.Resp;
import com.songbai.futurex.model.mine.AccountList;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/5/31
 */
public class PropertyListFragment extends BaseFragment {
    private static final String PROPERTY_TYPE = "property_type";

    @BindView(R.id.searchProperty)
    EditText mSearchProperty;
    @BindView(R.id.hideZero)
    TextView mHideZero;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;
    private int mPropertyType;
    private PropertyListAdapter mAdapter;

    public static PropertyListFragment newInstance(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(PROPERTY_TYPE, position);
        PropertyListFragment messageFragment = new PropertyListFragment();
        messageFragment.setArguments(bundle);
        return messageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mPropertyType = bundle.getInt(PROPERTY_TYPE, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_property_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PropertyListAdapter();
        mRecyclerView.setAdapter(mAdapter);
        if (mPropertyType == 1) {
            accountList();
        } else if (mPropertyType == 2) {
            userAccount();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.hideZero})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.hideZero:
                break;
            default:
        }
    }

    private void accountList() {
        Apic.accountList()
                .callback(new Callback<Resp<AccountList>>() {
                    @Override
                    protected void onRespSuccess(Resp<AccountList> resp) {
                        setAdapter(1,resp.getData());
                    }
                })
                .fire();
    }

    private void userAccount() {
        Apic.userAccount()
                .callback(new Callback<Object>() {

                    @Override
                    public void onFailure(ReqError reqError) {

                    }

                    @Override
                    protected void onRespSuccess(Object resp) {

                    }
                })
                .fire();
    }

    private void setAdapter(int postiton, AccountList accountList) {
        ((MyPropertyActivity) getActivity()).setAccountAmount(postiton,accountList.getBalance());
        mAdapter.setList(accountList.getAccount());
        mAdapter.notifyDataSetChanged();
    }

    static class PropertyListAdapter extends RecyclerView.Adapter {
        private List<AccountList.AccountBean> mList;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_property, parent, false);
            return new PropertyListHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            if (mList == null) {
                return 0;
            }
            return mList.size();
        }

        public void setList(List<AccountList.AccountBean> list) {
            mList = list;
        }

        static class PropertyListHolder extends RecyclerView.ViewHolder {
            PropertyListHolder(View itemView) {
                super(itemView);
            }

            private void bindData() {
            }
        }
    }
}
