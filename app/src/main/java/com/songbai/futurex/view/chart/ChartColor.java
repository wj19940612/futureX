package com.songbai.futurex.view.chart;

import android.util.SparseIntArray;

/**
 * Modified by john on 08/04/2018
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class ChartColor {

    private int baseLineColor;
    private int positiveColor;
    private int negativeColor;
    private int baseTextColor;
    private int closePriceColor;
    private int crossLineColor;
    private int crossLineDateColor;
    private int crossLinePriceColor;

    private SparseIntArray masColor;

    public void setChartColor(ChartColor color) {
        baseLineColor = color.baseLineColor;
        positiveColor = color.positiveColor;
        negativeColor = color.negativeColor;
        baseTextColor = color.baseTextColor;
        closePriceColor = color.closePriceColor;
        crossLineColor = color.crossLineColor;
        crossLineDateColor = color.crossLineDateColor;
        crossLinePriceColor = color.crossLinePriceColor;
        masColor = color.masColor;
    }

    public ChartColor() {
        this.masColor = new SparseIntArray(3);
    }

    public int getBaseLineColor() {
        return baseLineColor;
    }

    public int getPositiveColor() {
        return positiveColor;
    }

    public int getNegativeColor() {
        return negativeColor;
    }

    public int getBaseTextColor() {
        return baseTextColor;
    }

    public int getClosePriceColor() {
        return closePriceColor;
    }

    public void setMaColor(int ma, int maColor) {
        masColor.put(ma, maColor);
    }

    public int getCrossLineColor() {
        return crossLineColor;
    }

    public int getMaColor(int ma) {
        return masColor.get(ma);
    }

    public int getCrossLineDateColor() {
        return crossLineDateColor;
    }

    public int getCrossLinePriceColor() {
        return crossLinePriceColor;
    }

    public void setCrossLineColor(int crossLineColor) {
        this.crossLineColor = crossLineColor;
    }

    public void setBaseLineColor(int baseLineColor) {
        this.baseLineColor = baseLineColor;
    }

    public void setPositiveColor(int positiveColor) {
        this.positiveColor = positiveColor;
    }

    public void setNegativeColor(int negativeColor) {
        this.negativeColor = negativeColor;
    }

    public void setBaseTextColor(int baseTextColor) {
        this.baseTextColor = baseTextColor;
    }

    public void setClosePriceColor(int closePriceColor) {
        this.closePriceColor = closePriceColor;
    }

    public void setCrossLineDateColor(int crossLineDateColor) {
        this.crossLineDateColor = crossLineDateColor;
    }

    public void setCrossLinePriceColor(int crossLinePriceColor) {
        this.crossLinePriceColor = crossLinePriceColor;
    }
}
