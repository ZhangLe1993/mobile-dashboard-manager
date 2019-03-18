package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.front.auth.CurrentUser;
import com.aihuishou.bi.md.front.auth.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/front/gmv")
public class GmvC {

    @Resource
    private GmvService gmvService;

    @RequestMapping("/summary")
    public ResponseEntity summary() {
        List<SummaryBean> summary = gmvService.querySummary();
        return new ResponseEntity(summary, HttpStatus.OK);
    }

    @RequestMapping("/month_day")
    public ResponseEntity monthDay(@CurrentUser User user) {

        return new ResponseEntity("hello world " + user.getName(), HttpStatus.OK);
    }
}
