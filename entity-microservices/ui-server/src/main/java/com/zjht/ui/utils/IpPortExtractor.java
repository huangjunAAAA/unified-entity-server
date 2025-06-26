package com.zjht.ui.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Optional;

public class IpPortExtractor {
    /**
     * 从字符串中提取首个符合格式的IP:端口组合
     * @param input 任意输入字符串
     * @return 提取到的"IP:端口"字符串，未找到时返回空Optional
     */
    public static String extractIpPort(String input) {
        // 正则表达式：匹配IPv4地址 + 冒号 + 端口号（端口范围1-65535）
        String regex =
                "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                        "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):" +
                        "(0|[1-9][0-9]{0,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])\\b";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(); // 返回完整匹配的"IP:端口"
        }
        return null;
    }
}