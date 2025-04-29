package com.zjht.unified.datasource.websocket;

import com.wukong.core.util.SpringUtil;

import javax.websocket.server.ServerEndpointConfig;

public class CorsConfigurator extends ServerEndpointConfig.Configurator{
    @Override
    public boolean checkOrigin(String originHeaderValue) {
        return true;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return SpringUtil.getBean(clazz);
    }
}
