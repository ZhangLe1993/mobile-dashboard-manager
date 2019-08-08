package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.front.chart.enums.MergeItem;
import com.aihuishou.bi.md.front.chart.enums.ServiceValue;

import java.util.*;

public class MergeItemService {

    public static List<String> getNeedMergeCollect(ServiceValue serviceName) {
        switch(serviceName) {
            case BTB:
                return new ArrayList<>(Arrays.asList(MergeItem.BTB.GMV.getType(), MergeItem.BTB.MERCHANT_SERVICES.getType(), MergeItem.BTB.STORE_BUSINESS.getType()));
            case CTB_0:
                return new ArrayList<>(Collections.singletonList(MergeItem.RECYCLE.GMV.getType()));
            case CTB_1:
                return new ArrayList<>(Collections.singletonList(MergeItem.SWAP.GMV.getType()));
            default:
                return new ArrayList<>(Arrays.asList(MergeItem.BTB.GMV.getType(), MergeItem.BTB.MERCHANT_SERVICES.getType(), MergeItem.BTB.STORE_BUSINESS.getType()));
        }
    }


    public static Set<String> getNeedMergeItem(ServiceValue serviceName, String type) {
        switch(serviceName) {
            case BTB:
                return MergeItem.BTB.fromMerge(type).getChild();
            case CTB_0:
                return MergeItem.RECYCLE.fromMerge(type).getChild();
            case CTB_1:
                return MergeItem.SWAP.fromMerge(type).getChild();
            default:
                return MergeItem.BTB.fromMerge(type).getChild();
        }
    }


    public static String getLabel(ServiceValue serviceName, String type) {
        switch(serviceName) {
            case BTB:
                return MergeItem.BTB.fromMerge(type).getLabel();
            case CTB_0:
                return MergeItem.RECYCLE.fromMerge(type).getLabel();
            case CTB_1:
                return MergeItem.SWAP.fromMerge(type).getLabel();
            default:
                return MergeItem.BTB.fromMerge(type).getLabel();
        }
    }

}
