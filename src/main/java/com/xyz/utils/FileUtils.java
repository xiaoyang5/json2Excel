package com.xyz.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.*;

public class FileUtils {

    /**
     * 获取文件夹下的json文件
     * @param path
     * @return
     */
    public static File[] getFiles(String path){
        File file = new File(path);
        return file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                boolean flag = false;
                if (pathname.getName().toLowerCase().endsWith(".json")){
                    flag = true;
                }
                return flag;
            }
        });
    }

    /**
     * 读取文件转换为JSON对象
     * @param jsonFile  JSON文件对象
     * @return
     */
    public static JSONObject getJsonContent(File jsonFile){
        JSONObject jsonObject = null;
        try {
            Reader reader = new InputStreamReader(new FileInputStream(jsonFile),"utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1){
                sb.append((char) ch);
            }
            reader.close();
            String jsonStr = sb.toString();
            jsonObject = JSON.parseObject(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
