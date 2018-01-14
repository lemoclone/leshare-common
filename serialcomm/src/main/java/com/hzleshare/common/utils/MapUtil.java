package com.hzleshare.common.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

public class MapUtil {

    /**
     * Map key 排序
     *
     * @param map map
     * @return map
     */
    public static SortedMap<String, String> order(Map<String, String> map) {
        SortedMap<String, String> result = new TreeMap<>();
        for(String key:map.keySet()){
            result.put(key,map.get(key));
        }
        return result;
    }


    /**
     * 转换对象为map
     *
     * @param object object
     * @param ignore ignore
     * @return map
     */
    public static Map<String, String> objectToMap(Object object, String... ignore) {
        Map<String, String> tempMap = new LinkedHashMap<String, String>();
        for (Field f : getAllFields(object.getClass())) {
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            boolean ig = false;
            if (ignore != null && ignore.length > 0) {
                for (String i : ignore) {
                    if (i.equals(f.getName())) {
                        ig = true;
                        break;
                    }
                }
            }
            if (ig) {
                continue;
            } else {
                Object o = null;
                try {
                    o = f.get(object);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                tempMap.put(f.getName(), o == null ? "" : o.toString());
            }
        }
        return tempMap;
    }

    /**
     * 获取所有Fields,包含父类field
     *
     * @param clazz clazz
     * @return list
     */
    private static List<Field> getAllFields(Class<?> clazz) {
        if (!clazz.equals(Object.class)) {
            List<Field> fields = new ArrayList<Field>(Arrays.asList(clazz.getDeclaredFields()));
            List<Field> fields2 = getAllFields(clazz.getSuperclass());
            if (fields2 != null) {
                fields.addAll(fields2);
            }
            return fields;
        } else {
            return null;
        }
    }

    /**
     * url 参数串连
     *
     * @param map            map
     * @param keyLower       keyLower
     * @param valueUrlencode valueUrlencode
     * @return string
     */
    public static String mapJoin(Map<String, String> map, boolean keyLower, boolean valueUrlencode) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            if (map.get(key) != null && !"".equals(map.get(key))) {
                try {
                    String temp = (key.endsWith("_") && key.length() > 1) ? key.substring(0, key.length() - 1) : key;
                    stringBuilder.append(keyLower ? temp.toLowerCase() : temp)
                            .append("=")
                            .append(valueUrlencode ? URLEncoder.encode(map.get(key), "utf-8").replace("+", "%20") : map.get(key))
                            .append("&");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

}
