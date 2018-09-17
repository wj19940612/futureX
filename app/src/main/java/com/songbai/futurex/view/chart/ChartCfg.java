package com.songbai.futurex.view.chart;

public class ChartCfg {

    private int mBaseLines;
    private int mVolBaseLines;
    private boolean mVolEnable;
    private int mNumberScale;
    private int mXAxis;
    private boolean mEnableDrag;
    private boolean mEnableCrossLine;
    private float[] mBaseLineArray;
    private double[] mIndexesBaseLineArray;
    private float mViewScale;
    private boolean mIndexEnable;

    public void setChartCfg(ChartCfg cfg) {
        setBaseLines(cfg.getBaseLines());
        setVolBaseLines(cfg.getVolBaseLines());
        setVolEnable(cfg.isVolEnable());
        setNumberScale(cfg.getNumberScale());
        setXAxis(cfg.getXAxis());
        setEnableDrag(cfg.isEnableDrag());
        setEnableCrossLine(cfg.isEnableCrossLine());
        setViewScale(cfg.getViewScale());
    }

    public ChartCfg() {
        mBaseLines = 2;
        mVolBaseLines = 2;
        mNumberScale = 2;
        mXAxis = 0;
        mBaseLineArray = new float[0];
        mIndexesBaseLineArray = new double[0];
        mViewScale = 1;
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

    public void setVolBaseLines(int volBaseLines) {
        mVolBaseLines = volBaseLines;
        if (mIndexesBaseLineArray.length != volBaseLines) {
            mIndexesBaseLineArray = new double[volBaseLines];
        }
    }

    public int getBaseLines() {
        return mBaseLines;
    }

    public int getVolBaseLines() {
        return mVolBaseLines;
    }

    public float[] getBaseLineArray() {
        return mBaseLineArray;
    }

    public double[] getVolBaseLineArray() {
        return mIndexesBaseLineArray;
    }

    public boolean isVolEnable() {
        return mVolEnable;
    }

    public void setVolEnable(boolean volEnable) {
        mVolEnable = volEnable;
    }

    public boolean isIndexEnable() {
        return mIndexEnable;
    }

    public void setIndexEnable(boolean indexEnable) {
        mIndexEnable = indexEnable;
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
}
