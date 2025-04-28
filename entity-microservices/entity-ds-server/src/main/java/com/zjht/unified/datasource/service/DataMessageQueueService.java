package com.zjht.unified.datasource.service;


import com.zjht.unified.datasource.dto.DataValUpdate;
import com.zjht.unified.common.core.json.GsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class DataMessageQueueService {

    @Autowired
    private RedisChannelService redisChannelService;

    private ConcurrentHashMap<String,DataValUpdate.DataMeta> currentMeta=new ConcurrentHashMap<>();

    private ConcurrentHashMap<DataMQCallback,CallbackCtx> delegates=new ConcurrentHashMap<>();


    public interface DataMQCallback{
        void OnMessage(DataValUpdate msg, StringRedisTemplate redisTemplate);
    }
    public void registerOnMessage(String type,String connectionString,String topic,DataMQCallback callback){
        log.info("registerOnMessage: {},{},{},{}",type,connectionString,topic,callback);
        if(type.equalsIgnoreCase("redis")){
            RedisDataCallback internalCallback=new RedisDataCallback(callback,connectionString);
            RedisMessageListenerContainer listenerContainer = redisChannelService.createListenerContainer(connectionString);
            listenerContainer.addMessageListener(internalCallback,new PatternTopic(topic));
            listenerContainer.afterPropertiesSet();
            log.info("registerOnMessage add listener: {},{}",topic,callback);
            delegates.put(callback,new CallbackCtx(internalCallback,type,listenerContainer));
        }
    }

    public void removeCallback(DataMQCallback callback){
        CallbackCtx ctx = delegates.remove(callback);
        if(ctx.mqType.equalsIgnoreCase("redis")){
            ctx.listenerContainer.removeMessageListener((MessageListener) ctx.delegate);
        }
    }

    @AllArgsConstructor
    private static class CallbackCtx{
        Object delegate;
        String mqType;
        RedisMessageListenerContainer listenerContainer;
    }

    @AllArgsConstructor
    private class RedisDataCallback implements MessageListener {

        private DataMQCallback callback;

        private String connectionString;

        @Override
        public void onMessage(Message message, byte[] pattern) {
            String key=new String(message.getBody());
            String channel=new String(message.getChannel());
            log.debug("receive message from stream channel: "+channel+", data: "+key);
            StringRedisTemplate redisTemplate=redisChannelService.createRedisTemplate(connectionString);
            String dataVal = redisTemplate.opsForValue().get(key);
            DataValUpdate val= GsonUtil.fromJson(dataVal,DataValUpdate.class);
            DataValUpdate.DataMeta meta = val.getMeta();
            meta.setChannel(channel);
            synchronized (key) {
                DataValUpdate.DataMeta lastMeta = currentMeta.get(meta.getKey());
                if (lastMeta != null && lastMeta.getTs().equals(meta.getTs()))
                    return;
                currentMeta.put(meta.getKey(), meta);
            }
            log.debug("update message from stream channel: "+channel+", data: "+key);
            callback.OnMessage(val,redisTemplate);
        }
    }
}
