package com.zjht.unified.dto;

import lombok.Data;

import java.util.List;

@Data
public class UePrjListDTO {
    private List<Long> id;

    /**
     * 名称
     */
    private List<String> name;

    /**
     * UI项目ID
     */
    private List<Long> uiPrjId;

    /**
     * 版本号
     */
    private List<String> version;

    /**
     * 原始ID
     */
    private List<Long> originalId;

    /**
     * GUID
     */
    private List<String> guid;

    /**
     * 是否模板
     */
    private List<Integer> template;
}