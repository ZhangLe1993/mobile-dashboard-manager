package com.aihuishou.bi.md.front.notice;

import com.aihuishou.bi.md.front.auth.SessionHelper;
import com.aihuishou.bi.md.front.auth.UserService;
import com.aihuishou.bi.md.front.chart.gmv.GmvService;
import com.aihuishou.bi.md.front.chart.gmv.SummaryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SendMessJob {

    private static final String FORM_ID_PREFIX = "form_id_";
    private static final Integer FORM_MAX_SIZE = 7 * 10;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SessionHelper sessionHelper;

    @Resource
    private UserService userService;

    @Resource
    private GmvService gmvService;

    public void sendGmv() {
        try {
            List<String> openIds = userService.allOpenIds();
            for (String openId : openIds) {
                sendGmv(openId);
            }
        } catch (SQLException e) {
            log.error("sendGmv", e);
        }
    }

    private void sendGmv(String openId) {
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = sessionHelper.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + accessToken;
        Map arguments = new HashMap();
        String template_id = "8djC-TtdVUqn-A48-aehRU22-jz08vd1DVgXDRC9SC0";
        String formId = getFormId(openId);
        if (formId == null) {
            return;
        }
        arguments.put("touser", openId);
        arguments.put("form_id", formId);
        arguments.put("template_id", template_id);
        arguments.put("page", "pages/statement/index");
        Map data = new HashMap();
        Map keyword1 = new HashMap();
        keyword1.put("value", new SimpleDateFormat("yyyy-MM-dd").format(gmvService.getLastDataDate()));
        Map keyword2 = new HashMap();
        SummaryBean gmvValue = gmvService.querySummary().stream().filter(it -> it.getLabel().equalsIgnoreCase("GMV")).findFirst().get();
        keyword2.put("value", "昨日GMV " + dataFormat(gmvValue.getValue()) + " 较前日 " + dataFormatPercent((double) (gmvValue.getValue() - gmvValue.getValueContrast()) / gmvValue.getValueContrast())
                + "\n本月GMV " + gmvValue.getMonthAccumulation()
                + " 同比 " + dataFormatPercent((double) (gmvValue.getMonthAccumulation() - gmvValue.getMonthAccumulationContrast()) / gmvValue.getMonthAccumulationContrast()));
        data.put("keyword1", keyword1);
        data.put("keyword2", keyword2);
        arguments.put("data", data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, arguments, String.class);
        log.info("sendGmv(" + openId + ") " + response.getStatusCodeValue() + " \n" + response.getBody());
    }

    private String dataFormatPercent(double p) {
        DecimalFormat format = new DecimalFormat("0.00%");
        return (p > 0 ? "\u2191" : "\u2193") + format.format(p);
    }

    private String dataFormat(Long value) {
        if (value < 10000) {
            return value.toString();
        } else {
            DecimalFormat format = new DecimalFormat("0.00万");
            return format.format(value / 10000d);
        }
    }


    /**
     * 存储formId,最多存储 FORM_MAX_SIZE 个formId,没必要存太多
     *
     * @param openId
     * @param formId
     */
    public void addFormId(String openId, String formId) {
        String key = FORM_ID_PREFIX + openId;
        ListOperations opt = redisTemplate.opsForList();
        List<FormId> arr = opt.range(key, 0, -1);
        int total = arr.size();
        for (int i = 0; i < arr.size(); i++) {
            FormId f = arr.get(i);
            if (f.getExpireTime() >= System.currentTimeMillis()) {//已过期
                redisTemplate.opsForList().remove(key, 1, f.getValue());
                total--;
            }
        }
        if (total < FORM_MAX_SIZE) {//没超过上线就继续存储
            FormId f = new FormId();
            f.setValue(formId);
            f.setExpireTime(System.currentTimeMillis() + (3600L * 24 * 6));//6天延迟
            redisTemplate.opsForList().rightPush(key, f);
        }
    }

    /**
     * 消费一个formId
     *
     * @param openId
     * @return
     */
    public String getFormId(String openId) {
        String key = FORM_ID_PREFIX + openId;
        while (true) {
            Object formId = redisTemplate.opsForList().leftPop(key);
            if (formId == null) {
                break;
            }
            Long t = ((FormId) formId).getExpireTime();
            if (t > System.currentTimeMillis()) {//没过期
                return ((FormId) formId).getValue();
            }
        }
        return null;
    }
}
