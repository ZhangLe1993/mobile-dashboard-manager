package com.aihuishou.bi.md.front.chart.enums;

/**
 * 区分业务线
 */
public interface ServiceItem {

    /**
     * B2B
     */
    enum BTB implements ServiceItem {
        GMV("B2B", "GMV", "icondollar"),
        MERCHANT_BUSINESS("B2B", "商家业务", "iconhammer"),
        OPT("B2B", "OPT", "iconnotice"),
        POP("B2B", "POP", "iconnotice"),
        STORE_BUSINESS("B2B", "到店业务", "iconuser"),
        LOVE_MACHINE_SINK("B2B", "爱机汇", "icontool"),
        SHOP_BAO("B2B", "店员宝", "iconstore"),
        CORPORATE_SERVICES("B2B", "企业服务", "iconcomputer"),
        TAIL_EXCHANGE("B2B", "尾品汇", "iconnotice"),
        OVERSEAS("B2B", "海外", "iconglobal")
        ;
        private String service;
        private String name;
        private String icon;

        BTB(String service, String name, String icon) {
            this.service = service;
            this.name = name;
            this.icon = icon;
        }

        public String getService() {
            return service;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }
    }

    /**
     * 回收
     */
    enum RECYCLE implements ServiceItem {
        GMV("回收", "GMV", "icondollar"),
        BD("回收", "BD", "iconbd"),
        WEBSITE("回收", "官网", "iconwebsite"),
        REGION("回收", "自营门店", "iconregion"),
        JOIN("回收", "加盟门店", "iconjoin"),
        OTHER("回收", "其他", "iconother")
        ;
        private String service;
        private String name;
        private String icon;

        RECYCLE(String service, String name, String icon) {
            this.service = service;
            this.name = name;
            this.icon = icon;
        }

        public String getService() {
            return service;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }
    }

    /**
     * 换新
     */
    enum SWAP implements ServiceItem {
        GMV("换新", "GMV", "icondollar"),
        WEBSITE("换新", "官网", "iconwebsite"),
        REGION("换新", "自营门店", "iconstore"),
        BD("换新", "BD", "iconbd")
        ;
        private String service;
        private String name;
        private String icon;

        SWAP(String service, String name, String icon) {
            this.service = service;
            this.name = name;
            this.icon = icon;
        }

        public String getService() {
            return service;
        }

        public String getName() {
            return name;
        }

        public String getIcon() {
            return icon;
        }
    }

}
