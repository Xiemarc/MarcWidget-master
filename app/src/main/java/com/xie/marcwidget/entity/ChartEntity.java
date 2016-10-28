package com.xie.marcwidget.entity;

/**
 * author: marc
 * date:  2016/10/28 22:21
 * email：aliali_ha@yeah.net
 */
public class ChartEntity {
    private String xLabel;
    private Float yValue;

    public ChartEntity(String xLabel, Float yValue) {
        this.xLabel = xLabel;
        this.yValue = yValue;
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public ChartEntity(Float yValue) {
        this.yValue = yValue;
    }

    public Float getyValue() {
        return yValue;
    }

    public void setyValue(Float yValue) {
        this.yValue = yValue;
    }
}
