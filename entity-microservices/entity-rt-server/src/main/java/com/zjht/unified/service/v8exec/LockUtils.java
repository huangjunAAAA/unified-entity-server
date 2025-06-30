package com.zjht.unified.service.v8exec;

import com.zjht.unified.service.ctx.TaskContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

public class LockUtils {
    private String prjGuid;

    private String prjVer;

    public LockUtils(TaskContext taskContext, String prjGuid, String prjVer) {
        this.taskContext = taskContext;
        this.prjGuid = prjGuid;
        this.prjVer = prjVer;
    }

    private TaskContext taskContext;
}
