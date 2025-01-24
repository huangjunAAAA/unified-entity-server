package com.zjht.unified.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;


public class IgnoreValidateFormDataConfiguration {
    @Bean
    public RequestInterceptor ignoreValidate(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                template.header("ignoreValidate","true");
            }
        };
    }

    @Bean
    public Encoder encoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
}
