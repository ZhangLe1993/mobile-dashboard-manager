package com.aihuishou.bi.md.utils;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnumUtil {
    /**
     * 根据枚举的字符串获取枚举的值
     *
     * @param
     * @return
     * @throws Exception
     */
    public static Map<String, String> getIcons(Class<?> clazz) throws Exception {
        Class<Enum> enumClazz = (Class<Enum>) clazz;
        // 得到枚举类对象
        Map<String, String> icons = new LinkedHashMap<>();
        //获取所有枚举实例
        Enum[] enumConstants = enumClazz.getEnumConstants();
        //根据方法名获取方法
        Method getName = clazz.getMethod("getName");
        Method getIcon = clazz.getMethod("getIcon");
        for (Enum obj : enumConstants) {
            //执行枚举方法获得枚举实例对应的值
            String k = getName.invoke(obj).toString();
            String v = getIcon.invoke(obj).toString();
            icons.put(k, v);
        }
        return icons;
    }

}
