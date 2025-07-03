package com.zjht.unified.dto;

import lombok.Data;

import java.util.List;

@Data
public class SentinelDefListDTO {
    /**
     * id
     */
    private List<Long> id;
    /**
     * GUID
     */
    private List<String> guid;

    /**
     * 名称
     */
    private List<String> name;

    /**
     * 执行体
     */
    private List<String> body;

    /**
     * 定时器表达式
     */
    private List<String> cron;

    /**
     * 并发模式
     */
    private List<Integer> concurrent;

    /**
     * 终止条件
     */
    private List<String> abort;
}