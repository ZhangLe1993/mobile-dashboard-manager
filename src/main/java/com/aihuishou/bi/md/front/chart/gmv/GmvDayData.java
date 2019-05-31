package com.aihuishou.bi.md.front.chart.gmv;

import java.io.Serializable;

public class GmvDayData implements Serializable {
    private String reportDate;
    private String gmvType;
    private Long amountDay=0L;//当日值
    private Long amountToNow=0L;//当月累计值
    private Long target;//月目标

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String reportDate) {
        this.reportDate = reportDate;
    }

    public String getGmvType() {
        return gmvType;
    }

    public void setGmvType(String gmvType) {
        this.gmvType = gmvType;
    }

    public Long getAmountDay() {
        return amountDay;
    }

    public void setAmountDay(Long amountDay) {
        this.amountDay = amountDay;
    }

    public Long getAmountToNow() {
        return amountToNow==null?0:amountToNow;
    }

    public void setAmountToNow(Long amountToNow) {
        this.amountToNow = amountToNow;
    }

    public Long getTarget() {
        return target;
    }

    public void setTarget(Long target) {
        this.target = target;
    }
}
