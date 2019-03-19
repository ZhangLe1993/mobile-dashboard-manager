package com.aihuishou.bi.md.front.chart.gmv;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Component
public class GmvService {

    @Resource
    @Qualifier("gp")
    private DataSource gp;

    //要ban掉的gmv 类型
    private List<String> banGmvType = new ArrayList<>();

    {
        banGmvType.add("加盟店");
    }

    @Cacheable("gmv/querySummary")
    public List<SummaryBean> querySummary() {
        try {
            //最近2日，上月同比日
            Date dataDate = getLastDataDate();//最新数据日期
            Calendar now = Calendar.getInstance();
            now.setTime(dataDate);
            now.add(Calendar.DAY_OF_MONTH, -1);
            Date contrast = new Date(now.getTime().getTime());//最新数据日期 上一日
            now.add(Calendar.DAY_OF_MONTH, 1);
            now.add(Calendar.MONTH, -1);
            Date monthContrast = new Date(now.getTime().getTime());//上月同日对比

            FutureTask<Map<String, List<GmvDayData>>> aC = submitQuery(dataDate);
            FutureTask<Map<String, List<GmvDayData>>> bC = submitQuery(contrast);
            FutureTask<Map<String, List<GmvDayData>>> cC = submitQuery(monthContrast);

            Map<String, List<GmvDayData>> a = aC.get();//当前
            Map<String, List<GmvDayData>> b = bC.get();//上一日
            Map<String, List<GmvDayData>> c = cC.get();//上月同一天

            List<SummaryBean> summaryList = a.keySet().stream().map(it -> {
                SummaryBean summaryBean = new SummaryBean();
                summaryBean.setLabel(it);
                summaryBean.setValue(a.get(it).get(0).getAmountDay());
                summaryBean.setMonthTarget(a.get(it).get(0).getTarget());
                summaryBean.setMonthAccumulation(a.get(it).get(0).getAmountToNow());
                if (b.get(it) != null) summaryBean.setValueContrast(b.get(it).get(0).getAmountDay());
                if (c.get(it) != null) summaryBean.setMonthAccumulationContrast(c.get(it).get(0).getAmountToNow());
                return summaryBean;
            }).collect(Collectors.toList());
            //sum
            SummaryBean sum = new SummaryBean();
            sum.setLabel("GMV");
            sum.setIcon("");
            summaryList.stream().reduce(sum, (a1, a2) -> {
                a1.setValue(a1.getValue() + a2.getValue());
                a1.setValueContrast(a1.getValueContrast() + a2.getValueContrast());
                a1.setMonthTarget(a1.getMonthTarget() + a2.getMonthTarget());
                a1.setMonthAccumulation(a1.getMonthAccumulation() + a2.getMonthAccumulation());
                a1.setMonthAccumulationContrast(a1.getMonthAccumulation() + a2.getMonthAccumulation());
                return a1;
            });
            summaryList.add(0, sum);
            return summaryList;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Cacheable("summary/getLastDataDate")
    public Date getLastDataDate() {
        String sql = "select report_date from rpt.rpt_b2b_gmv_day order by report_date desc limit 1";
        Date dataDate = null;
        try {
            dataDate = new QueryRunner(gp).query(sql, new ResultSetHandler<Date>() {
                @Override
                public Date handle(ResultSet resultSet) throws SQLException {
                    resultSet.next();
                    return resultSet.getDate("report_date");
                }
            });
            return dataDate;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<GmvDayData> queryDetail(Date from, Date to, String gmvType) {
        String sql = "select t1.report_date as reportDate,\n" +
                "         t1.gmv_type as gmvType,\n" +
                "         case when gmv_type='海外' then  t1.settle_amount_num_day*t2.exchange_rate \n" +
                "           else t1.settle_amount_num_day end  as amountDay,\n" +
                "     case when gmv_type='海外' then  t1.settle_amount_num_to_now*t2.exchange_rate \n" +
                "           else t1.settle_amount_num_to_now end as amountToNow,\n" +
                "         t3.gmv_target as target\n" +
                "from rpt.rpt_b2b_gmv_day t1\n" +
                "join dim.dim_exchange_rate  t2 on t1.report_date =t2.report_date\n" +
                "left join dim.dim_b2b_gmv_target_month t3 on substr(t1.report_date,1,7)=t3.month and t1.gmv_type=t3.business_unit\n" +
                "where t1.report_date between ? and ?" + (gmvType != null ? " and t1.gmv_type=? " : "");
        Object[] params = gmvType == null ? new Object[]{from, to} : new Object[]{from, to, gmvType};
        try {
            List<GmvDayData> arr = new QueryRunner(gp).query(sql, new BeanListHandler<>(GmvDayData.class), params);
            return arr.stream().filter(it -> !banGmvType.contains(it.getGmvType())).collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<GmvDayData> queryDetail(Date day) throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(day);
        cal.set(Calendar.HOUR_OF_DAY,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        Date from = new Date(cal.getTime().getTime());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date to = new Date(cal.getTime().getTime());
        return queryDetail(from, to, null);
    }

    private FutureTask<Map<String, List<GmvDayData>>> submitQuery(Date queryDate) {
        FutureTask<Map<String, List<GmvDayData>>> futureTask = new FutureTask(() -> {
            return queryDetail(queryDate).stream().collect(Collectors.groupingBy(it -> it.getGmvType()));
        });
        new Thread(futureTask).run();
        return futureTask;
    }
}
