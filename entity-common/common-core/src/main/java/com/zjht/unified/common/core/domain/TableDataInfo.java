package com.zjht.unified.common.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 * 
 * @author zjht
 */
public class TableDataInfo<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> data;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    /**
     * 表格数据对象
     */
    public TableDataInfo()
    {
    }

    public TableDataInfo(int code, String msg){
        this.code=code;
        this.msg=msg;
    }

    /**
     * 分页
     * 
     * @param list 列表数据
     * @param total 总记录数
     */
    public TableDataInfo(List<T> list, int total)
    {
        this.data = list;
        this.total = total;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}