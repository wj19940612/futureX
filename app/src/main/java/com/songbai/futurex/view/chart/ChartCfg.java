package com.songbai.futurex.view.chart;

public class ChartCfg {

    public static final int INDEX_NONE = -1;
    public static final int INDEX_MA = 1;
    public static final int INDEX_BOLL = 2;
    public static final int INDEX_MACD = 3;
    public static final int INDEX_KDJ = 4;
    public static final int INDEX_RSI = 5;
    public static final int INDEX_WR = 6;

    private int mBaseLines;
    private int mVolBaseLines;
    private int mSubBaseLines;
    private boolean mVolEnable;
    private int mNumberScale;
    private int mXAxis;
    private boolean mEnableDrag;
    private boolean mEnableCrossLine;
    private float[] mBaseLineArray;
    private double[] mVolBaseLineArray;
    private float[] mSubBaseLineArray;
    private float mViewScale;
    private int mMainIndex;
    private int mSubIndex;

    public void setChartCfg(ChartCfg cfg) {
        setBaseLines(cfg.getBaseLines());
        setVolBaseLines(cfg.getVolBaseLines());
        setSubBaseLines(cfg.getSubBaseLines());
        setVolEnable(cfg.isVolEnable());
        setNumberScale(cfg.getNumberScale());
        setXAxis(cfg.getXAxis());
        setEnableDrag(cfg.isEnableDrag());
        setEnableCrossLine(cfg.isEnableCrossLine());
        setViewScale(cfg.getViewScale());
    }

    public ChartCfg() {
        mBaseLines = 0;
        mVolBaseLines = 0;
        mSubBaseLines = 0;
        mNumberScale = 2;
        mXAxis = 0;
        mBaseLineArray = new float[0];
        mVolBaseLineArray = new double[0];
        mSubBaseLineArray = new float[0];
        mViewScale = 1;
        mMainIndex = INDEX_MA;
        mSubIndex = INDEX_NONE;
    }

    public void setViewScale(float viewScale) {
        mViewScale = viewScale;
    }

    public void setBaseLines(int baseLines) {
        mBaseLines = baseLines;
        if (mBaseLineArray.length != baseLines) {
            mBaseLineArray = new float[baseLines];
        }
    }

    public void setSubBaseLines(int subBaseLines) {
        mSubBaseLines = subBaseLines;
        if (mSubBaseLineArray.length != subBaseLines) {
            mSubBaseLineArray = new float[subBaseLines];
        }
    }

    public void setVolBaseLines(int volBaseLines) {
        mVolBaseLines = volBaseLines;
        if (mVolBaseLineArray.length != volBaseLines) {
            mVolBaseLineArray = new double[volBaseLines];
        }
    }

    public int getBaseLines() {
        return mBaseLines;
    }

    public int getVolBaseLines() {
        return mVolBaseLines;
    }

    public int getSubBaseLines() {
        return mSubBaseLines;
    }

    public float[] getBaseLineArray() {
        return mBaseLineArray;
    }

    public double[] getVolBaseLineArray() {
        return mVolBaseLineArray;
    }

    public float[] getSubBaseLineArray() {
        return mSubBaseLineArray;
    }

    public boolean isVolEnable() {
        return mVolEnable;
    }

    public void setVolEnable(boolean volEnable) {
        mVolEnable = volEnable;
    }

    public boolean isSubIndexEnable() {
        return mSubIndex > 0;
    }

    public int getNumberScale() {
        return mNumberScale;
    }

    public void setNumberScale(int numberScale) {
        mNumberScale = numberScale;
    }

    public int getXAxis() {
        return mXAxis;
    }

    public void setXAxis(int XAxis) {
        mXAxis = XAxis;
    }

    public boolean isEnableDrag() {
        return mEnableDrag;
    }

    public void setEnableDrag(boolean enableDrag) {
        mEnableDrag = enableDrag;
    }

    public boolean isEnableCrossLine() {
        return mEnableCrossLine;
    }

    public void setEnableCrossLine(boolean enableCrossLine) {
        mEnableCrossLine = enableCrossLine;
    }

    public float getViewScale() {
        return mViewScale;
    }

    public int getMainIndex() {
        return mMainIndex;
    }

    public void setMainIndex(int mainIndex) {
        mMainIndex = mainIndex;
    }

    public int getSubIndex() {
        return mSubIndex;
    }

    public void setSubIndex(int subIndex) {
        mSubIndex = subIndex;
    }
}
