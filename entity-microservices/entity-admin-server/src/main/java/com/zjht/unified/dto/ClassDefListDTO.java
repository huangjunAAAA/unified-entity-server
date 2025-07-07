package com.zjht.unified.dto;

import lombok.Data;

import java.util.List;

/**
 * 所有字段与ClassDef一样，转为List版本
 */
@Data
public class ClassDefListDTO {
    private List<Long> id;

    /**
     * 类GUID
     */
    private List<String> guid;

    /**
     * 父类ID
     */
    private List<Long> parentId;

    /**
     * 父类GUID
     */
    private List<String> parentGuid;

    /**
     * 父类是否源自依赖项目
     */
    private List<Long> parentPrj;

    /**
     * 类名
     */
    private List<String> name;

    /**
     * 类中文名
     */
    private List<String> nameZh;

    /**
     * 用户定义/系统定义
     */
    private List<String> type;

    /**
     * 项目ID
     */
    private List<Long> prjId;

    /**
     * 映射的表名
     */
    private List<String> tbl;

    /**
     * 是否持久化
     */
    private List<Integer> persistent;

    /**
     * 版本号
     */
    private List<String> version;

    /**
     * 原始ID
     */
    private List<Long> originalId;

    /**
     * 修饰符
     */
    private List<String> modifer;

    /**
     * pv的同义属性
     */
    private List<String> pvAttr;

    /**
     * 是否可继承
     */
    private List<Integer> inheritable;

    /**
     * 继承的类是否可读基类
     */
    private List<Integer> inheritRead;

    /**
     * 继承的类是否可写基类
     */
    private List<Integer> inheritWrite;
}
