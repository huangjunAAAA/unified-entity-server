package com.zjht.unified.datasource.websocket;

import com.zjht.authcenter.permission.entity.SysUser;
import lombok.Data;

import javax.websocket.Session;

@Data
public class WebSocketSysUser extends SysUser {
    private Session session;
    private String token;
}
