package com.zjht.unified.common.core.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.DefaultIdStrategy;
import io.protostuff.runtime.IdStrategy;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Base64;

public class ProtoBufUtils {
    static final DefaultIdStrategy STRATEGY = new DefaultIdStrategy(IdStrategy.DEFAULT_FLAGS
            | IdStrategy.PRESERVE_NULL_ELEMENTS
            | IdStrategy.MORPH_COLLECTION_INTERFACES
            | IdStrategy.MORPH_MAP_INTERFACES
            | IdStrategy.MORPH_NON_FINAL_POJOS);

    public static String serialize(Object obj){
        Schema schema = RuntimeSchema.getSchema(obj.getClass(), STRATEGY);
        // Re-use (manage) this buffer to avoid allocating on every serialization
        LinkedBuffer buffer = LinkedBuffer.allocate(512);

        // ser
        try {
            final byte[] protostuff;
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
            return Base64.getEncoder().encodeToString(protostuff);
        }finally {
            buffer.clear();
        }
    }

    public static <T> T deserialize(String message, Class<T> klass){
        Schema<T> schema = RuntimeSchema.getSchema(klass, STRATEGY);
        T result=schema.newMessage();
        byte[] buffer = Base64.getDecoder().decode(message);
        ProtostuffIOUtil.mergeFrom(buffer, result, schema);
        return result;
    }

    public static <T> T cloneObject(T obj){
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass(), STRATEGY);
        // Re-use (manage) this buffer to avoid allocating on every serialization
        LinkedBuffer buffer = LinkedBuffer.allocate(512);
        final byte[] protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        T result= (T) schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, result, schema);
        return result;
    }
}
