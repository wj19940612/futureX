package com.songbai.futurex.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.songbai.futurex.App;
import com.songbai.futurex.Preference;
import com.songbai.futurex.model.CurrencyPair;
import com.songbai.futurex.model.local.LocalUser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Modified by john on 2018/6/14
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class SearchRecordsHelper {

    private Gson mGson;
    private Type mType;

    private List<CurrencyPair> mRecordList;

    public SearchRecordsHelper() {
        mGson = new Gson();
        mType = new TypeToken<List<CurrencyPair>>() {
        }.getType();
        mRecordList = getRecordsByUserOrDeviceId();
    }

    private List<CurrencyPair> getRecordsByUserOrDeviceId() {
        String json = Preference.get().getSearchRecordsByUserOrDeviceId(getId());
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        return mGson.fromJson(json, mType);
    }

    public List<CurrencyPair> getRecordList() {
        return mRecordList;
    }

    public void clearRecord() {
        mRecordList.clear();
        Preference.get().setSearchRecordsForUserOrDeviceId(getId(), null);
    }

    /**
     * 添加新的搜索记录（找到老的，删除，把新的数据添加到最前面）
     *
     * @param currencyPair
     */
    public void addRecord(CurrencyPair currencyPair) {
        Iterator<CurrencyPair> iterator = mRecordList.iterator();
        while (iterator.hasNext()) {
            CurrencyPair pair = iterator.next();
            if (pair.getPairs().equals(currencyPair.getPairs())) {
                iterator.remove();
                break;
            }
        }
        mRecordList.add(0, currencyPair);
        String json = mGson.toJson(mRecordList);
        Preference.get().setSearchRecordsForUserOrDeviceId(getId(), json);
    }

    private String getId() {
        return LocalUser.getUser().isLogin() ?
                LocalUser.getUser().getUserInfo().getUserId() :
                AppInfo.getDeviceHardwareId(App.getAppContext());
    }
}
