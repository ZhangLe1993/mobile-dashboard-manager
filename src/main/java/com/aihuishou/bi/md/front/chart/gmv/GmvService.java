package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.front.cache.CacheMd;
import com.aihuishou.bi.md.front.notice.GroupMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GmvService {

    @Resource
    @Qualifier("gp")
    private DataSource gp;

    @Resource
    private GmvDataDateService gmvDataDateService;

    @Resource
    private DataSource mysql;
    //要ban掉的gmv 类型
    private List<String> banGmvType = new ArrayList<>();

    {
        banGmvType.add("加盟店");
    }

    private List<String> countImDisplay = new ArrayList<>();
    {
        countImDisplay.add("其他");
    }

    // .parallelStream().filter(p -> !countImDisplay.contains(p.getGmvType())).collect(Collectors.toList())
    @CacheMd
    public List<SummaryBean> querySummary(String service) {
        try {
            //最近2日，上月同比日
            Date dataDate = gmvDataDateService.getLastDataDate(service);//最新数据日期
            Calendar now = Calendar.getInstance();
            now.setTime(dataDate);
            now.add(Calendar.DAY_OF_MONTH, -1);
            Date contrast = new Date(now.getTime().getTime());//最新数据日期 上一日
            now.add(Calendar.DAY_OF_MONTH, 1);
            now.add(Calendar.MONTH, -1);
            Date monthContrast = new Date(now.getTime().getTime());//上月同日对比
            FutureTask<Map<String, List<GmvDayData>>> aC = submitQuery(dataDate, service);
            FutureTask<Map<String, List<GmvDayData>>> bC = submitQuery(contrast, service);
            FutureTask<Map<String, List<GmvDayData>>> cC = submitQuery(monthContrast, service);

            Map<String, List<GmvDayData>> a = aC.get();//当前
            Map<String, List<GmvDayData>> b = bC.get();//上一日
            Map<String, List<GmvDayData>> c = cC.get();//上月同一天


            Map<String, String> icon = getIcons();
            List labels = new ArrayList(icon.keySet());

            // && ("gmv".equalsIgnoreCase(it) || !countImDisplay.contains(it))
            List<SummaryBean> summaryList = a.keySet().stream()
                    .filter(it -> labels.contains(it))
                    .sorted((c1, c2) -> labels.indexOf(c1) - labels.indexOf(c2))
                    .map(it -> {
                        SummaryBean summaryBean = new SummaryBean();
                        summaryBean.setLabel(it);
                        summaryBean.setIcon(icon.get(it));
                        //当前值
                        summaryBean.setValue(a.get(it).get(0).getAmountDay());
                        //月目标
                        summaryBean.setMonthTarget(a.get(it).get(0).getTarget());
                        //月累计值
                        summaryBean.setMonthAccumulation(a.get(it).get(0).getAmountToNow());
                        if (b.get(it) != null) {
                            //对比值
                            summaryBean.setValueContrast(b.get(it).get(0).getAmountDay());
                        }
                        if (c.get(it) != null) {
                            //月累计对比值
                            summaryBean.setMonthAccumulationContrast(c.get(it).get(0).getAmountToNow());
                        }
                        return summaryBean;
                    }).collect(Collectors.toList());
            //sum
            SummaryBean sum = new SummaryBean();
            sum.setLabel("GMV");
            sum.setIcon(icon.get("GMV"));
            summaryList.stream().reduce(sum, (a1, a2) -> {
                a1.setValue(a1.getValue() + a2.getValue());
                a1.setValueContrast(a1.getValueContrast() + a2.getValueContrast());
                a1.setMonthTarget(a1.getMonthTarget() + a2.getMonthTarget());
                a1.setMonthAccumulation(a1.getMonthAccumulation() + a2.getMonthAccumulation());
                a1.setMonthAccumulationContrast(a1.getMonthAccumulationContrast() + a2.getMonthAccumulationContrast());
                return a1;
            });
            summaryList.add(0, sum);
            return summaryList;
        } catch (InterruptedException e) {
            log.error("", e);
        } catch (ExecutionException e) {
            log.error("", e);
        }
        return new ArrayList<>();
    }

    /**
     * @return gmv_type->icon
     */
    private Map<String, String> getIcons() {
        Map<String, String> icons = new LinkedHashMap<>();
        String sql = "select gmv_type,gmv_icon from gmv_type_config order by order_no";
        try {
            List<Map<String, Object>> rs = new QueryRunner(mysql).query(sql, new MapListHandler());
            for (Map<String, Object> r : rs) {
                String gmvType = r.get("gmv_type").toString();
                String gmvIcon = r.get("gmv_icon").toString();
                icons.put(gmvType, gmvIcon);
            }
        } catch (SQLException e) {
            log.error("", e);
        }
        return icons;
    }

    public Set<String> allGmvType() {
        Set<String> allTypes = getIcons().keySet();
        allTypes.removeAll(banGmvType);
        return new HashSet(allTypes);
    }



    @CacheMd
    public List<GmvDayData> queryDetail(Date from, Date to, String gmvType, String service) {
        String sql = buildStatement(service, gmvType);
        Object[] params = gmvType == null ? new Object[]{from, to} : new Object[]{from, to, gmvType};
        try {
            List<GmvDayData> arr = new QueryRunner(gp).query(sql, new BeanListHandler<>(GmvDayData.class), params);
            return arr.stream().filter(it -> !banGmvType.contains(it.getGmvType())).collect(Collectors.toList());
        } catch (SQLException e) {
            log.error("", e);
            return new ArrayList<>();
        }
    }

    private String buildStatement(String service, String gmvType) {
        String change = "t1.gmv_type as gmvType,t1.settle_amount_num_day as amountDay,t1.settle_amount_num_to_now as amountToNow,";
        String mainTable = "rpt.rpt_b2b_gmv_day ";
        String targetTable = "dim.dim_b2b_gmv_target_month";
        String filter = "t1.gmv_type";
        if(!GroupMapping.BTB.getKey().equalsIgnoreCase(service)) {
            if(GroupMapping.CTB_1.getKey().equalsIgnoreCase(service)) {
                change = "t1.business_unit AS gmvType,t1.settle_amount_num_day AS amountDay,t1.settle_amount_num_to_now AS amountToNow,";
            }else {
                change = "t1.business_unit AS gmvType,t1.settle_order_num_day AS amountDay,t1.settle_order_num_to_now AS amountToNow,";
            }
            mainTable = "rpt.rpt_c2b_gmv_day ";
            targetTable = "dim.dim_c2b_gmv_target_month";
            filter = "t1.business_unit";
        }
        return "select t1.report_date as reportDate," + change +
                "t3.gmv_target as target from " + mainTable +
                "t1 left join " + targetTable + " t3 on substr(t1.report_date,1,7)=t3.month " +
                "and " + filter + "=t3.business_unit " +
                "where t1.report_date between ? and ?" + (gmvType != null ? " and " + filter + "=? " : "");
    }


    public List<GmvDayData> queryDetail(Date day, String service) throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date from = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date to = new Date(cal.getTime().getTime());
        return queryDetail(from, to, null, service);
    }

    private FutureTask<Map<String, List<GmvDayData>>> submitQuery(Date queryDate, String service) {
        FutureTask<Map<String, List<GmvDayData>>> futureTask = new FutureTask(() -> {
            return queryDetail(queryDate, service).stream().collect(Collectors.groupingBy(it -> it.getGmvType()));
        });
        new Thread(futureTask).run();
        return futureTask;
    }
}
