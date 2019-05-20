package com.aihuishou.bi.md.front.auth;

import lombok.Data;

@Data
public class Group {

    private int id;

    private String groupKey;

    private String description;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"groupKey\":\"")
                .append(groupKey).append('\"');
        sb.append(",\"description\":\"")
                .append(description).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
