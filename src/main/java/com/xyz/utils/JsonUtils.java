package com.xyz.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;

public class JsonUtils {

    /**
     * 将json对象拉平，存储到map中,key的类型为A.B.C
     * @param map
     * @param prefix 第一层json的前缀，一般设置为:""
     * @param jsonObject
     */
    public static void getAllObjects(Map<String,String> map,String prefix,JSONObject jsonObject){
//        Map<String,String> map = new ConcurrentHashMap<>();
        Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
        for (Map.Entry<String,Object> entry:entries){
            String key = entry.getKey();
            if (prefix != ""){
                key = prefix+"."+key;
            }
            Object value = entry.getValue();
            if (value.getClass().getName().equals(JSONObject.class.getName())){
                getAllObjects(map,key,JSONObject.parseObject(value.toString()));
            }else if (value.getClass().getName().equals(JSONArray.class.getName())){
                JSONArray jsonArray = (JSONArray) value;
                if (jsonArray.get(0).getClass().getName().equals(String.class.getName()) ||
                        jsonArray.get(0).getClass().getName().equals(Integer.class.getName())){
                    if (map.containsKey(key)){
                        key = key+"1";
                    }
                    map.put(key,jsonArray.toString());
                }else {
                    for (int i = 0;i<jsonArray.size();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        getAllObjects(map,key,jsonObject1);
                    }
                }
            }else {
                if (map.containsKey(key)){
                    key = key+"1";
                }
                map.put(key,value.toString());
            }
        }
    }

    public static <T> T jsonToBean(String json, @NonNull Class<T> cls) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return JSON.parseObject(json, cls);
    }

    public static <T> T jsonToBean(String jsonStr, Class<T> baseType, Type... nestingTypes) {
        if (StringUtils.isBlank(jsonStr)) {
            return null;
        }
        return JSON.parseObject(jsonStr, buildType(baseType, nestingTypes));
    }

    public static <T> List<T> jsonToList(String json, @NonNull Class<T> cls) {
        if (StringUtils.isBlank(json)) {
            return Collections.emptyList();
        }
        return JSON.parseArray(json, cls);
    }

    public static String beanToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return JSON.toJSONString(obj, DisableCircularReferenceDetect);
        } catch (Exception e) {
            return null;
        }
    }

    public static String extractStrByPath(String contentJson, String path) {
        if (StringUtils.isBlank(contentJson)) {
            return null;
        }
        Object valueObj = JSONPath.read(contentJson, path);
        if (null == valueObj) {
            return null;
        }
        return String.valueOf(valueObj);
    }

    public static <T> List<T> extraListByPath(String contentJson, String path, Class<T> tClass) {
        Object data = JSONPath.read(contentJson, path);
        if (data == null) {
            return Collections.emptyList();
        }
        return JSONObject.parseArray(JsonUtils.beanToJson(data), tClass);
    }

    public static <F, T> T convert(F f, Class<T> clazz) {
        if (f == null) {
            return null;
        }
        return jsonToBean(beanToJson(f), clazz);
    }

    public static <F, T> List<T> convertList(List<F> f, Class<T> clazz) {
        if (CollectionUtils.isEmpty(f)) {
            return Collections.emptyList();
        }
        return jsonToBean(beanToJson(f), List.class, clazz);
    }

    private static <T> Type buildType(Class<T> baseType, Type... nestingTypes) {
        if (nestingTypes == null || nestingTypes.length == 0) {
            return baseType;
        }

        ParameterizedTypeImpl beforeType = null;
        for (int i = nestingTypes.length - 1; i > 0; i--) {
            beforeType = new ParameterizedTypeImpl(new Type[]{beforeType == null ? nestingTypes[i] : beforeType}, null,
                    nestingTypes[i - 1]);
        }
        beforeType = new ParameterizedTypeImpl(new Type[]{beforeType == null ? nestingTypes[0] : beforeType},
                null, baseType);
        return beforeType;
    }

    /**测试方法，验证json文件转excel的准确性*/
    public static void main(String[] args) {
        File[] files = FileUtils.getFiles("E:\\tmp\\fw_ws");
        ArrayList<Map<String,String>> rowList = CollUtil.newArrayList();
        for (File file:files){
            Map<String,String> map = new ConcurrentHashMap<>();
            JSONObject jsonContent = FileUtils.getJsonContent(file);
            getAllObjects(map,"",jsonContent);
            System.out.println(map.toString());
            rowList.add(map);
        }
        ExcelUtils.generateExcel("E:\\tmp\\fw_ws","wsjxtest",rowList);
    }
}