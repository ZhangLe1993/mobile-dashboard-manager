package com.aihuishou.bi.md.front.chart.gmv;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/front/gmv")
public class GmvC {

    @Resource
    private GmvService gmvService;

    @RequestMapping("/summary")
    public ResponseEntity summary() {
        Date lastDataDate = gmvService.getLastDataDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastDataDate);
        double monthProgress = (double) cal.get(Calendar.DAY_OF_MONTH) / cal.getMaximum(Calendar.DAY_OF_MONTH);
        DecimalFormat nf = new DecimalFormat("00.00%");
        List<SummaryBean> summary = gmvService.querySummary();
        Map<String, Object> result = new HashMap();
        result.put("date", new SimpleDateFormat("yyyy-MM-dd").format(lastDataDate));
        result.put("date_progress", nf.format(monthProgress));
        result.put("data", summary);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping("/month_day")
    public ResponseEntity monthDay(@RequestParam(value = "type") String gmvType) {
        List<LineChartData> lineCharts = new ArrayList<>();
        LineChartData line = new LineChartData();
        line.setTitle(gmvType);
        lineCharts.add(line);

        Date now = gmvService.getLastDataDate();//当前最新数据日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        cal.add(Calendar.MONTH, -1);//TODO

        Date b = new Date(cal.getTime().getTime() - 1);//上月末
        cal.add(Calendar.MONTH, -1);
        Date a = new Date(cal.getTime().getTime());//上月初

        //上月初至今的数据
        List<GmvDayData> data;
        if ("gmv".equalsIgnoreCase(gmvType)) {
            data = gmvService.allGmvType().parallelStream().flatMap(t -> {
                return gmvService.queryDetail(a, now, t).stream();
            }).collect(Collectors.groupingBy(it -> it.getReportDate()))
                    .entrySet().stream()
                    .map(it -> {
                        GmvDayData v = new GmvDayData();
                        v.setReportDate(it.getKey());
                        v.setGmvType("GMV");
                        return it.getValue().stream().reduce(v, (a1, a2) -> {
                            a1.setAmountDay(a1.getAmountDay() + a2.getAmountDay());//后续只会用到amountDay
                            return a1;
                        });
                    }).collect(Collectors.toList());
        } else {
            data = gmvService.queryDetail(a, now, gmvType);
        }

        List<String> xArr = getFullMonthDate(now);
        line.setxAxis(xArr);//本月设X轴
        LineChartData.Series s1 = getFullMonthDateData(now, data);
        s1.setName("本月" + gmvType);
        line.getSeries().add(s1);
        LineChartData.Series s2 = getFullMonthDateData(b, data);
        s2.setName("上月" + gmvType);
        if (s2.getData().size() > s1.getData().size()) {
            s2.setData(s2.getData().subList(0, s1.getData().size()));
        }
        line.getSeries().add(s2);
        return new ResponseEntity(lineCharts, HttpStatus.OK);
    }

    /**
     * 获取截止时间所在月从月初开始的所有数据
     *
     * @param end 截止时间(包含)
     * @return
     */
    private LineChartData.Series getFullMonthDateData(Date end, List<GmvDayData> data) {
        List<String> xArr = getFullMonthDate(end);
        Map<String, Object> points = new HashMap<>();
        data.stream().forEach(it -> {
            points.put(it.getReportDate(), it.getAmountDay());
        });
        List<Object> d = xArr.stream().map(it -> {
            Object v = points.get(it);
            return v == null ? 0 : v;
        }).collect(Collectors.toList());
        LineChartData.Series series = new LineChartData.Series();
        series.setData(d);
        return series;
    }

    private List<String> getFullMonthDate(Date end) {
        List<String> arr = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        while (!(cal.getTime().getTime() > end.getTime())) {//不超过截止时间
            arr.add(dayFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }
        return arr;
    }
}
