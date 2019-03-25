package com.aihuishou.bi.md.front.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
@CacheConfig(cacheNames = "session-helper")
public class SessionHelper {

    @Value("${app_id}")
    private String appId;
    @Value("${app_secret}")
    private String appSecret;

    @CachePut(value = "sid", key = "#sid")
    public String bindSid(String sid, String openId) {
        return openId;
    }

    @Cacheable("sid")
    public String getOpenId(String sid) {
        return null;
    }

    @Cacheable("access-token")
    public String getAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("getAccessToken " + "http response:" + response.getStatusCode() + "|" + response.getStatusCodeValue());
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode accessToken = mapper.readTree(response.getBody()).get("access_token");
                if (accessToken != null) {
                    return accessToken.asText();
                }
            } catch (IOException e) {
                log.error("getAccessToken \n" + response.getBody(), e);
            }
        }
        return null;
    }

    @CachePut("access-token")
    public String updateAccessToken() {
        return getAccessToken();
    }
}
