package com.songbai.futurex.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Launcher {

    private static Launcher sInstance;

    private Context mContext;

    private Intent mIntent;

    private Launcher() {
        mIntent = new Intent();
    }

    public static Launcher with(Fragment fragment, Class<?> clazz) {
        sInstance = new Launcher();
        sInstance.mContext = new WeakReference<>(fragment.getActivity()).get();
        sInstance.mIntent.setClass(sInstance.mContext, clazz);
        return sInstance;
    }

    public static Launcher with(Context context, Class<?> clazz) {
        sInstance = new Launcher();
        sInstance.mContext = new WeakReference<>(context).get();
        sInstance.mIntent.setClass(sInstance.mContext, clazz);
        return sInstance;
    }

    public Launcher putExtra(String key, ArrayList<? extends Parcelable> value) {
        mIntent.putParcelableArrayListExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, Parcelable parcelable) {
        mIntent.putExtra(key, parcelable);
        return this;
    }

    public Launcher putExtra(String key, int value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, long value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, double value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, String value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, boolean value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, String[] value) {
        mIntent.putExtra(key, value);
        return this;
    }

    public Launcher putExtra(String key, Bundle bundle) {
        mIntent.putExtra(key, bundle);
        return this;
    }

    public Launcher setFlags(int flag) {
        mIntent.setFlags(flag);
        return this;
    }

    public Launcher addFlags(int flag) {
        mIntent.addFlags(flag);
        return this;
    }

    public void execute() {
        if (mContext != null) {
            mContext.startActivity(mIntent);
        }
    }

    public void execute(int requestCode) {
        if (mContext != null) {
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                activity.startActivityForResult(mIntent, requestCode);
            }
        }
    }

    public void execute(Fragment fragment, int requestCode) {
        if (mContext != null && fragment != null) {
            fragment.startActivityForResult(mIntent, requestCode);
        }
    }

    public void executeForResult(int requestCode) {
        if (mContext != null) {
            if (mContext instanceof Activity) {
                Activity activity = (Activity) mContext;
                activity.startActivityForResult(mIntent, requestCode);
            }
        }
    }
}
