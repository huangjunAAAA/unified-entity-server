package com.zjht.unified.data.common.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zjht.unified.data.common.core.constants.Constants;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @author zjht
 */
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class R<T> implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = Constants.SUCCESS;

    /** 失败 */
    public static final int FAIL = Constants.FAIL;

    private T data;

    private int code;

    private String msg;    

    public static <T> R<T> ok()
    {
        return new R(null, SUCCESS, null);
    }

    public static <T> R<T> ok(T data)
    {
        return new R(data, SUCCESS, null);
    }

    public static <T> R<T> ok(T data, String msg)
    {
        return new R(data, SUCCESS, msg);
    }

    public static <T> R<T> fail()
    {
        return new R(null, FAIL, null);
    }

    public static <T> R<T> fail(String msg)
    {
        return new R(null, FAIL, msg);
    }

    public static <T> R<T> fail(T data)
    {
        return new R(data, FAIL, null);
    }

    public static <T> R<T> fail(T data, String msg)
    {
        return new R(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg)
    {
        return new R(null, code, msg);
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

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }
}
