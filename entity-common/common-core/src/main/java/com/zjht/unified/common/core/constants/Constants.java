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

    public static final String DORIS_ID_PREFIX="DORIS:ID:";

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
    public static final int TRUE=1;
    public static final int FALSE=0;

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
    public static final int SUCCESS_A = 0;

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


    /**
     * 文件所属类型
     */
    public static final String FILE_TYPE_PROJECT_EXTRA = "project-extra";
    public static final String FILE_TYPE_PROJECT_ROUTE = "project-route";
    public static final String FILE_TYPE_PROJECT_NODE_MODULE="node_modules";

    /**
     * 方案类型
     */
    public static final String PLAN_TYPE_SYNC = "1"; //同步
    public static final String PLAN_TYPE_OUTPUT = "2";//输出

    /**
     * 方案操作类型
     */
    public static final String OPERTOR_TYPE_GRAB = "1"; //抓取
    public static final String OPERTOR_TYPE_PUSH = "2"; //推送
    public static final String OPERTOR_TYPE_IMPORT = "3"; //导入

    /**
     * 文案更新类型
     */
    public static final String UPDATE_TYPE_INSERT = "1"; //新增
    public static final String UPDATE_TYEP_UPDATE = "2"; //更新

    /**
     * 方案范围类型
     */
    public static final String SCOPE_TYPE_AREA = "1";
    public static final String SCOPE_TYPE_MANUFACTURER = "2";
    public static final String SCOPE_TYPE_CATEGORY = "3";


    /**
     * 配置类型
     */
    public static final String PROCESS_TYPE_API = "1";
    public static final String PROCESS_TYPE_SDK = "2";
    public static final String PROCESS_TYPE_IMPORT = "3";

    /**
     * token类型
     */
    public static final String TOKEN_TYPE_HEADER = "1";
    public static final String TOKEN_TYPE_PARAM = "2";





    /**
     * sdk请求标识
     */
    public static final String SDK_REQUEST_TYPE_NORMAL = "1";
    public static final String SDK_REQUEST_TYPE_JSON = "2";

    /**
     * 状态
     */
    public static final int STATUS_CONFIGURE = 1 ; // 正常
    public static final int STATUS_DEPLOY= 2 ; // 发布中


    /**
     * 过期时间类型
     */
    public static final String EXPRIRED_TYPE_MILLISECONDS = "1";
    public static final String EXPRIRED_TYPE_SECONDS = "2";
    public static final String EXPRIRED_TYPE_MINUTE = "3";
    public static final String EXPRIRED_TYPE_HOUR = "4";
    public static final String EXPRIRED_TYPE_DAY = "5";


    public static final int DATA_SOURCE_IMPORT=1;
    public static final int DATA_SOURCE_MANUAL=2;

    /**
     * 分组成员类型
     */
    public static final int MEMBER_TYPE_NODE=1;
    public static final int MEMBER_TYPE_OTHER=3;
    public static final int MEMBER_TYPE_CONNECTOR=1;
    public static final int MEMBER_TYPE_PIPE=2;

    /**
     * 对象类型
     */
    public static final String OBJECT_TYPE_EQUIPMENT = "1";
    public static final String OBJECT_TYPE_CATEGORY = "2";
    public static final String OBJECT_TYPE_AREA = "3";
    public static final String OBJECT_TYPE_WARNRECORD = "4";

    /**
     * 在线状态
     */
    public static final String ONLINE_STATUS_ON = "1";
    public static final String ONLINE_STATUS_OFF = "2";

    /**
     * 区域来源
     */

    public static final String SOURCEBY_ADD = "1";
    public static final String SOURCEBY_API = "2";

    /**
     * 报警配置类型
     */
    public static final String WARN_TYPE_BUSI = "1";
    public static final String WARN_TYPE_MEG = "2";

    /**
     * 报警消息类型
     */
    public static final String WARN_RECORD_TYPE_LOCAL = "1";
    public static final String WARN_RECORD_TYPE_THRID = "2";

    /**
     * 报警消息状态
     */
    public static final String WARN_STATUS_UNCONFIRMED = "1";
    public static final String WARN_STATUS_CONFIRMED = "2"; //已确认
    /**
     * 报警消息来源
     */
    public static final String THRID_SYSTEM_LOCAL = "local";
    public static final String THRID_SYSTEM_INTELLIGENCE = "intelligence";
    public static final String THRID_SYSTEM_UNIVIEW = "uniview";

    /**
     * 指令开关
     */
    public static final String INSTRUCTIONS_OPEN = "2";
    public static final String INSTRUCTIONS_CLOSED = "1";

    /**
     * 设备开关状态
     */
    public static final String ACTIVE_STATUS_ON = "1";
    public static final String ACTIVE_STATUS_OFF = "0";

    /**
     * 报警
     */
    //报警级别字典
    public static final String WARN_LEVEL_DiCT = "warn_level";
    //业务类型
    public static final String WARN_BUSINESS_PASSENGER = "1";//客流


    //报警规则比较方式
    public static final String WARN_RULE_COMPARETYPE_GT = "1";
    public static final String WARN_RULE_COMPARETYPE_EQ = "2";
    public static final String WARN_RULE_COMPARETYPE_LT = "3";

    public static final int CELLID_NODE =0;
    public static final int CELLID_EDGE =1;
    public static final int CELLID_LAYER =2;
    public static final int CELLID_SELECTOR=3;

    public static final int BINDING_SOURCE_LIVEFEED=1;
    public static final int BINDING_SOURCE_SELECTOR=2;
    public static final int BINDING_SOURCE_DRAWING=3;

    public static final int NODETYPE_START=0;
    public static final int NODETYPE_END=1;
    public static final int NODETYPE_REGULAR=2;
    public static final int NODETYPE_VIRTUAL=3;

    public static final int DRAWING_STATUS_DELETED=0;
    public static final int DRAWING_STATUS_NORMAL=1;
    public static final int DRAWING_STATUS_LOCKED=2;

    //    public static final int DRAWING_TYPE_DEFAULT=1;
    public static final int DRAWING_TYPE_NORMAL=2;
    public static final int DRAWING_TYPE_COMMONINUSE=3;
    public static final int DRAWING_TYPE_PREVIEW=4;

    public static final int ACCESS_ADMIN=2;
    public static final int ACCESS_RW=1;
    public static final int NO_ACCESS=0;
    public static final int INVALID_ACCESS=4;

    public static final String BINDTYPE_LIVEFEED="livefeed";
    public static final String BINDTYPE_BIZ_SYSTEM="biz_sys";

    public  static final String CellClick = "cell:click"; // 图元单击
    public  static final String CellDblclick = "cell:dblclick"; // 图元双击
    public  static final String CellContextmenu = "cell:contextmenu"; // 图元右键
    public  static final String CellMouseenter = "cell:mouseenter"; // 图元鼠标移入
    public  static final String CellMouseleave = "cell:mouseleave";

    public static final Integer FEEDBACK_TYPE_DATA=1;

    public static final Integer FEEDBACK_TYPE_UIEVENT=2;

    public static final Integer FEEDBACK_TYPE_REGISTER=3;

    public static final Integer FEEDBACK_TYPE_RELOAD=4;

    public static final String SDP_DIRECTORY="Directory";

    public static final String SDP_SYSTEM="System";

    public static final String SDP_DEVICE="Device";

    public static final String SDP_POINT="Point";

    public static final String SDP_TEMPLATE="SDP:TEMPLATE";

    public static final String SDP_ATTR="SDP:ATTR";

    public static final String SDP_UDA="SDP:UDA";


    public static final String SDP_POINT_TYPE_ANALOG="TYPE_A";
    public static final String SDP_POINT_TYPE_DIGIAL="TYPE_S";
    public static final String SDP_POINT_TYPE_GEO="TYPE_G";
    public static final String SDP_POINT_TYPE_TEXT="TYPE_T";
    public static final String SDP_POINT_TYPE_STREAM="TYPE_M";
    public static final String SDP_POINT_TYPE_CRAWL="TYPE_C";
    public static final String SDP_POINT_TYPE_RECORD="TYPE_R";
    public static final String SDP_POINT_TYPE_SWITCH="TYPE_D";

    public static final String SDP_DEVICE_TYPE_FIXED="DEVICE_FD";
    public static final String SDP_DEVICE_TYPE_MOBILE="DEVICE_MD";
    public static final String SDP_DEVICE_TYPE_DISCOVERABLE="DEVICE_DD";
    public static final String SDP_DEVICE_TYPE_ENTITY_DYNAMIC="DEVICE_ED";
    public static final String SDP_DEVICE_TYPE_LOGICAL="DEVICE_LD";
    public static final String SDP_DEVICE_TYPE_SPECIAL="DEVICE_SD";

    public static final String AGGR_COUNT ="kount";
    public static final String AGGR_MIN ="min";
    public static final String AGGR_MAX ="max";
    public static final String AGGR_AVERAGE ="avg";
    public static final String AGGR_SUM ="sum";
    public static final String COL_DATA_TIME="data_time";

    public static final String ED_RULE_SQL="sql";
    public static final String ED_DERIVED_DEVICE_ID_SEQ="ed_derived_id";
    public static final String ED_DERIVED_POINT_ID_SEQ="ed_point_id";

    public static final String EXCHANGE_NAME="external-exchange";
    public static final String EXTERNAL_ALERT_QUEUE="external-alert-list";
    public static final String EXTERNAL_ALERT_QUEUE_KEY="external-alert-key";

    public static final String ALERT_TRIGGER_RANGE="range";
    public static final String ALERT_TRIGGER_SWITCH="switch";
    public static final String ALERT_TRIGGER_ENUM="enum";
    public static final String ALERT_TRIGGER_NON_ENUM="non-enum";

    public static final Integer VIDEO_STREAM=1;
    public static final Integer AUDIO_STREAM=2;
    public static final Integer PICTURE=3;

    public static final String DISCOVERABLE_DEVICE_ID_SEQ="discoverable_device_id";
    public static final String DISCOVERABLE_POINT_ID_SEQ="discoverable_point_id";

    public static final String SDP_TEMPLATE_TYPE_GENERAL="sdp";
    public static final String SDP_TEMPLATE_TYPE_ED="ed";

    public static final String HOLDING_REGISTER="1";
    public static final String DATA_INFER_REGISTER="2";

    public static final String EVENT_TYPE_CONTEXT="context";
    public static final String EVENT_TYPE_REGULAR="event";

    public static final String CC_BELONG_TO_PAGE="page";
    public static final String CC_BELONG_TO_CELL="cell";

    public static final String SCRIPT_FRONT="front";
    public static final String SCRIPT_BACKEND="backend";
    public static final String SCRIPT_FRONT_SUBMIT="front-submit";

    public static final String VITE_IN_RUNNING="runningVite";


    public static final String STATICS = "static";
    public static final String STATIC_TYPE_SENTINEL="static_sentinel";
    public static final String STATIC_TYPE_FSM="static_fsm";
    public static final String STATIC_NAME_GUID="name_guid";
}
