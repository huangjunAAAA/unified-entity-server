package com.zjht.unified.service.ctx;

public interface StaticMgmt {
    public <T> T getObject(String type,String guid);
    public void setObject(String type,String guid,Object obj);
}
