package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.activity.mine.DrawCoinActivity;
import com.songbai.futurex.fragment.BaseFragment;
import com.songbai.futurex.utils.Launcher;

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
    @BindView(R.id.rechargeCoin)
    TextView mRechargeCoin;
    @BindView(R.id.drawCoin)
    TextView mDrawCoin;
    @BindView(R.id.propertyFlow)
    TextView mPropertyFlow;
    @BindView(R.id.propertyOperateGroup)
    ConstraintLayout mPropertyOperateGroup;
    private Unbinder mBind;
    private int mPropertyType;

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
        switch (mPropertyType) {
            case 0:
                mPropertyOperateGroup.setVisibility(View.VISIBLE);
                mHideZero.setVisibility(View.VISIBLE);
                break;
            case 1:
                mPropertyOperateGroup.setVisibility(View.GONE);
                mHideZero.setVisibility(View.GONE);
                break;
            default:
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new PropertyListAdapter());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(new PropertyListAdapter());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.rechargeCoin, R.id.drawCoin, R.id.propertyFlow, R.id.hideZero})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rechargeCoin:
                UniqueActivity.launcher(getContext(), ReChargeCoinFragment.class).execute();
                break;
            case R.id.drawCoin:
                Launcher.with(getContext(), DrawCoinActivity.class).execute();
                break;
            case R.id.propertyFlow:
                break;
            case R.id.hideZero:
                break;
            default:
        }
    }

    static class PropertyListAdapter extends RecyclerView.Adapter {
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
            return 5;
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
