package com.aihuishou.bi.md.front.notice;

import com.aihuishou.bi.md.front.auth.SessionHelper;
import com.aihuishou.bi.md.front.auth.User;
import com.aihuishou.bi.md.front.auth.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping()
public class NoticeC {

    @Resource
    private SessionHelper sessionHelper;

    @Resource
    private SendMessJob sendMessJob;

    @Resource
    private UserService userService;

    @PutMapping("/front/notice/form_id")
    public void submitFormId(@RequestParam("form_id") String formId, @RequestHeader("sid") String sid) {
        String openId = sessionHelper.getOpenId(sid);
        log.info("controller add formId openId:"+openId+" formId:"+formId);
        sendMessJob.addFormId(openId, formId);
    }

    @GetMapping("/back/notice/trigger")
    public void trigger(@RequestParam(value = "employee_no", required = false) String employeeNo) throws Exception {
        if (!StringUtils.isEmpty(employeeNo)) {
            User u = userService.findByEmployeeNo(employeeNo);
            String openId = u.getOpenId();
            sendMessJob.sendGmv(openId);
        } else {
            sendMessJob.sendGmv(true);
        }
    }

    @GetMapping("/back/notice/trigger/test")
    public void test() throws Exception {
        log.info("发送模板测试");
        sendMessJob.sendGmv("oYscn48qNNGWWYVfZLuXzfWKfFQc");
    }
}
