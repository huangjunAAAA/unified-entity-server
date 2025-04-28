package com.zjht.unified.datasource.service;



import com.zjht.unified.datasource.util.RedisConfHelper;
import com.zjht.unified.common.core.json.GsonUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class RedisChannelService {

    private ConcurrentHashMap<String, RedisContext> redisConnections=new ConcurrentHashMap<>();

    public RedisMessageListenerContainer createListenerContainer(String connectionString){
        RedisContext rtx = redisConnections.get(connectionString);
        if(rtx==null){
            log.info("createListenerContainer: {}",connectionString);
            rtx=createRedisContext(connectionString);
        }
        return rtx.redisMessageListenerContainer;
    }

    public StringRedisTemplate createRedisTemplate(String connectionString){
        RedisContext rtx = redisConnections.get(connectionString);
        if(rtx==null){
            rtx=createRedisContext(connectionString);
        }
        return rtx.redisTemplate;
    }

    public RedisConnectionFactory createRedisFactory(String connectionString){
        RedisContext rtx = redisConnections.get(connectionString);
        if(rtx==null){
            rtx=createRedisContext(connectionString);
        }
        return rtx.redisConnectionFactory;
    }

    private synchronized RedisContext createRedisContext(String connectionString){
        RedisContext rtx = redisConnections.get(connectionString);
        if(rtx!=null){
            return rtx;
        }

        log.info("createRedisContext: {}",connectionString);
        RedisProperties redis= GsonUtil.fromJson(connectionString,RedisProperties.class);
        JedisConnectionFactory f=null;
        if(redis.getCluster()!=null){
            RedisClusterConfiguration rconf = RedisConfHelper.getClusterConfiguration(redis);
            f=new JedisConnectionFactory(rconf);
        }else if(redis.getSentinel()!=null){
            RedisSentinelConfiguration rconf = RedisConfHelper.getSentinelConfig(redis);
            f=new JedisConnectionFactory(rconf);
        }else{
            RedisStandaloneConfiguration rconf = RedisConfHelper.getStandaloneConfig(redis);
            f=new JedisConnectionFactory(rconf);
        }
        if(f==null)
            throw new RuntimeException("invalid redis configuration:"+redis);
        f.afterPropertiesSet();
        rtx=new RedisContext(f);
        redisConnections.put(connectionString,rtx);
        log.info("createRedisContext successful: {}",connectionString);
        return rtx;
    }

    @NoArgsConstructor
    private class RedisContext{

        public RedisContext(RedisConnectionFactory f){
            redisMessageListenerContainer=new RedisMessageListenerContainer();
            redisMessageListenerContainer.setConnectionFactory(f);
            redisMessageListenerContainer.afterPropertiesSet();
            redisMessageListenerContainer.setTaskExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
            redisMessageListenerContainer.start();
            redisTemplate=new StringRedisTemplate(f);
            redisTemplate.afterPropertiesSet();
        }

        RedisMessageListenerContainer redisMessageListenerContainer;
        StringRedisTemplate redisTemplate;
        RedisConnectionFactory redisConnectionFactory;
    }
}
