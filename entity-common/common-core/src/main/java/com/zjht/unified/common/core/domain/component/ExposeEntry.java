package com.zjht.unified.common.core.domain.component;


import lombok.Data;



@Data
public class ExposeEntry {

    /**
     * 获取数据的函数
     */
    private FuncField dataCollect;


    /**
     * 提交数据的函数
     */
    private FuncField submit;

    /**
     * 返回结果的回调
     */
    private FuncField setRet;
}
