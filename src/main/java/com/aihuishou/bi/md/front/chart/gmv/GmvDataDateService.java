package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.core.QRunner;
import com.aihuishou.bi.md.front.cache.CacheHolder;
import com.aihuishou.bi.md.front.chart.enums.ServiceValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.ResultSetHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Slf4j
@Component
public class GmvDataDateService {
    @Resource
    @Qualifier("gp")
    private DataSource gp;

    @Cacheable(value = CacheHolder.GMV_LAST_DATA_DATE_CACHE_NAME, key = "#service")
    public Date getLastDataDate(String service) throws ParseException {
        //return new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2019-02-28").getTime());
        String sql = getSqlByService(service);
        //log.info("查询日期SQL：{}", sql);
        Date dataDate = null;
        try {
            dataDate = new QRunner(gp).query(sql, new ResultSetHandler<Date>() {
                @Override
                public Date handle(ResultSet resultSet) throws SQLException {
                    resultSet.next();
                    return resultSet.getDate("report_date");
                }
            });
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return new Date(cal.getTime().getTime());
        } catch (SQLException e) {
            log.error("", e);
            return null;
        }
    }

    private String getSqlByService(String service) {
        if(!ServiceValue.BTB.getKey().equalsIgnoreCase(service)) {
            String businessType = ServiceValue.CTB_0.getValue();
            if(ServiceValue.CTB_1.getKey().equalsIgnoreCase(service)) {
                businessType = ServiceValue.CTB_1.getValue();
            }
            return "select report_date from rpt.rpt_c2b_gmv_day where business_type ='" + businessType + "' order by report_date desc limit 1";
        }
        return "select report_date from rpt.rpt_b2b_gmv_day order by report_date desc limit 1";
    }

    @CachePut(key = "#service",value = CacheHolder.GMV_LAST_DATA_DATE_CACHE_NAME)
    public Date setLastDataDate(String date, String service) throws ParseException {
        if(!StringUtils.isEmpty(date) && !"null".equalsIgnoreCase(date)){
            return new Date(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
            //return new Date(new SimpleDateFormat("yyyy-MM-dd").parse("2019-02-28").getTime());
        }else{
            return getLastDataDate(service);
        }
    }
}
