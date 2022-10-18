package me.qiujun.arbitrage.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtil {

    public static Map<String, Object> bean2Map(Object bean) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                String name = field.getName();
                field.setAccessible(true);
                Object value = field.get(bean);
                if (value != null) {
                    map.put(name, value);
                }
            } catch (IllegalAccessException ignored) {
            }
        }
        return map;
    }

}
