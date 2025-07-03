package com.zjht.unified.dto;

import lombok.Data;

import java.util.List;

@Data
public class FsmDefListDTO {
    /**
     * ID
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
     * 驱动类型
     */
    private List<Integer> driver;

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

    /**
     * 初始状态
     */
    private List<String> initialState;
}