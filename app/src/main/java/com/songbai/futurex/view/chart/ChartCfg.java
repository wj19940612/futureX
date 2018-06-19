package com.songbai.futurex.view.chart;

public class ChartCfg {

    private int mBaseLines;
    private int mIndexesBaseLines;
    private boolean mIndexesEnable;
    private int mNumberScale;
    private int mXAxis;
    private boolean mEnableDrag;
    private boolean mEnableCrossLine;
    private float[] mBaseLineArray;
    private double[] mIndexesBaseLineArray;

    public void setChartCfg(ChartCfg cfg) {
        setBaseLines(cfg.getBaseLines());
        setIndexesBaseLines(cfg.getIndexesBaseLines());
        setIndexesEnable(cfg.isIndexesEnable());
        setNumberScale(cfg.getNumberScale());
        setXAxis(cfg.getXAxis());
        setEnableDrag(cfg.isEnableDrag());
        setEnableCrossLine(cfg.isEnableCrossLine());
    }

    public ChartCfg() {
        mBaseLines = 2;
        mIndexesBaseLines = 2;
        mNumberScale = 2;
        mXAxis = 0;
        mBaseLineArray = new float[0];
        mIndexesBaseLineArray = new double[0];
    }

    public void setBaseLines(int baseLines) {
        mBaseLines = baseLines;
        if (mBaseLineArray.length != baseLines) {
            mBaseLineArray = new float[baseLines];
        }
    }

    public void setIndexesBaseLines(int indexesBaseLines) {
        mIndexesBaseLines = indexesBaseLines;
        if (mIndexesBaseLineArray.length != indexesBaseLines) {
            mIndexesBaseLineArray = new double[indexesBaseLines];
        }
    }

    public int getBaseLines() {
        return mBaseLines;
    }

    public int getIndexesBaseLines() {
        return mIndexesBaseLines;
    }

    public float[] getBaseLineArray() {
        return mBaseLineArray;
    }

    public double[] getIndexesBaseLineArray() {
        return mIndexesBaseLineArray;
    }

    public boolean isIndexesEnable() {
        return mIndexesEnable;
    }

    public void setIndexesEnable(boolean indexesEnable) {
        mIndexesEnable = indexesEnable;
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
}
