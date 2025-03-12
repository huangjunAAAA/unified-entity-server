package com.zjht.unified.common.core.domain.component;

import lombok.Data;

/**
 * 用于说明收集参数的函数的位置
 * 用于说明组件的结果回调函数的位置
 * 使用 type说明该函数是 数据收集函数/数据提交函数/结果回调函数
 * 不同的type值指示paramData是不同的用法
 */
@Data
public class FuncField extends SimpleField{
    /**
     * 数据的样例
     */
    private String paramData;
    /**
     * 实际提交的URL
     */
    private String actualUrl;
}
