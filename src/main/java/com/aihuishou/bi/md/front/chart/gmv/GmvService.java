package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.core.QRunner;
import com.aihuishou.bi.md.front.cache.CacheMd;
import com.aihuishou.bi.md.front.chart.conf.Const;
import com.aihuishou.bi.md.front.chart.enums.MergeItem;
import com.aihuishou.bi.md.front.chart.enums.ServiceValue;
import com.aihuishou.bi.md.utils.EnumUtil;
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
import java.util.*;
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
    private IconService iconService;

    @Resource
    private DataSource mysql;

    // .parallelStream().filter(p -> !countImDisplay.contains(p.getGmvType())).collect(Collectors.toList())
    @CacheMd
    public List<SummaryBean> querySummary(String service) throws Exception {
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

            //实际用来分隔业务类型  B2b, c2b_0 ，c2b_1
            ServiceValue serviceName = ServiceValue.fromType(service);
            //B2b, 回收 ，换新
            //String serviceType = iconService.getServiceType(serviceName);
            //Map<String, String> icon = getIcons(iconType);
            Map<String, String> icon = EnumUtil.getIcons(iconService.getClazz(serviceName));
            List labels = new ArrayList(icon.keySet());

            // && ("gmv".equalsIgnoreCase(it) || !countImDisplay.contains(it))
            final List<SummaryBean> summaryList = a.keySet().stream()//接口要返回的结果
                    .filter(it -> labels.contains(it))
                    .map(it -> {
                        SummaryBean summaryBean = new SummaryBean();
                        summaryBean.setLabel(it);
                        summaryBean.setKey(it);
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
            //是否有其他统计字段 需要另外加和的分类，GMV 和 其他统计标签走一套逻辑。
            List<String> list = MergeItemService.getNeedMergeCollect(serviceName);
            if (list.size() > 0) {
                list.forEach(p -> {
                    Set<String> childrenLabel = MergeItemService.getNeedMergeItem(serviceName, p);
                    SummaryBean sum = new SummaryBean();
                    sum.setKey(MergeItemService.getLabel(serviceName, p));
                    sum.setLabel(p);
                    sum.setIcon(icon.get(p));
                    summaryList.stream()
                            .filter(it -> {
                                if (childrenLabel.contains(it.getLabel())) {
                                    if(!p.equalsIgnoreCase("GMV")) {
                                        sum.getChildren().add(it);
                                    }
                                    return true;
                                }
                                return false;
                            })
                            .reduce(sum, this::accumulation);
                    summaryList.add(0, sum);
                });
            }
            summaryList.removeIf(bean -> Const.countImDisplay.contains(bean.getLabel()) || MergeItem.BTB.MERCHANT_SERVICES.getChild().contains(bean.getLabel()) || MergeItem.BTB.STORE_BUSINESS.getChild().contains(bean.getLabel()));
            //排序
            Collections.sort(summaryList, (c1, c2) -> labels.indexOf(c1.getKey()) - labels.indexOf(c2.getKey()));
            summaryList.stream().filter(it -> it.getChildren().size() > 0).forEach(it -> {
                Collections.sort(it.getChildren(), (c1, c2) -> labels.indexOf(c1.getKey()) - labels.indexOf(c2.getKey()));
            });
            return summaryList;
        } catch (InterruptedException | ExecutionException e) {
            log.error("", e);
        }
        return new ArrayList<>();
    }

    /**
     * 累加
     * @param a1
     * @param a2
     * @return
     */
    private SummaryBean accumulation(SummaryBean a1, SummaryBean a2) {
        a1.setValue(a1.getValue() + a2.getValue());
        a1.setValueContrast(a1.getValueContrast() + a2.getValueContrast());
        if (a1.getMonthTarget() == -1) {
            a1.setMonthTarget(0L);
        } else if (a2.getMonthTarget() == -1) {
            a1.setMonthTarget(a1.getMonthTarget());
        } else {
            a1.setMonthTarget(a1.getMonthTarget() + a2.getMonthTarget());
        }
        a1.setMonthAccumulation(a1.getMonthAccumulation() + a2.getMonthAccumulation());
        a1.setMonthAccumulationContrast(a1.getMonthAccumulationContrast() + a2.getMonthAccumulationContrast());
        return a1;
    }

    /**
     * @Delete
     * @return gmv_type->icon
     */
    private Map<String, String> getIcons(String iconType) {
        Map<String, String> icons = new LinkedHashMap<>();
        String sql = "select gmv_type,gmv_icon,icon_type from gmv_type_config_pro where enable=1 and icon_type = '" + iconType + "' order by order_no";
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

    @CacheMd
    public List<GmvDayData> queryDetail(Date from, Date to, String gmvType, String service) {
        String sql = buildStatement(service, gmvType);
        Object[] params = gmvType == null ? new Object[]{from, to} : new Object[]{from, to, gmvType};
        try {
            List<GmvDayData> arr = new QRunner(gp).query(sql, new BeanListHandler<>(GmvDayData.class), params);
            return arr.stream().filter(it -> !Const.banGmvType.contains(it.getGmvType())).collect(Collectors.toList());
        } catch (SQLException e) {
            log.error("", e);
            return new ArrayList<>();
        }
    }

    private String buildStatement(String service, String gmvType) {
        if (ServiceValue.BTB.getKey().equalsIgnoreCase(service.trim())) {
            return "SELECT t1.report_date AS reportDate,t1.gmv_type AS gmvType,t1.settle_amount_num_day AS amountDay,t1.settle_amount_num_to_now AS amountToNow," +
                    "coalesce(t3.gmv_target,-1) AS target FROM rpt.rpt_b2b_gmv_day " +
                    "t1 LEFT JOIN dim.dim_b2b_gmv_target_month t3 ON substr(t1.report_date,1,7)=t3.month " +
                    "AND t1.gmv_type=t3.business_unit " +
                    "WHERE t1.report_date BETWEEN ? AND ?" + (gmvType != null ? " AND t1.gmv_type=? " : "");

        } else if (ServiceValue.CTB_0.getKey().equalsIgnoreCase(service.trim())) {

            return "SELECT t1.report_date AS reportDate,t1.business_unit AS gmvType,t1.settle_amount_num_day AS amountDay,t1.settle_amount_num_to_now AS amountToNow," +
                    "coalesce(t3.gmv_target,-1) AS target FROM rpt.rpt_c2b_gmv_day  " +
                    "t1 LEFT JOIN dim.dim_c2b_gmv_target_month t3 ON substr(t1.report_date,1,7)=t3.month " +
                    "AND t1.business_unit=t3.business_unit AND t1.business_type=t3.business_type " +
                    "WHERE t1.business_type='" + ServiceValue.CTB_0.getValue() + "' AND t1.report_date BETWEEN ? AND ? " + (gmvType != null ? " AND t1.business_unit=? " : "");
        } else if (ServiceValue.CTB_1.getKey().equalsIgnoreCase(service.trim())) {

            return "SELECT t1.report_date AS reportDate,t1.business_unit AS gmvType,t1.settle_order_num_day AS amountDay,t1.settle_order_num_to_now AS amountToNow," +
                    "coalesce(t3.order_target,-1) AS target FROM rpt.rpt_c2b_gmv_day  " +
                    "t1 LEFT JOIN dim.dim_c2b_gmv_target_month t3 ON substr(t1.report_date,1,7)=t3.month " +
                    "AND t1.business_unit=t3.business_unit AND t1.business_type=t3.business_type " +
                    "WHERE t1.business_type='" + ServiceValue.CTB_1.getValue() + "' AND t1.report_date BETWEEN ? AND ?" + (gmvType != null ? " AND t1.business_unit=? " : "");
        }
        return null;

    }

    private FutureTask<Map<String, List<GmvDayData>>> submitQuery(Date queryDate, String service) {
        FutureTask<Map<String, List<GmvDayData>>> futureTask = new FutureTask(() -> {
            return queryDetail(queryDate, service).stream().collect(Collectors.groupingBy(it -> it.getGmvType()));
        });
        new Thread(futureTask).run();
        return futureTask;
    }

    private List<GmvDayData> queryDetail(Date day, String service) throws SQLException {
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


}
