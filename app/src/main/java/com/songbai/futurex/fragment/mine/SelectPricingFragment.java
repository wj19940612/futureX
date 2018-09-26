package com.songbai.futurex.fragment.mine;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.songbai.futurex.Preference;
import com.songbai.futurex.R;
import com.songbai.futurex.activity.UniqueActivity;
import com.songbai.futurex.view.IconTextRow;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author yangguangda
 * @date 2018/9/20
 */
public class SelectPricingFragment extends UniqueActivity.UniFragment {
    Unbinder unbinder;
    @BindView(R.id.cny)
    IconTextRow mCny;
    @BindView(R.id.usd)
    IconTextRow mUsd;
    @BindView(R.id.twd)
    IconTextRow mTwd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_pricing, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onCreateWithExtras(Bundle savedInstanceState, Bundle extras) {
    }

    @Override
    protected void onPostActivityCreated(Bundle savedInstanceState) {
        setPricingState();
    }

    private void setPricingState() {
        String pricingMethod = Preference.get().getPricingMethod();
        mCny.setRightIconVisible("cny".equals(pricingMethod));
        mUsd.setRightIconVisible("usd".equals(pricingMethod));
        mTwd.setRightIconVisible("twd".equals(pricingMethod));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.cny, R.id.usd, R.id.twd})
    public void onViewClicked(View view) {
        String pricingMethod = Preference.get().getPricingMethod();
        switch (view.getId()) {
            case R.id.cny:
                if (!"cny".equals(pricingMethod)) {
                    Preference.get().setPricingMethod("cny");
                }
                setPricingState();
                break;
            case R.id.usd:
                if (!"usd".equals(pricingMethod)) {
                    Preference.get().setPricingMethod("usd");
                }
                setPricingState();
                break;
            case R.id.twd:
                if (!"twd".equals(pricingMethod)) {
                    Preference.get().setPricingMethod("twd");
                }
                setPricingState();
                break;
            default:
        }
    }
}
