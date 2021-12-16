package com.xyz.utils;

import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelWriter;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    /**
     * 将listMap 去转化为Excel文件，生成xlsx后缀的文件
     * @param excelPath 存储路径
     * @param excelName 文件名
     * @param rowList
     */
    public static void generateExcel(String excelPath, String excelName, List<Map<String,String>> rowList){
        String excel = excelPath+ File.separator+excelName+".xlsx";
        BigExcelWriter excelWriter = new BigExcelWriter(excel);
        excelWriter.write(rowList,true);
        excelWriter.close();
    }
}
