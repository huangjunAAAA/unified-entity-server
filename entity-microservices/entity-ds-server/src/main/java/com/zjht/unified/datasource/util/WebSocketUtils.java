package com.zjht.unified.datasource.util;

import com.wukong.core.weblog.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;

@Slf4j
public class WebSocketUtils {

    /**
     * 发送文本消息
     *
     * @param session 自己的用户名
     * @param message 消息内容
     */
    public static void sendMessageToUserByText(Session session, String message) {
        if(session!=null) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("\n[发送消息异常]", e);
                }
            } else {
                log.debug(session.getId() + "[已离线]");
            }
        }
    }


    public static void sendMessageToUser(Session session, Object r) {
        sendMessageToUserByText(session, JsonUtil.toJson(r));
    }
}
