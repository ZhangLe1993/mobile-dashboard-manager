package com.aihuishou.bi.md.front.notice;

import com.aihuishou.bi.md.front.auth.SessionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/front/notice")
public class NoticeC {

    @Resource
    private SessionHelper sessionHelper;

    @Resource
    private SendMessJob sendMessJob;

    @PutMapping("/form_id")
    public void submitFormId(@RequestParam("form_id") String formId, @RequestHeader("sid") String sid) {
        String openId = sessionHelper.getOpenId(sid);
//        sendMessJob.addFormId(openId,formId);
    }
}
