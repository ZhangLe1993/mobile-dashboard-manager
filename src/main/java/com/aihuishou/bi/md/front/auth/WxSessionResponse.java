package com.aihuishou.bi.md.front.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WxSessionResponse {
    /**
     * 用户唯一标识
     */
    @JsonProperty("openid")
    private String openId;
    /**
     * 会话密钥(不能下发到client)
     */
    @JsonProperty("session_key")
    private String sessionKey;
    /**
     * 用户在开放平台的唯一标识符
     */
    @JsonProperty("unionid")
    private String unionId;
    /**
     * 错误码：-1 系统繁忙、0 请求成功、40029 code 无效、45011 频率限制 每个用户每分钟100次
     */
    @JsonProperty("errcode")
    private Integer errCode;
    /**
     * 错误信息
     */
    @JsonProperty("errmsg")
    private String errMsg;
}
