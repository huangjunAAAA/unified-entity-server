package com.zjht.ui.dto;

import lombok.Data;
import java.util.List;

@Data
public class UiPrjListDTO {
    /**
     * ID列表
     */
    private List<Long> id;

    /**
     * 项目名称列表
     */
    private List<String> name;

    /**
     * 项目目录列表
     */
    private List<String> workDir;

    /**
     * Node.js版本列表
     */
    private List<String> nodejsVer;

    /**
     * 组件库版本列表
     */
    private List<String> componentLibVer;

    /**
     * 存储方式列表 (db数据库/hybrid混合)
     */
    private List<String> storageType;

    /**
     * 版本号列表
     */
    private List<String> version;

    /**
     * 源项目ID列表
     */
    private List<Long> originalId;

    /**
     * 是否外联项目列表
     */
    private List<String> externalType;

    /**
     * 外联项目ID列表
     */
    private List<String> externalId;
}