package com.xyz.controller;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.xyz.utils.ExcelUtils;
import com.xyz.utils.FileUtils;
import com.xyz.utils.JsonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api-excel")
public class GenerateExcelController {

    /**
     * 服务代码,json文件与生成的excel文件在同一台机器,并将文件内容下载
     * @param jsonFilePath
     * @param excelpath
     * @param excelName
     * @return
     */
    @GetMapping("/generate-excel")
    public HttpServletResponse generateExcel(@RequestParam String jsonFilePath,
                                @RequestParam String excelpath,
                                @RequestParam(required = false,defaultValue = "demo") String excelName,
                                ServletRequest request, HttpServletResponse response){
        File[] files = FileUtils.getFiles(jsonFilePath);
        ArrayList<Map<String,String>> rowList = CollUtil.newArrayList();
        for (File file : files){
            Map<String,String> map = new ConcurrentHashMap<>();
            JSONObject jsonContent = FileUtils.getJsonContent(file);
            JsonUtils.getAllObjects(map,"",jsonContent);
            rowList.add(map);
        }
        //生成excel文件
        ExcelUtils.generateExcel(excelpath,excelName,rowList);

        //将excel文件下载到本地
        excelName = excelName+".xlsx";
        File excelFile = new File(excelpath+File.separator+excelName);
        InputStream fis = null;
        OutputStream toClient = null;
        try {
            fis = new BufferedInputStream(new FileInputStream(excelFile));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            //清空response
            response.reset();
            //设置response的header
            response.addHeader("Content-Disposition","attachment;filename*=utf-8'zh_cn'"+ URLEncoder.encode(excelName,"UTF-8"));
            response.addHeader("Content-length",""+excelFile.length());
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 服务代码与json文件在同台机器，将excel文件下载
     * @param jsonFilePath
     * @return
     */
    public String downloadExcel(@RequestParam String jsonFilePath,
                                @RequestParam String excelpath,
                                @RequestParam(required = false,defaultValue = "demo") String excelname){

        return null;
    }
}
