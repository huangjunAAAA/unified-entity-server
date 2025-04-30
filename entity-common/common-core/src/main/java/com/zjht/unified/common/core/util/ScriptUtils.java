package com.zjht.unified.common.core.util;

import cn.hutool.core.lang.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

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
        int candidate=-1;
        while (true) {
            index = content.indexOf(tagStart, index);
            if (index == -1) {
                return candidate;
            }

            // 检查标签是否在引号内
            boolean inQuotes = false;
            for (int i = 0; i < index; i++) {
                if (content.charAt(i) == '"') {
                    inQuotes = !inQuotes;
                }
            }

            if (!inQuotes) {
                candidate = index;
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

    public static Pair<String,List<String>> parseImports(String content) {
        BufferedReader br=new BufferedReader(new StringReader(content));
        List<String> imports=new ArrayList<>();
        StringBuilder script=new StringBuilder();
        String line=null;
        try {
            while (null != (line = br.readLine())) {
                if (line.trim().startsWith("import")) {
                    imports.add(line);
                }else{
                    script.append(line).append("\n");
                }
            }
        }  catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return new Pair<>(script.toString(),imports);
    }

    /**
     * 解析类似下列的导入,然后合并
     * import { getPageModelList, getPageModelById, updatePageModel, delPageModel} from "@/api/uiserverApi";
     * import AA from "@/api/uiserverApi";
     * @param imports
     * @return
     */
    public static List<String> mergeImports(List<String> imports){
        Map<String, Set<String>> whole=new HashMap<>();
        Map<String, Set<String>> parts=new HashMap<>();
        Set<String> pureImports=new HashSet<>();

        for (String imp : imports) {
            int partStart = imp.indexOf("import") + 6;
            int partEnd = imp.indexOf("from");
            if(partEnd==-1){
                pureImports.add(imp.substring(partStart).trim());
                continue;
            }

            String part = imp.substring(partStart, partEnd);
            part=part.trim();
            String from = imp.substring(partEnd + 4);
            from=from.trim();
            int fromEnd=from.indexOf(";");
            if(fromEnd!=-1){
                from=from.substring(0,fromEnd);
                from=from.trim();
            }
            if(part.indexOf("{")==-1){
                if(whole.containsKey(from)){
                    whole.get(from).add(part);
                }else{
                    whole.put(from,new HashSet<>(Arrays.asList(part)));
                }
            }else{
                String[] subparts = part.replace("{", "").replace("}", "").split(",");
                for (String subpart : subparts) {
                    String subpartAlias = subpart.trim();
                    if (parts.containsKey(from)) {
                        parts.get(from).add(subpartAlias);
                    } else {
                        parts.put(from, new HashSet<>(Arrays.asList(subpartAlias)));
                    }
                }
            }
        }

        List<String> ret=new ArrayList<>();

        for (String pureImport : pureImports){
            ret.add("import "+pureImport+";\n");
        }

        for (Map.Entry<String, Set<String>> entry : parts.entrySet()) {
            String from = entry.getKey();
            Set<String> partsSet = entry.getValue();
            StringBuilder sb = new StringBuilder();
            sb.append("import ");
            if (!partsSet.isEmpty()) {
                sb.append("{ ");
                for (String part : partsSet) {
                    sb.append(part).append(", ");
                }
                sb.setLength(sb.length() - 2); // 移除最后一个逗号
                sb.append(" }");
            }
            sb.append(" from ").append(from).append(";\n");
            ret.add(sb.toString());
        }

        for (Map.Entry<String, Set<String>> entry : whole.entrySet()){
            String from = entry.getKey();
            Set<String> wholeSet = entry.getValue();
            for (String wholePart : wholeSet) {
                StringBuilder sb = new StringBuilder();
                sb.append("import ").append(wholePart).append(" from ").append(from).append(";\n");
                ret.add(sb.toString());
            }
        }

        return ret;
    }

    public static void main(String[] args) {
        String vueContent1 = "import { createApp } from \"vue\";\n" +
                "\n" +
                "import 'element-plus/theme-chalk/el-message.css';\n" +
                "import 'element-plus/theme-chalk/el-message-box.css';\n" +
                "\n" +
                "import ElementPlus from 'element-plus'\n" +
                "import zhLocale from 'element-plus/es/locale/lang/zh-cn'\n" +
                "import 'element-plus/dist/index.css'\n" +
                "\n" +
                "import \"animate.css/animate.min.css\";\n" +
                "\n" +
                "// import \"./styles/global.scss\";\n" +
                "\n" +
                "import \"./styles/style.scss\";\n" +
                "import \"./styles/global.scss\";\n" +
                "import 'virtual:svg-icons-register'\n" +
                "\n" +
                "import * as MH from \"./engine/modelhandler\";\n" +
                "\n" +
                "\n" +
                "\n" +
                "import App from \"./App.vue\";\n" +
                "const app = createApp(App);\n" +
                "\n" +
                "app.config.globalProperties.$ELEMENT = {};\n" +
                "\n" +
                "\n" +
                "import router from \"./router\";\n" +
                "app.use(router);\n" +
                "\n" +
                "\n" +
                "import { pinia } from \"./pinia/index\"; // 引入创建好的pinia\n" +
                "app.use(pinia);\n" +
                "\n" +
                "\n" +
                "app.use(ElementPlus, { locale: zhLocale })\n" +
                "\n" +
                "app.mount(\"#app\");\n" +
                "\n" +
                "app.config.errorHandler = (err, instance, info) => {\n" +
                "  // 全局应用错误监听\n" +
                "  console.error(\"application-err：\", err, instance, info);\n" +
                "};\n" +
                "\n" +
                "console.log(\"MH.theHandler\");";

        String vueContent2="import { ref, onMounted, watch } from \"vue\";\n" +
                "const nameJs6Pkak9 =ref(\"\")";

        String vueContent3="import { ref, onMounted, watch } from \"vue\";\n" +
                "import ArrowLeft,ArrowRight }from '@element-lus/icons-vue';\n" +
                "import TopTool from \"./components/tool/TopTool.vue\";\n" +
                "import MiddlTool from \"./components/tool/MiddlTool.vue\";\n" +
                "import LeftTool from \"./components/tool/LeftTool.vue\";\n" +
                "import draw from\"./components/tool/draw.vue\";\n" +
                "const p=MH.theHandler.loadAllPageFromSvr(-1,1);\n" +
                "p.then(function(data){\n" +
                "  console.log(\"data\",data);\n" +
                "});\n" +
                "import RightTool from \"./components/tool/RightTool.vue\",\n" +
                "import {useHomestore }from \"@/stores/home\";";

        Pair<String, List<String>> p1 = parseImports(vueContent1);
        Pair<String, List<String>> p2 = parseImports(vueContent2);
        Pair<String, List<String>> p3 = parseImports(vueContent3);

        List<String> all=new ArrayList<>();
        all.addAll(p1.getValue());
        all.addAll(p2.getValue());
        all.addAll(p3.getValue());
        System.out.println(mergeImports(all));

    }
}