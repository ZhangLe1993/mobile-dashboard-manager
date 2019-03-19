package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.front.auth.CurrentUser;
import com.aihuishou.bi.md.front.auth.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        DecimalFormat nf=new DecimalFormat("00.00%");
        List<SummaryBean> summary = gmvService.querySummary();
        Map<String, Object> result = new HashMap();
        result.put("date", new SimpleDateFormat("yyyy-MM-dd").format(lastDataDate));
        result.put("date_progress",nf.format(monthProgress));
        result.put("data", summary);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @RequestMapping("/month_day")
    public ResponseEntity monthDay(@CurrentUser User user) {
        //TODO
        return new ResponseEntity("hello world " + user.getName(), HttpStatus.OK);
    }
}
