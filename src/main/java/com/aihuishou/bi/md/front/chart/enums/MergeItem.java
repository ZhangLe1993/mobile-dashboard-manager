package com.aihuishou.bi.md.front.chart.enums;

import java.util.*;

/**
 * 手机看版GMV合计指标，指标枚举及父子关系
 */
public enum MergeItem {

    BTB_GMV("B2B", "GMV", new HashSet<>(Arrays.asList("尾品汇", "企业服务", "海外"))),
    MERCHANT_SERVICES("B2B", "商家业务", new HashSet<>(Arrays.asList("OPT", "POP"))),
    STORE_BUSINESS("B2B", "到店业务", new HashSet<>(Arrays.asList("爱机汇", "店员宝"))),
    CTB_0_GMV("C2B_0", "GMV", new HashSet<>()),
    CTB_1_GMV("C2B_1", "GMV", new HashSet<>());


    private String service;
    private String type;
    private Set<String> child;

    MergeItem(String service, String type, Set<String> child) {
        this.service = service;
        this.type = type;
        this.child = child;
    }

    public String getService() {
        return service;
    }

    public String getType() {
        return type;
    }

    public Set<String> getChild() {
        return child;
    }

    public static MergeItem getTotalType(String type) {
        switch (type) {
            case "GMV":
                return MergeItem.BTB_GMV;
            case "商家业务":
                return MergeItem.MERCHANT_SERVICES;
            case "到店业务":
                return MergeItem.STORE_BUSINESS;
            default:
                return null;
        }
    }

    public static List<String> listTotal(String service) {
        if(service.equalsIgnoreCase(ServiceValue.BTB.getKey())){
            return new ArrayList<>(Arrays.asList(MergeItem.MERCHANT_SERVICES.getType(), MergeItem.STORE_BUSINESS.getType()));
        }
        return new ArrayList<>();
    }

}
