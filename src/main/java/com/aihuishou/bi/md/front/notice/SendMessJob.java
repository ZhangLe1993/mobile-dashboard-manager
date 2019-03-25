package com.aihuishou.bi.md.front.notice;

import com.aihuishou.bi.md.front.auth.SessionHelper;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class SendMessJob {

    private static final String FORM_ID_PREFIX = "form_id_";
    private static final Integer FORM_MAX_SIZE = 7 * 10;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SessionHelper sessionHelper;

//    @PostConstruct
    public void sendGmv() {
        String accessToken = sessionHelper.getAccessToken();
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + accessToken;
        Map arguments = new HashMap();
        String touser = "o96Ec5GnvSg_3AwLeHvO5TAhwL2c";
        String template_id = "8djC-TtdVUqn-A48-aehRU22-jz08vd1DVgXDRC9SC0";
        String page = "index?foo=bar";
        ResponseEntity<String> response = restTemplate.postForEntity(url, arguments, String.class);

    }

    /**
     * 存储formId,最多存储 7*10 70个formId,没必要存太多
     *
     * @param openId
     * @param formId
     */
    public void addFormId(String openId, String formId) {
        //TODO
        String key = FORM_ID_PREFIX + openId;
        ListOperations opt = redisTemplate.opsForList();
        opt.size(key);
    }

    /**
     * 消费一个formId
     *
     * @param openId
     * @return
     */
    public String getFormId(String openId) {
        //TODO
        return null;
    }
}
