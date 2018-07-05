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

    public SearchRecordsHelper() {
        mGson = new Gson();
        mType = new TypeToken<List<CurrencyPair>>() {
        }.getType();
    }

    private List<CurrencyPair> getRecordsByUserOrDeviceId() {
        String json = Preference.get().getSearchRecordsByUserOrDeviceId(getId());
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        return mGson.fromJson(json, mType);
    }

    public List<CurrencyPair> getRecordList() {
        return getRecordsByUserOrDeviceId();
    }

    public void clearRecord() {
        Preference.get().setSearchRecordsForUserOrDeviceId(getId(), null);
    }

    /**
     * 添加新的搜索记录（找到老的，删除，把新的数据添加到最前面）
     *
     * @param currencyPair
     */
    public void addRecord(CurrencyPair currencyPair) {
        List<CurrencyPair> list = getRecordsByUserOrDeviceId();
        Iterator<CurrencyPair> iterator = list.iterator();
        while (iterator.hasNext()) {
            CurrencyPair pair = iterator.next();
            if (pair.getPairs().equals(currencyPair.getPairs())) {
                iterator.remove();
                break;
            }
        }
        list.add(0, currencyPair);
        String json = mGson.toJson(list);
        Preference.get().setSearchRecordsForUserOrDeviceId(getId(), json);
    }

    private String getId() {
        return LocalUser.getUser().isLogin() ?
                LocalUser.getUser().getUserInfo().getUserId() :
                AppInfo.getDeviceHardwareId(App.getAppContext());
    }

    /**
     * 更新本地保存的搜索记录添加自选情况
     *
     * @param pair
     */
    public void updateRecord(CurrencyPair pair) {
        List<CurrencyPair> list = getRecordsByUserOrDeviceId();
        for (CurrencyPair cp : list) {
            if (cp.getPairs().equals(pair.getPairs())) {
                cp.setAddOptional(pair.isAddOptional());
                break;
            }
        }
        String json = mGson.toJson(list);
        Preference.get().setSearchRecordsForUserOrDeviceId(getId(), json);
    }
}
