package com.zjht.ui.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
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
                    if (!moduleName.contains("/")) { // 只处理一级依赖
                        JsonNode versionNode = value.get("version");
                        if (versionNode != null && versionNode.isTextual()) {
                            lockDependencies.put(moduleName, versionNode.asText());
                        }
                    }
                }
            });
        }

        List<String> mismatches = new ArrayList<>();
        for (Map.Entry<String, String> entry : dependencies.entrySet()) {
            String module = entry.getKey();
            String expectedVersion = entry.getValue();
            String actualVersion = lockDependencies.get(module);

            if (actualVersion == null) {
                mismatches.add(module + ": 在 package-lock.json 中缺失");
            } else if (!expectedVersion.equals(actualVersion)) {
                mismatches.add(module + ": 版本不一致 (期望: " + expectedVersion + ", 实际: " + actualVersion + ")");
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

}
