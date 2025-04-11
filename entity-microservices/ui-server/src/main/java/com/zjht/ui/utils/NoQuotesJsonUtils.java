package com.zjht.ui.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class NoQuotesJsonUtils {

    public static class NoQuotesSerializer extends JsonSerializer {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeRawValue(value.toString()); // 直接写入原始值，不带引号
        }
    }

    public static class SingleQuotesSerializer extends JsonSerializer {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
                throws IOException {
            gen.writeRawValue("'"+value.toString()+"'");
        }
    }

    public static String toJson(Object o){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false); // 全局禁用引号

        try{
            String json = mapper.writeValueAsString(o);
            return json;
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }

        return null;
    }
}
