package com.zjht.unified.data.common.core.util;



import com.zjht.unified.data.common.core.constants.Constants;
import com.zjht.unified.data.common.core.domain.HttpR;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpUtils {

    public static HttpR get(String url, Headers headers, String ssl) {
        OkHttpClient client = buildClient(url, ssl);
        Request.Builder builder = new Request.Builder();
        builder.url(url).get();
        if (headers != null) {
            builder.headers(headers);
        }
        String res = "";
        try {
            Response response = client.newCall(builder.build()).execute();
            if (response.body() != null) {
                res = response.body().string();
            }
            if (response.isSuccessful()) {
                return HttpR.ok(res);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            res = e.getMessage();
        }
        return HttpR.fail(res);
    }

    public static HttpR post(String url, Headers headers, Map<String,Object> params, String ssl) {
        OkHttpClient client = buildClient(url, ssl);
        FormBody.Builder formBody = new FormBody.Builder();
        if(params!=null)
            params.forEach((k, v) -> {
                if (k != null && v != null) {
                    formBody.add(k, String.valueOf(v));
                }
            });

        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (headers != null) {
            builder.headers(headers);
        }
        builder.post(formBody.build());
        Request request = builder.build();
        String res = "";
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                res = response.body().string();
            }
            if (response.isSuccessful()) {
                return HttpR.ok(res);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            res = e.getMessage();
        }
        return HttpR.fail(res);

    }

    public static HttpR post(String url, Map<String,String> headers,Map<String,Object> params,String ssl){
        Headers.Builder builder = new Headers.Builder();
        if(headers!=null)
            for (Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> next = iterator.next();
                if(StringUtils.isNotEmpty(next.getValue()))
                    builder.add(next.getKey(),next.getValue());
            }
        return post(url, builder.build(), params, ssl);
    }

    public static HttpR get(String url, Map<String,String> headers,Map<String,Object> params,String ssl){
        Headers.Builder builder = new Headers.Builder();
        if(headers!=null)
            for (Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> next = iterator.next();
                if(StringUtils.isNotEmpty(next.getValue()))
                    builder.add(next.getKey(),next.getValue());
            }
        StringBuilder queryString=new StringBuilder();
        if(params!=null&&!params.isEmpty()) {
            queryString.append("?");
            for (Iterator<Map.Entry<String, Object>> iterator = params.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> param = iterator.next();
                if(param.getValue()!=null&&StringUtils.isNotEmpty(param.getValue()+"")) {
                    queryString.append(param.getKey()).append("=").append(param.getValue());
                    if (iterator.hasNext()) {
                        queryString.append("&");
                    }
                }
            }
            url=url+queryString;
        }
        return get(url, builder.build(), ssl);
    }

    public static HttpR postJSON(String url, Map<String,String> headers,String json,String ssl){
        Headers.Builder builder = new Headers.Builder();
        if(headers!=null)
            for (Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> next = iterator.next();
                if(StringUtils.isNotEmpty(next.getValue()))
                    builder.add(next.getKey(),next.getValue());
            }
        return postJSON(url, builder.build(), json, ssl);
    }

    public static HttpR postJSON(String url, Headers headers, String json, String ssl) {
        // 如果不需要忽略证书可new OkHttpClient();实现自己的业务
        OkHttpClient client = buildClient(url, ssl);

        if (json == null) {
            json = "";
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (headers != null) {
            builder.headers(headers);
        }
        builder.post(body);
        Request request = builder.build();
        String res = "";
        try {
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                res = response.body().string();
            }
            if (response.isSuccessful()) {
                return HttpR.ok(res);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            res = e.getMessage();
        }
        return HttpR.fail(res);
    }


    private static OkHttpClient buildClient(String url, String ssl) {
        OkHttpClient client = null;
        if (url.startsWith("https:") || Constants.YES.equals(ssl)) {
            client = getUnsafeOkHttpClient();
        } else {
            client = new OkHttpClient();
        }
        //10秒连接超时
        client.newBuilder().connectTimeout(3, TimeUnit.SECONDS)
                //30m秒写入超时
                .writeTimeout(30, TimeUnit.SECONDS)
                //30秒读取超时
                .readTimeout(30, TimeUnit.SECONDS).build();
        return client;
    }

    /**
     * okHttp3添加信任所有证书
     *
     * @return
     */

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            // builder.sslSocketFactory(sslSocketFactory ）方法已过时，使用下面方法替代
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                // 可以将不需要忽略的域名放入数组，也可为空（忽略所有证书）
                String[] arr = new String[]{};

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    if (StringUtils.isEmpty(hostname)) {
                        return false;

                    }
                    return !Arrays.asList(arr).contains(hostname);
                }
            });
            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
