package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.InvalidActivationCodeException;
import com.aihuishou.bi.md.front.auth.exception.InvalidSidException;
import com.aihuishou.bi.md.front.auth.exception.WeixinAuthFailException;
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
import java.io.IOException;
import java.sql.SQLException;
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

    @RequestMapping("/login")
    public ResponseEntity login(@RequestParam("code") String code, @RequestHeader(value = "sid", required = false) String sid) throws IOException {
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
        userService.checkActive(openId);//校验激活情况
        return new ResponseEntity(sid, HttpStatus.OK);
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
     * 已登录用户进行激活处理
     *
     * @param activationCode
     * @return
     */
    @RequestMapping("/active")
    public ResponseEntity active(@RequestParam("code") String activationCode, @RequestHeader("sid") String sid) throws SQLException {
        String openId = sessionHelper.getOpenId(sid);
        if (StringUtils.isEmpty(openId)) {
            throw new InvalidSidException();
        }
        User user = userService.findByOpenId(openId);
        if (user.getActive()) {
            return new ResponseEntity(HttpStatus.OK);
        }

        if (activationCode.equals(user.getActivationCode())) {//激活码正确
            user.setActive(true);//设置为激活状态
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new InvalidActivationCodeException();
        }
    }

}
