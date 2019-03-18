package com.aihuishou.bi.md.front.auth;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;//员工姓名
    private String openId;//微信ID
    private String employeeNo;//员工号
    private Boolean active=false;//是否已激活
    private String activationCode;//激活码
    private Boolean enable=true;//是否可用
}
