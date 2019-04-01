package com.aihuishou.bi.md.front.chart.gmv;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;
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

    @Cacheable(value = "gmv-last-data-date", key = "123")
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

    @CachePut(key = "123",value = "gmv-last-data-date")
    public Date setLastDataDate(String date) throws ParseException {
        if(!StringUtils.isEmpty(date)&&!"null".equalsIgnoreCase(date)){
            return new Date(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
        }else{
            return getLastDataDate();
        }
    }
}
