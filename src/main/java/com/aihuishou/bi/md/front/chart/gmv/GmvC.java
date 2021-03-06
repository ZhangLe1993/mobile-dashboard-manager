package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.front.chart.enums.ServiceValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/front/gmv")
public class GmvC {

    @Resource
    private GmvService gmvService;

    @Resource
    private IconService iconService;

    @Resource
    private GmvDataDateService gmvDataDateService;

    @RequestMapping("/summary")
    public ResponseEntity summary(@RequestParam(value="service_type", required = false, defaultValue = "b2b") String service) throws Exception {
        Date lastDataDate = gmvDataDateService.getLastDataDate(service);
        Calendar cal = Calendar.getInstance();
        cal.setTime(lastDataDate);
        double monthProgress = (double) cal.get(Calendar.DAY_OF_MONTH) / cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        DecimalFormat nf = new DecimalFormat("00.00%");
        List<SummaryBean> summary = gmvService.querySummary(service);
        Map<String, Object> result = new HashMap();
        result.put("date", new SimpleDateFormat("yyyy-MM-dd").format(lastDataDate));
        result.put("date_progress", nf.format(monthProgress));
        result.put("data", summary);
        result.put("panel_key","GMV");
        if(ServiceValue.CTB_1.getKey().equalsIgnoreCase(service)){
            result.put("panel_key","单量");
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }



    @RequestMapping("/month_day")
    public ResponseEntity monthDay(@RequestParam(value = "type") String gmvType,
                                   @RequestParam(value="service_type", required = false, defaultValue = "b2b") String service) throws Exception {
        List<LineChartData> lineCharts = new ArrayList<>();
        Date now = gmvDataDateService.getLastDataDate(service);//当前最新数据日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.DAY_OF_MONTH, 1);//本月初
        Date b = new Date(cal.getTime().getTime() - 1);//上月末
        cal.add(Calendar.MONTH, -1);
        Date a = new Date(cal.getTime().getTime());//上月初
        cal.add(Calendar.MONTH,1);
        cal.add(Calendar.YEAR,-1);
        Date lastYearMonthBegin=new Date(cal.getTimeInMillis());//去年同月初
        cal.add(Calendar.MONTH,1);
        Date lastYearMonthEnd=new Date(cal.getTime().getTime() - 1);;//去年同月末

        //上月初至今的数据
        List<GmvDayData> data=getDetailData(gmvType, service, a,now);

        //Map<String,List<String>> day
        LineChartData dayLine = new LineChartData();//每日数据折线
        LineChartData accLine = new LineChartData();//累计值数据折线
        if(ServiceValue.CTB_1.getKey().equalsIgnoreCase(service) && "GMV".equalsIgnoreCase(gmvType)) {
            dayLine.setTitle("单量每日");
            accLine.setTitle("单量月累计");
        } else {
            dayLine.setTitle(gmvType+"每日");
            accLine.setTitle(gmvType+"月累计");
        }

        List<String> xArr = getFullMonthDate(now);
        dayLine.setxAxis(xArr);
        accLine.setxAxis(xArr);
        dayLine.getxAxisArr().put("cur", xArr);//本月设X轴
        accLine.getxAxisArr().put("cur", xArr);
        //本月每天
        LineChartData.Series s1 = getFullMonthDayData(now, data, it -> {
            return it.getAmountDay();
        });
        if(ServiceValue.CTB_1.getKey().equalsIgnoreCase(service) && "GMV".equalsIgnoreCase(gmvType)) {
            s1.setName("本月单量");
        } else {
            s1.setName("本月" + gmvType);
        }
        dayLine.getSeries().add(s1);
        //本月累计
        LineChartData.Series acc1 = getFullMonthDayData(now, data, it -> {
            return it.getAmountToNow();
        });
        acc1.setName("本月"/*+gmvType+"累计"*/);
        accLine.getSeries().add(acc1);

        List<String> xArr2 = getFullMonthDate(b);
        dayLine.getxAxisArr().put("pre", xArr2);//本月设X轴
        accLine.getxAxisArr().put("pre", xArr2);
        //dayLine.setxAxis(xArr2);//本月设X轴
        //accLine.setxAxis(xArr2);
        //上月每天
        LineChartData.Series s2 = getFullMonthDayData(b, data, it -> {
            return it.getAmountDay();
        });
        if(ServiceValue.CTB_1.getKey().equalsIgnoreCase(service) && "GMV".equalsIgnoreCase(gmvType)) {
            s2.setName("上月单量");
        } else {
            s2.setName("上月" + gmvType);
        }
        if (s2.getData().size() > xArr2.size()) {
            s2.setData(s2.getData().subList(0, xArr2.size()));
        }
        dayLine.getSeries().add(s2);
        //上月累计
        LineChartData.Series acc2 = getFullMonthDayData(b, data, it -> {
            return it.getAmountToNow();
        });
        acc2.setName("上月"/* + gmvType+"累计"*/);
        if (acc2.getData().size() > xArr2.size()) {
            acc2.setData(acc2.getData().subList(0, xArr2.size()));
        }
        accLine.getSeries().add(acc2);

        List<String> xArr3 = getFullMonthDate(lastYearMonthEnd);
        dayLine.getxAxisArr().put("last", xArr3);//本月设X轴
        accLine.getxAxisArr().put("last", xArr3);
        dayLine.setxAxis(xArr3);//本月设X轴
        accLine.setxAxis(xArr3);
        //去年同月
        List<GmvDayData> lastYearData = getDetailData(gmvType, service, lastYearMonthBegin, lastYearMonthEnd);
        LineChartData.Series acc3 = getFullMonthDayData(lastYearMonthEnd, lastYearData, it -> {
            return it.getAmountToNow();
        });
        acc3.setName("去年同月"/* + gmvType+"累计"*/);
        if (acc3.getData().size() > xArr3.size()) {
            acc3.setData(acc3.getData().subList(0, xArr3.size()));
        }
        accLine.getSeries().add(acc3);
        //添加每天+累计的折线图
        lineCharts.add(dayLine);
        lineCharts.add(accLine);
        return new ResponseEntity(lineCharts, HttpStatus.OK);
    }

    private List<GmvDayData> getDetailData(String gmvType, String service, Date from,Date to) throws Exception {
        List<GmvDayData> data;
        ServiceValue serviceName = ServiceValue.fromType(service);
        List<String> list = MergeItemService.getNeedMergeCollect(serviceName);
        Set<String> childrenLabel = MergeItemService.getNeedMergeItem(serviceName, gmvType);
        if(list.contains(gmvType) && childrenLabel.size() > 0) {
            data = childrenLabel.parallelStream().flatMap(t -> {
                return gmvService.queryDetail(from, to, t, service).stream();
            }).collect(Collectors.groupingBy(it -> it.getReportDate()))
                    .entrySet().stream()
                    .map(it -> {
                        GmvDayData v = new GmvDayData();
                        v.setReportDate(it.getKey());
                        v.setGmvType(gmvType);
                        return it.getValue().stream().reduce(v, (a1, a2) -> {
                            a1.setAmountDay(a1.getAmountDay() + a2.getAmountDay());
                            a1.setAmountToNow(a1.getAmountToNow() + a2.getAmountToNow());
                            return a1;
                        });
                    }).collect(Collectors.toList());
        } else {
            data = gmvService.queryDetail(from, to, gmvType, service);
        }
        return data;
    }


    /**
     * 获取截止时间所在月从月初开始的所有数据
     *
     * @param end  截止时间(包含)
     * @param data 原始数据
     * @param item 要计算的项
     * @return
     */
    private LineChartData.Series getFullMonthDayData(Date end, List<GmvDayData> data, Function<GmvDayData, Object> item) {
        List<String> xArr = getFullMonthDate(end);
        Map<String, Object> points = new HashMap<>();
        data.stream().forEach(it -> {
            points.put(it.getReportDate(), item.apply(it));
        });
        List<Object> d = xArr.stream().map(it -> {
            try {
                if (new SimpleDateFormat("yyyy-MM-dd").parse(it).getTime() > end.getTime()) {
                    return null;
                } else {
                    Object v = points.get(it);
                    return v == null ? 0 : v;
                }
            } catch (ParseException e) {
                log.error("", e);
                return null;
            }
        }).filter(it -> it != null).collect(Collectors.toList());
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
//        while (!(cal.getTime().getTime() > end.getTime())) {//不超过截止时间
        int nowMonth = cal.get(Calendar.MONTH);
        while (cal.get(Calendar.MONTH) == nowMonth) {//截止本月末
            arr.add(dayFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }
        return arr;
    }
}
