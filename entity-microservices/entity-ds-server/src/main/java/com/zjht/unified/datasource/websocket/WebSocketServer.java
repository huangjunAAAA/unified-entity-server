package com.zjht.unified.datasource.websocket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wukong.core.weblog.utils.JsonUtil;
import com.zjht.authcenter.permission.R;
import com.zjht.authcenter.permission.entity.SysUser;
import com.zjht.authcenter.token.TokenService;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.datasource.dto.RegInfo;
import com.zjht.unified.datasource.dto.response.Feedback;
import com.zjht.unified.datasource.dto.response.WsNotice;
import com.zjht.unified.datasource.service.DataEventCenter;
import com.zjht.unified.datasource.util.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * websocket 消息处理
 *
 * @author zjht
 */
@Component
@ServerEndpoint(value = "/websocket/event",configurator = CorsConfigurator.class)
@Slf4j
public class WebSocketServer {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private WebSocketUsers webSocketUsers;

    @Autowired
    private DataEventCenter responseCenter;

    /**
     * 解析用户
     * @param session
     * @return
     * @throws IOException
     */
    private WebSocketSysUser getUserByToken(Session session){
        WebSocketSysUser user=new WebSocketSysUser();
        user.setSession(session);

        String token = getParameter(session,"token");
        SysUser su = tokenService.getUserByTokenFromDB(token);
        if(null!=su){
            if(user.getUserId()==null)
                user.setUserId(su.getUserId());
            if(user.getUserName()==null)
                user.setUserName(su.getUserName());
        }

        if(user.getUserId()==null)
            user.setUserId(0L+new Random().nextInt(10000));
        if(user.getUserName()==null)
            user.setUserName("user-"+user.getUserId());
        user.setToken(token);
        return user;
    }

    private String getParameter(Session session,String pName){
        List<String> vlst = session.getRequestParameterMap().get(pName);
        if(vlst == null || vlst.size() == 0){
            return null;
        }
        return vlst.get(0);
    }



    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws Exception {
        WebSocketSysUser loginUser = getUserByToken(session);
        if (loginUser == null)
        {
            WebSocketUtils.sendMessageToUser(session, R.fail("登录状态已过期"));
            session.close();
            return;
        }
        webSocketUsers.put(loginUser, loginUser.getToken());
        log.info("\n 建立连接 - {}", loginUser.getUserName());
        log.info("\n 当前人数 - {}", webSocketUsers.getUsers().size());
        Feedback<String> ok=new Feedback();
        ok.setFeedbackType(Constants.FEEDBACK_TYPE_REGISTER);
        ok.setTs(new Date());
        ok.setConnectionId(session.getId());
        ok.setDataSet(JsonUtil.toJson(new WsNotice("info","connection successful")));
        WebSocketUtils.sendMessageToUser(session, ok);
    }

    private boolean exceedDisplayCardLimit(String cardModel,int connCount){
        log.info("video display card:"+cardModel+", panel:"+connCount);
        return false;
    }

    /**
     * 连接关闭时处理
     */
    @OnClose
    public void onClose(Session session) {
        log.info("\n 关闭连接 - {}", session);
        // 移除用户
        webSocketUsers.remove(session.getId());
    }

    /**
     * 抛出异常时处理
     */
    @OnError
    public void onError(Session session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            // 关闭连接
            session.close();
        }
        String sessionId = session.getId();
        // 移出用户
        webSocketUsers.remove(sessionId);
        responseCenter.unbind(sessionId);

        log.error("\n 连接异常 - {}", sessionId);
        log.error("\n 异常信息 - "+exception.getMessage(),exception);
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        ObjectMapper mapper=new ObjectMapper();
        try {
            RegInfo regInfo = mapper.readValue(message, RegInfo.class);
            responseCenter.onMessage(regInfo,session.getId());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(),e);
        }
    }
}
