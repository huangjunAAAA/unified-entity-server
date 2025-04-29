package com.zjht.unified.datasource.websocket;



import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.datasource.util.WebSocketUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * websocket 客户端用户集
 * 
 * @author zjht
 */
@Service
@Slf4j
public class WebSocketUsers
{

    /**
     * 用户集
     */
    private Map<String, WebSocketSysUser> users = new ConcurrentHashMap<>();

    private Map<String, ConcurrentHashMap<String,WebSocketSysUser>> tokenToConn=new ConcurrentHashMap<>();

    /**
     * 存储用户
     *
     * @param user 用户信息
     * @param token
     */
    public void put(WebSocketSysUser user, String token)
    {
        users.put(user.getSession().getId(), user);
        if(StringUtils.isNotEmpty(token)){
            ConcurrentHashMap<String, WebSocketSysUser> conns = tokenToConn.get(token);
            if(conns==null){
                tokenToConn.put(token,new ConcurrentHashMap<>());
                conns=tokenToConn.get(token);
            }
            conns.put(user.getSession().getId(),user);
        }
    }

    public int getTokenConn(String token){
        if(StringUtils.isEmpty(token))
            return 0;
        ConcurrentHashMap<String, WebSocketSysUser> conns = tokenToConn.get(token);
        if(conns==null){
            return 0;
        }else {
            return conns.size();
        }
    }

    /**
     * 移除用户
     *
     * @return 移除结果
     */
    public void removeUser(Long userId)
    {
        List<WebSocketSysUser> allUserConns = users.values().stream().filter(t -> t.getUserId().equals(userId)).collect(Collectors.toList());
        allUserConns.stream().forEach(t->{
            remove(t.getSession().getId());
        });
    }

    /**
     * 移出用户
     *
     * @param key 键
     */
    public Session remove(String key)
    {
        log.debug("\n 正在移出用户 - {}", key);
        WebSocketSysUser removed = users.remove(key);
        if (removed != null){
            return removed.getSession();
        }
        for (Iterator<ConcurrentHashMap<String,WebSocketSysUser>> iterator = tokenToConn.values().iterator(); iterator.hasNext(); ) {
            ConcurrentHashMap<String,WebSocketSysUser> conns =  iterator.next();
            if(null!=conns.remove(key)){
                break;
            }
        }
        return null;
    }

    /**
     * 获取在线用户列表
     *
     * @return 返回用户集合
     */
    public Map<String, WebSocketSysUser> getUsers(){
        return users;
    }

    public void setMessageToUser(String sessionId, Object r){
        WebSocketSysUser userconn = users.get(sessionId);
        if (userconn != null) {
            synchronized (userconn) {
                WebSocketUtils.sendMessageToUser(userconn.getSession(), r);
            }
        }
    }


}
