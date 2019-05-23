package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.ActivationFail;
import com.aihuishou.bi.md.front.auth.exception.InvalidSidException;
import com.aihuishou.bi.md.front.auth.exception.WeixinAuthFailException;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("front")
public class LoginC {

    @Value("${app_id}")
    private String appId;
    @Value("${app_secret}")
    private String appSecret;
    @Resource
    private SessionHelper sessionHelper;
    @Resource
    private UserService userService;

    @Resource
    private GroupService groupService;

    @RequestMapping("/login")
    public void login(@RequestParam("code") String code, @RequestHeader(value = "sid", required = false) String sid, HttpServletResponse response) throws IOException, SQLException {
        String openId = null;
        if (!StringUtils.isEmpty(sid)) {
            openId = sessionHelper.getOpenId(sid);
        }
        if (StringUtils.isEmpty(openId)) {
            WxSessionResponse resp = checkCode(code);
            if (resp.getErrCode() == null || resp.getErrCode() == 0) {
                openId = resp.getOpenId();
                sid = UUID.randomUUID().toString();
                sessionHelper.bindSid(sid, openId);
            } else {//微信认证失败
                log.error(resp.getErrMsg());
                throw new WeixinAuthFailException();
            }
        }
        response.setHeader("sid", sid);
        userService.checkActive(openId);//校验激活情况
        response.setHeader("no", userService.findByOpenId(openId).getEmployeeNo());
        List<String> group = groupService.list(openId);
        response.setHeader("group", JSONArray.toJSONString(group));
        response.setStatus(200);
    }

    private WxSessionResponse checkCode(String code) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId + "&secret=" +
                appSecret + "&js_code=" + code + "&grant_type=authorization_code";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        log.info("check code:" + code);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.info("http response:" + response.getStatusCode() + "|" + response.getStatusCodeValue());
        }
        ObjectMapper mapper = new ObjectMapper();
        WxSessionResponse rep = mapper.readValue(response.getBody(), WxSessionResponse.class);
        return rep;
    }

    /**
     * 用户进行激活处理
     *
     * @param activationCode
     * @return
     */
    @RequestMapping("/active")
    public void active(@RequestParam("code") String activationCode, @RequestHeader(value = "sid") String sid, HttpServletResponse response) throws SQLException {
        String openId = sessionHelper.getOpenId(sid);
        if (StringUtils.isEmpty(openId)) {//校验用户SID会话
            throw new InvalidSidException();
        }
        User user = userService.findByActiveCode(activationCode);
        if (user == null) {
            log.warn("activate fail,user not found,openId:" + openId + " activationCode:" + activationCode);
            throw new ActivationFail();
        } else if (!userService.active(openId, activationCode)) {
            log.warn("activate fail,openId:" + openId + " activationCode:" + activationCode);
            throw new ActivationFail();
        } else {
            List<String> group = groupService.list(openId);
            response.setStatus(200);
            response.setHeader("no", userService.findByOpenId(openId).getEmployeeNo());
            response.setHeader("group", JSONArray.toJSONString(group));
        }
    }

}
