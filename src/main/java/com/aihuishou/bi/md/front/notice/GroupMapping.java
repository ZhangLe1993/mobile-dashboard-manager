package com.aihuishou.bi.md.front.notice;

import com.aihuishou.bi.md.core.enums.Total;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum GroupMapping {
    CTB("C2B", "C2B"),
    BTB("B2B", "B2B"),
    CTB_0("C2B_0", "回收"),
    CTB_1("C2B_1", "换新")
    ;

    private String key;
    private String value;

    GroupMapping(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

}
