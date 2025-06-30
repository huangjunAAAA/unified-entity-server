package com.zjht.unified.service.v8exec;

import com.zjht.unified.service.ctx.TaskContext;

public class LockUtils {
    private TaskContext taskContext;
    private String prjGuid;
    private String prjVer;

    public LockUtils(TaskContext taskContext, String prjGuid, String prjVer) {
        this.taskContext = taskContext;
        this.prjGuid = prjGuid;
        this.prjVer = prjVer;
    }
}
