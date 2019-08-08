package com.aihuishou.bi.md.front.chart.gmv;

import com.aihuishou.bi.md.front.chart.conf.Const;
import com.aihuishou.bi.md.front.chart.enums.ServiceItem;
import com.aihuishou.bi.md.front.chart.enums.ServiceValue;
import com.aihuishou.bi.md.utils.EnumUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class IconService {


    public Set<String> allGmvType(String service) throws Exception {
        ServiceValue serviceName = ServiceValue.fromType(service);
        Set<String> allTypes = EnumUtil.getIcons(getClazz(serviceName)).keySet();
        allTypes.removeAll(Const.banGmvType);
        return allTypes;
    }

    public String getIconType(ServiceValue serviceName) {
        switch(serviceName) {
            case BTB:
                return ServiceValue.BTB.getValue();
            case CTB_0:
                return ServiceValue.CTB_0.getValue();
            case CTB_1:
                return ServiceValue.CTB_1.getValue();
            default:
                return ServiceValue.BTB.getValue();
        }
    }

    public Class<?> getClazz(ServiceValue serviceName) {
        switch(serviceName) {
            case BTB:
                return ServiceItem.BTB.class;
            case CTB_0:
                return ServiceItem.RECYCLE.class;
            case CTB_1:
                return ServiceItem.SWAP.class;
            default:
                return ServiceItem.BTB.class;
        }
    }
}
