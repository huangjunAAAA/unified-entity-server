package com.zjht.unified.service;

import com.zjht.unified.service.ctx.TaskContext;

import java.util.Map;

/**
 * var a=ClassUtils.new("ClassA");
 * var b=a.a1
 * a.a1="a1 alter"
 * var b1=InstanceUtils.get(a.guid);
 */
public interface IScriptEngine {
    Object exec(String script, Map<String, Object> params, TaskContext ctx, String prjGuid, String prjVer);
}
