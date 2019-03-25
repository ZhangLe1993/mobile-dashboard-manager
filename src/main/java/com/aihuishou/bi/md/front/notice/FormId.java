package com.aihuishou.bi.md.front.notice;

import java.io.Serializable;

public class FormId implements Serializable {
    private String value;//form id值
    private Long expireTime;//过期的时间点

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
