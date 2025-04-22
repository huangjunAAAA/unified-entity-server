package com.zjht.unified.common.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ScriptUtils {

    public static Map<String, String> readProperties(String content) {
        Map<String, String> map = new HashMap<>();
        if(StringUtils.isBlank(content))
            return map;
        try {
            BufferedReader br = new BufferedReader(new StringReader(content));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    map.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return map;
    }


    public static Map<String, String> parseScript(String content) {
        Map<String, String> result = new HashMap<>();

        result.put("script", "");
        result.put("scriptTag", "<script setup lang=\"ts\">");

        // 解析script部分
        int scriptStart = findTagStart(content, "<script");
        int scriptEnd = findTagEnd(content, "</script>", scriptStart);
        if(scriptEnd==-1){
            scriptEnd=findTagEnd(content,"<\\/script>",Math.max(0,scriptStart));
        }
        if (scriptStart != -1 && scriptEnd != -1) {
            result.put("script", content.substring(findTagContentStart(content, scriptStart), scriptEnd).trim());
            result.put("scriptTag", content.substring(scriptStart, findTagContentStart(content, scriptStart)).trim());
        }else{
            result.put("script",content);
        }

        return result;
    }
    /**
     * 解析Vue文件内容
     * @param content Vue文件内容
     * @return 返回一个包含template、script、style以及它们的标签开始符号的Map
     */
    public static Map<String, String> parseVueFile(String content) {
        Map<String, String> result = new HashMap<>();
        result.put("template", "");
        result.put("script", "");
        result.put("style", "");
        result.put("templateTag", "<template>");
        result.put("scriptTag", "<script setup lang=\"ts\">");
        result.put("styleTag", "<style>");

        // 解析template部分
        int templateStart = findTagStart(content, "<template");
        int templateEnd = findTagEnd(content, "</template>", templateStart);
        if (templateStart != -1 && templateEnd != -1) {
            result.put("template", content.substring(findTagContentStart(content, templateStart), templateEnd).trim());
            result.put("templateTag", content.substring(templateStart, findTagContentStart(content, templateStart)).trim());
        }

        // 解析script部分
        int scriptStart = findTagStart(content, "<script");
        int scriptEnd = findTagEnd(content, "</script>", scriptStart);
        if(scriptEnd==-1){
            scriptEnd=findTagEnd(content,"<\\/script>",Math.max(0,scriptStart));
        }
        if (scriptStart != -1 && scriptEnd != -1) {
            result.put("script", content.substring(findTagContentStart(content, scriptStart), scriptEnd).trim());
            result.put("scriptTag", content.substring(scriptStart, findTagContentStart(content, scriptStart)).trim());
        }

        // 解析style部分
        int styleStart = findTagStart(content, "<style");
        int styleEnd = findTagEnd(content, "</style>", styleStart);
        if (styleStart != -1 && styleEnd != -1) {
            result.put("style", content.substring(findTagContentStart(content, styleStart), styleEnd).trim());
            result.put("styleTag", content.substring(styleStart, findTagContentStart(content, styleStart)).trim());
        }

        return result;
    }

    /**
     * 查找标签的开始位置，确保标签不在引号内
     * @param content 文件内容
     * @param tagStart 标签的开始部分（如<script>或<style>）
     * @return 标签的开始位置，如果未找到或标签在引号内则返回-1
     */
    private static int findTagStart(String content, String tagStart) {
        int index = 0;
        while (true) {
            index = content.indexOf(tagStart, index);
            if (index == -1) {
                return -1;
            }

            // 检查标签是否在引号内
            boolean inQuotes = false;
            for (int i = 0; i < index; i++) {
                if (content.charAt(i) == '"') {
                    inQuotes = !inQuotes;
                }
            }

            if (!inQuotes) {
                return index; // 标签不在引号内，返回位置
            }

            index += tagStart.length(); // 继续查找下一个标签
        }
    }

    /**
     * 查找标签的结束位置，确保标签不在引号内
     * @param content 文件内容
     * @param tagEnd 标签的结束部分（如</script>或</style>）
     * @param startIndex 开始查找的位置
     * @return 标签的结束位置，如果未找到或标签在引号内则返回-1
     */
    private static int findTagEnd(String content, String tagEnd, int startIndex) {
        int index = startIndex;
        while (true) {
            index = content.indexOf(tagEnd, index);
            if (index == -1) {
                return -1;
            }

            // 检查标签是否在引号内
            boolean inQuotes = false;
            for (int i = startIndex; i < index; i++) {
                if (content.charAt(i) == '"') {
                    inQuotes = !inQuotes;
                }
            }

            if (!inQuotes) {
                return index; // 标签不在引号内，返回位置
            }

            index += tagEnd.length(); // 继续查找下一个标签
        }
    }

    /**
     * 查找标签内容的开始位置，跳过标签的属性部分
     * @param content 文件内容
     * @param tagStartIndex 标签的开始位置
     * @return 标签内容的开始位置
     */
    private static int findTagContentStart(String content, int tagStartIndex) {
        int tagEndIndex = content.indexOf('>', tagStartIndex);
        if (tagEndIndex == -1) {
            return -1;
        }
        return tagEndIndex + 1;
    }

    public static void main(String[] args) {
        String vueContent = " <template>\n" +
                " <div>Hello World</div>\n" +
                " </template>\n" +
                "\n" +
                " <script lang=\"ts\">\n" +
                " export default {\n" +
                " name: 'App',\n" +
                " template: \"<template><script>This is a fake template</template>\"\n" +
                " }\n" +
                " </script>\n" +
                "\n" +
                " <style scoped>\n" +
                " body {\n" +
                " background-color: #fff;\n" +
                " }\n" +
                " </style>";

        Map<String, String> parsed = parseVueFile(vueContent);
        System.out.println("Template: " + parsed.get("template"));
        System.out.println("Script: " + parsed.get("script"));
        System.out.println("Style: " + parsed.get("style"));
        System.out.println("Template Tag: " + parsed.get("templateTag"));
        System.out.println("Script Tag: " + parsed.get("scriptTag"));
        System.out.println("Style Tag: " + parsed.get("styleTag"));
    }
}