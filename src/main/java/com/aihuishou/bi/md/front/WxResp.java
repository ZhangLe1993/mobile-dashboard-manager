package com.aihuishou.bi.md.front;

import lombok.Data;

@Data
public class WxResp {
    private Integer code;//0是success
    private Object data;

    public WxResp(Object data) {
        this(0, data);
    }

    public WxResp(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }
}
