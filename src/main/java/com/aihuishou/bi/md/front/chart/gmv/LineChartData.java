package com.aihuishou.bi.md.front.chart.gmv;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineChartData implements Serializable {
    private String title = "";
    private Map<String,List<String>> xAxis = new HashMap<>();
    private List<Series> series = new ArrayList();

    public static class Series {
        private String name;
        private List data;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List getData() {
            return data;
        }

        public void setData(List data) {
            this.data = data;
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String,List<String>> getxAxis() {
        return xAxis;
    }

    public void setxAxis(Map<String,List<String>> xAxis) {
        this.xAxis = xAxis;
    }

    public List<Series> getSeries() {
        return series;
    }

    public void setSeries(List<Series> series) {
        this.series = series;
    }

    /*public void putxAxis(String key, List<String> xAxi) {
        xAxis.put(key, xAxi);
    }*/

}



