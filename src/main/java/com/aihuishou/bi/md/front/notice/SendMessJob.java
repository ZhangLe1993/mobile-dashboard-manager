package com.aihuishou.bi.md.front.notice;

import com.aihuishou.bi.md.front.auth.GroupService;
import com.aihuishou.bi.md.front.auth.SessionHelper;
import com.aihuishou.bi.md.front.auth.UserService;
import com.aihuishou.bi.md.front.chart.gmv.GmvDataDateService;
import com.aihuishou.bi.md.front.chart.gmv.GmvService;
import com.aihuishou.bi.md.front.chart.gmv.SummaryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    @Resource
    private GmvDataDateService gmvDataDateService;

    @Resource
    private GroupService groupService;

    @Scheduled(cron = "0 30 9 * * ?")//每天9点半
    public void sendGmv() throws IOException, ParseException, SQLException {
        try {
            List<String> openIds = userService.allOpenIds();
            log.info("begin sendGmv======" + org.apache.commons.lang3.StringUtils.join(openIds, ","));
            for (String openId : openIds) {
                sendGmv(openId);
            }
        } catch (SQLException e) {
            log.error("sendGmv error", e);
        }
    }

    public void sendGmv(String openId) throws IOException, ParseException, SQLException {
        RestTemplate restTemplate = new RestTemplate();
        String accessToken = sessionHelper.getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + accessToken;
        Map<String,Object> arguments = new HashMap<>();
        String template_id = "8djC-TtdVUqn-A48-aehRU22-jz08vd1DVgXDRC9SC0";
        String formId = popFormId(openId);
        if (formId == null) {
            log.info("openId(" + openId + "),formId not found");
            return;
        }
        List<String> group = groupService.list(openId);
        if(group == null || group.size() == 0) {
            return;
        }

        Map<String,Object> data = new HashMap<>();

        String template = "昨日(%s)GMV: %s 较前日 %s \n 本月(%s)GMV: %s  同比: %s \n";
        if(group.contains(GroupMapping.BTB.getKey())) {
            Date date = gmvDataDateService.getLastDataDate(GroupMapping.BTB.getKey());
            if(DateUtil.isYesterday(date)) {
                SummaryBean gmvValue = gmvService.querySummary(GroupMapping.BTB.getKey()).stream().filter(it -> it.getLabel().equalsIgnoreCase("GMV")).findFirst().get();
                Map<String,Object> temp = buildData(
                        "keyword1",
                        "keyword2",
                        template,
                        GroupMapping.BTB.getKey(),
                        GroupMapping.BTB.getKey(),
                        dataFormat(gmvValue.getValue()),
                        dataFormatPercent((double) (gmvValue.getValue() - gmvValue.getValueContrast()) / gmvValue.getValueContrast()),
                        dataFormat(gmvValue.getMonthAccumulation()),
                        dataFormatPercent((double) (gmvValue.getMonthAccumulation() - gmvValue.getMonthAccumulationContrast()) / gmvValue.getMonthAccumulationContrast()));
                data.putAll(temp);
            }
        }

        if(group.contains(GroupMapping.CTB.getKey())) {
            Date date = gmvDataDateService.getLastDataDate(GroupMapping.CTB_0.getKey());
            if(DateUtil.isYesterday(date)) {
                SummaryBean hsValue = gmvService.querySummary(GroupMapping.CTB_0.getKey()).stream().filter(it -> it.getLabel().equalsIgnoreCase("GMV")).findFirst().get();
                Map<String,Object> temp = buildData(
                        "keyword3",
                        "keyword4",
                        template,
                        GroupMapping.CTB_0.getValue(),
                        GroupMapping.CTB_0.getKey(),
                        dataFormat(hsValue.getValue()),
                        dataFormatPercent((double) (hsValue.getValue() - hsValue.getValueContrast()) / hsValue.getValueContrast()),
                        dataFormat(hsValue.getMonthAccumulation()),
                        dataFormatPercent((double) (hsValue.getMonthAccumulation() - hsValue.getMonthAccumulationContrast()) / hsValue.getMonthAccumulationContrast()));

                data.putAll(temp);
            }
            date = gmvDataDateService.getLastDataDate(GroupMapping.CTB_1.getKey());
            if(DateUtil.isYesterday(date)) {
                SummaryBean hxValue = gmvService.querySummary(GroupMapping.CTB_1.getKey()).stream().filter(it -> it.getLabel().equalsIgnoreCase("GMV")).findFirst().get();
                Map<String,Object> temp2 = buildData(
                        "keyword5",
                        "keyword6",
                        template,
                        GroupMapping.CTB_1.getValue(),
                        GroupMapping.CTB_1.getKey(),
                        dataFormat(hxValue.getValue()),
                        dataFormatPercent((double) (hxValue.getValue() - hxValue.getValueContrast()) / hxValue.getValueContrast()),
                        dataFormat(hxValue.getMonthAccumulation()),
                        dataFormatPercent((double) (hxValue.getMonthAccumulation() - hxValue.getMonthAccumulationContrast()) / hxValue.getMonthAccumulationContrast()));

                data.putAll(temp2);
            }
        }

        if(data.isEmpty()) {
            return;
        }
        arguments.put("touser", openId);
        arguments.put("form_id", formId);
        arguments.put("template_id", template_id);
        arguments.put("page", "pages/statement/index");

        arguments.put("data", data);
        ResponseEntity<String> response = restTemplate.postForEntity(url, arguments, String.class);
        log.info("sendGmv(" + openId + ") " + response.getStatusCodeValue() + " \n" + response.getBody());
    }


    private Map<String, Object> buildData(String key1, String key2, String template, String title, String service, String v1, String v2, String v3, String v4) {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> keyword1 = new HashMap<>();
        keyword1.put("value", new SimpleDateFormat("yyyy-MM-dd").format(gmvDataDateService.getLastDataDate(service)));
        data.put(key1, keyword1);
        Map<String, Object> keyword2 = new HashMap<>();
        String str = String.format(template, title, v1, v2, title, v3, v4);
        keyword2.put("value", str);
        data.put(key2, keyword2);
        return data;
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
        if(total>=FORM_MAX_SIZE){//存储已满时触发检查，清理过期的FormId
            for (int i = 0; i < arr.size(); i++) {
                FormId f = arr.get(i);
                if (f.getExpireTime() >= System.currentTimeMillis()) {//已过期
                    redisTemplate.opsForList().remove(key, 1, f.getValue());
                    total--;
                }
            }
        }
        if (total < FORM_MAX_SIZE) {//没超过上线就继续存储
            FormId f = new FormId();
            f.setValue(formId);
            f.setExpireTime(System.currentTimeMillis() + (1000L * 3600 * 24 * 6));//6天延迟
            redisTemplate.opsForList().rightPush(key, f);
        }
    }

    /**
     * 消费一个formId
     *
     * @param openId
     * @return
     */
    public String popFormId(String openId) {
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

    public List<FormId> allFormIds(String openId) {
        String key = FORM_ID_PREFIX + openId;
        List formId = redisTemplate.opsForList().range(key, 0, -1);
        return formId;
    }
}
