package com.aihuishou.bi.md.front.chart.gmv;

import java.io.Serializable;

public class SummaryBean implements Serializable {
    private Long value=0L;//当前值
    private Long valueContrast=0L;//对比值
    private String icon="";//图标
    private String label="";//标签名
    private Long monthTarget=0L;//月目标
    private Long monthAccumulation=0L;//月累计值
    private Long monthAccumulationContrast=0L;//月累计对比值

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getValueContrast() {
        return valueContrast;
    }

    public void setValueContrast(Long valueContrast) {
        this.valueContrast = valueContrast;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getMonthTarget() {
        return monthTarget;
    }

    public void setMonthTarget(Long monthTarget) {
        this.monthTarget = monthTarget;
    }

    public Long getMonthAccumulation() {
        return monthAccumulation;
    }

    public void setMonthAccumulation(Long monthAccumulation) {
        this.monthAccumulation = monthAccumulation;
    }

    public Long getMonthAccumulationContrast() {
        return monthAccumulationContrast;
    }

    public void setMonthAccumulationContrast(Long monthAccumulationContrast) {
        this.monthAccumulationContrast = monthAccumulationContrast;
    }
}
