package com.zjht.ui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Slf4j
public class PackageLockChecker {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 检查 package.json 和 package-lock.json 的第一层依赖是否完全匹配
     *
     * @param packageJsonContent      package.json 文件内容
     * @param packageLockJsonContent  package-lock.json 文件内容
     * @return 不匹配的依赖项列表，如果为空表示完全匹配
     * @throws IOException 如果读取文件失败
     */
    public static List<String> checkDependenciesMatch(String packageJsonContent, String packageLockJsonContent) throws IOException {
        JsonNode packageJson = objectMapper.readTree(packageJsonContent);
        JsonNode packageLockJson = objectMapper.readTree(packageLockJsonContent);

        Map<String, String> dependencies = new HashMap<>();

        // 添加 package.json 中的 dependencies
        addDependenciesFromJson(packageJson.get("dependencies"), dependencies, false);
        addDependenciesFromJson(packageJson.get("devDependencies"), dependencies, true);

        Map<String, String> lockDependencies = new HashMap<>();
        JsonNode packages = packageLockJson.get("packages");

        if (packages != null && packages.isObject()) {
            packages.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                JsonNode value = entry.getValue();

                if (key.startsWith("node_modules/")) {
                    String moduleName = key.replace("node_modules/", "");
                    JsonNode versionNode = value.get("version");
                    if (versionNode != null && versionNode.isTextual()) {
                        lockDependencies.put(moduleName, versionNode.asText());
                    }
                }
            });
        }

        List<String> mismatches = new ArrayList<>();
        for (Map.Entry<String, String> entry : dependencies.entrySet()) {
            String module = entry.getKey();
            String expectedVersion = entry.getValue();
            String actualVersion = lockDependencies.get(module);
//            if(module.equals("pinia"))
//                System.out.println(module+" "+expectedVersion+" "+actualVersion);
            if (actualVersion == null) {
                mismatches.add(module + ": 在 package-lock.json 中缺失");
            } else if (!isVersionCompatible(expectedVersion, actualVersion)) {
                mismatches.add(module + ": 兼容性不一致 (期望: " + expectedVersion + ", 实际: " + actualVersion + ")");
            }
        }

        return mismatches;
    }

    /**
     * 从 JSON 节点提取依赖并放入 map
     */
    private static void addDependenciesFromJson(JsonNode node, Map<String, String> result, boolean dev) {
        if (node != null && node.isObject()) {
            node.fields().forEachRemaining(entry -> {
                String name = entry.getKey();
                String version = entry.getValue().asText();
                result.put(name, version);
            });
        }
    }

    /**
     * 比较版本号是否符合要求
     * @param version1 基准版本号（如 "1.2.3"）
     * @param version2 待比较版本号（如 "a.b.c"）
     * @return 如果 version2 符合 version1 的要求返回 true，否则返回 false
     */
    public static boolean isVersionCompatible(String version1, String version2) {
        // 规则：第二个版本号为空直接返回 false
        if (version2 == null || version2.isEmpty()) {
            return false;
        }
        if (version1.startsWith("^") || version1.startsWith("~")) {
            version1 = version1.substring(1);
        }

        // 分割版本号
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");
        int minLength = Math.min(v1Parts.length, v2Parts.length);

        // 逐段比较
        for (int i = 0; i < minLength; i++) {
            String seg1 = v1Parts[i];
            String seg2 = v2Parts[i];

            // 规则：双方都是数字时比较数值
            if (seg1.matches("\\d+") && seg2.matches("\\d+")) {
                int num1 = Integer.parseInt(seg1);
                int num2 = Integer.parseInt(seg2);
                if (num1 < num2)
                    return true;
                if (num2 < num1) {
                    return false; // 数字部分小于基准
                }
                // 数字部分等于时继续比较下一段
            }
            // 规则：包含字母时按字符串比较
            else if (!seg1.matches("\\d+") && !seg2.matches("\\d+")) {
                if (seg2.compareTo(seg1) < 0) {
                    return false; // 字符串字典序小于基准
                }
                if (seg2.compareTo(seg1) > 0) {
                    return true; // 字符串字典序大于基准
                }
            }
            // 规则：数字和字母混合比较不符合
            else {
                return false;
            }
        }

        // 规则：version2 缺省后续部分时，沿用已比较结果
        return true;
    }


    public static void main(String[] args) throws Exception{
        String p1file="/tmp/package.json";
        String p2file="/tmp/package-lock.json";
        String p1content = new String(Files.readAllBytes(new File(p1file).toPath()));
        String p2content = new String(Files.readAllBytes(new File(p2file).toPath()));
        List<String> mismatches = checkDependenciesMatch(p1content, p2content);
        if (mismatches.isEmpty()) {
            System.out.println("依赖项完全匹配");
        } else {
            System.out.println("依赖项不匹配："+mismatches);
        }
    }

}
