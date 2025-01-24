package com.zjht.unified.common.core.domain;

import com.zjht.unified.common.core.constants.Constants;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @author zjht
 */
public class HttpR implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS = Constants.SUCCESS;

    /**
     * 失败
     */
    public static final int FAIL = Constants.FAIL;

    private int code;

    private String msg;

    public static HttpR ok() {
        return restResult(SUCCESS, null);
    }

    public static HttpR ok(String msg) {
        return restResult(SUCCESS, msg);
    }


    public static HttpR fail() {
        return restResult(FAIL, null);
    }

    public static HttpR fail(String msg) {
        return restResult(FAIL, msg);
    }

    public static HttpR fail(int code, String msg) {
        return restResult(code, msg);
    }

    private static HttpR restResult(int code, String msg) {
        HttpR apiResult = new HttpR();
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        return apiResult;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
