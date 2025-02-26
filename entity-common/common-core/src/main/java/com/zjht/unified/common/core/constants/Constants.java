package com.zjht.unified.common.core.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";
    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * quartz 任务配置
     */
    public static final String TASK_CLASS_NAME = "task_name_";

    public static final String JOB_DATA_KEY = "job_id";

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    public static final int CONTINUES=101;

    public static final String AUTH_HEADER = "ignoreValidate";
    public static final String AUTH_IGNORE_VALIDATE = "true";

    /**
     * 方案/任务执行状态
     */
    public static final int EXEC_STATUS_RUNNING = 1;
    public static final int EXEC_STATUS_FINISHED = 2;
    public static final int EXEC_STATUS_FAILED=3;





    /**
     * api请求方法
     */
    public static final String REQUEST_METHOD_GET = "get";
    public static final String REQUEST_METHOD_POST = "post";

    /**
     * api请求体类型
     */
    public static final String BODY_TYPE_FORM = "1";
    public static final String BODY_TYPE_BODY = "2";

    /**
     * 参数类型
     */
    public static final String PARAM_TYPE_OBJECT = "object";
    public static final String PARAM_TYPE_ARRAY = "array";
    public static final String PARAM_TYPE_VAL = "val";



    /**
     * 状态
     */
    public static final int STATUS_DISABLE = 0; // 禁用，不会进入采集计划
    public static final int STATUS_CONFIGURING = 1 ; // 正常 未开始采集
    public static final int STATUS_RUNNING= 2 ; // 正常 采集中
    public static final int STATUS_VERIFYING = 3; //验证中
    public static final int STATUS_OFFLINE = 4; //采集中-故障

    /**
     * 是否
     */
    public static final String YES = "1";
    public static final String NO = "2";

    /**
     * 日志状态
     */
    public static final String LOG_STATUS_SUCCESS = "1";
    public static final String LOG_STATUS_FAIL = "2";

    /**
     * 参数类型
     */
    public static final String VARIABLE_TYPE_STRING = "String";
    public static final String VARIABLE_TYPE_INTEGER = "Integer";
    public static final String VARIABLE_TYPE_LONG = "Long";
    public static final String VARIABLE_TYPE_FLOAT = "Float";
    public static final String VARIABLE_TYPE_DOUBLE = "Double";
    public static final String VARIABLE_TYPE_DATE = "Date";
    public static final String VARIABLE_TYPE_DECIMAL = "BigDecimal";

    public static final List<String> TIME_FORMAT =new ArrayList<>(Arrays.asList("%Y","%Y-%m","%Y-%m-%d","%Y-%m-%d %H"));
    public static final List<String> TIME_SLICE =new ArrayList<>(Arrays.asList("yyyy","yyyy-MM","yyyy-MM-dd","yyyy-MM-dd HH"));
    public static final List<String> TIME_SUFFIX =new ArrayList<>(Arrays.asList("year","month","day","hour"));


    public static final int DATA_MODE_APPEND=1;
    public static final int DATA_MODE_REPLACE=2;

    public static final String DEPLOY_TYPE_DRIVER="driver";
    public static final String DEPLOY_TYPE_DRIVER_CLUSTER="driver_cluster";

    public static final int DEPLOY_READY=1;
    public static final int DEPLOY_START=2;
    public static final int DEPLOY_STOP=3;
    public static final int DEPLOY_FAILED=10;
    public static final int DEPLOY_OFFLINE=11;

    public static final int DEPLOY_EXEC_SUCCESS=0;
    public static final int DEPLOY_EXEC_FAILED=-1;
    public static final int DEPLOY_EXEC_RES_NOT_FOUND =-2;

    public static final String SSH_EXEC_IGNORE = "ignore";
    public static final String SSH_EXEC_BREAK = "break";

    //0 任意类型 1 基础类型 2 普通类 3 结构 4 脚本 5 树节点 6 类关系
    public static int FIELD_TYPE_ANY=0;
    public static int FIELD_TYPE_PRIMITIVE=1;
    public static int FIELD_TYPE_CLASS=2;
    public static int FIELD_TYPE_CONSTRUCT=3;
    public static int FIELD_TYPE_SCRIPT=4;
    public static int FIELD_TYPE_TREE_NODE=5;
    public static int FIELD_TYPE_CLS_REL=6;

    // 状态机驱动方式
    public static int FSM_TIMER=2;
    public static int FSM_WATCHER=1;
}
