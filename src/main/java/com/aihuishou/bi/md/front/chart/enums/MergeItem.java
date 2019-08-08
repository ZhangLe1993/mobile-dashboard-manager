package com.aihuishou.bi.md.front.chart.enums;

import java.util.*;

/**
 * 手机看版GMV合计指标，指标枚举及父子关系
 */
public interface MergeItem {

    enum BTB implements MergeItem {
        GMV("B2B", "GMV", "GMV", new HashSet<>(Arrays.asList("OPT", "POP", "爱机汇", "店员宝", "尾品汇", "企业服务", "海外"))),
        MERCHANT_SERVICES("B2B", "商家业务", "商家业务", new HashSet<>(Arrays.asList("OPT", "POP"))),
        STORE_BUSINESS("B2B", "到店业务", "到店业务", new HashSet<>(Arrays.asList("爱机汇", "店员宝")))
        ;

        private String service;
        private String type;
        private String label;
        private Set<String> child;

        BTB(String service, String type, String label, Set<String> child) {
            this.service = service;
            this.type = type;
            this.label = label;
            this.child = child;
        }

        public String getService() {
            return service;
        }

        public String getType() {
            return type;
        }

        public String getLabel() {
            return label;
        }

        public Set<String> getChild() {
            return child;
        }

        public static BTB fromMerge(String type) {
            for (BTB item : BTB.values()) {
                if (item.getType().equalsIgnoreCase(type)) {
                    return item;
                }
            }
            return BTB.GMV;
        }
    }

    enum RECYCLE implements MergeItem {
        GMV("回收", "GMV", "GMV（不含加盟）", new HashSet<>(Arrays.asList("BD", "官网", "自营门店", "其他")))
        ;

        private String service;
        private String type;
        private String label;
        private Set<String> child;

        RECYCLE(String service, String type, String label, Set<String> child) {
            this.service = service;
            this.type = type;
            this.label = label;
            this.child = child;
        }

        public String getService() {
            return service;
        }

        public String getType() {
            return type;
        }

        public String getLabel() {
            return label;
        }

        public Set<String> getChild() {
            return child;
        }

        public static RECYCLE fromMerge(String type) {
            for (RECYCLE item : RECYCLE.values()) {
                if (item.getType().equalsIgnoreCase(type)) {
                    return item;
                }
            }
            return RECYCLE.GMV;
        }
    }

    enum SWAP implements MergeItem {
        GMV("换新", "GMV", "单量", new HashSet<>(Arrays.asList("官网", "自营门店")))
        ;

        private String service;
        private String type;
        private String label;
        private Set<String> child;

        SWAP(String service, String type, String label, Set<String> child) {
            this.service = service;
            this.type = type;
            this.label = label;
            this.child = child;
        }

        public String getService() {
            return service;
        }

        public String getType() {
            return type;
        }

        public String getLabel() {
            return label;
        }

        public Set<String> getChild() {
            return child;
        }

        /**
         * 根据业务获取要加和的项
         * @param
         * @return
         */
        public static SWAP fromMerge(String type) {
            for (SWAP item : SWAP.values()) {
                if (item.getType().equalsIgnoreCase(type)) {
                    return item;
                }
            }
            return SWAP.GMV;
        }
    }

}
