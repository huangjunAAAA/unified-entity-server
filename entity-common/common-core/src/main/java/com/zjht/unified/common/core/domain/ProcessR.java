package com.zjht.unified.common.core.domain;

import com.zjht.unified.common.core.constants.Constants;
import okhttp3.Headers;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @author zjht
 */
public class ProcessR implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String code;

    private Headers headers;

    private String requestBody;

    private String responseBody;

    public static ProcessR ok(Headers headers, String requestBody, String responseBody)
    {
        return restResult(Constants.LOG_STATUS_SUCCESS, headers,requestBody, responseBody);
    }

    public static ProcessR fail(Headers headers, String requestBody, String responseBody)
    {
        return restResult(Constants.LOG_STATUS_FAIL, headers,requestBody, responseBody);
    }

    private static ProcessR restResult(String code, Headers headers, String requestBody, String responseBody)
    {
        ProcessR apiResult = new ProcessR();
        apiResult.setCode(code);
        apiResult.setHeaders(headers);
        apiResult.setRequestBody(requestBody);
        apiResult.setResponseBody(responseBody);
        return apiResult;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
