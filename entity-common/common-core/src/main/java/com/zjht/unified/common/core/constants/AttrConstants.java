package com.zjht.unified.common.core.constants;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AttrConstants {

    public static final String ID = "ID";

    public static final String PRJ_ID = "工程ID";

    public static final String UPPER_ID = "上级设备ID";

    public static final String TEMPLATE_ID = "模板ID";

    public static final String REFERENCABLE = "可引用性";

    public static final String VISIBILITY = "可见性";

    public static final String UPDATE_TIME = "更新时间";

    public static final String CREATE_TIME = "创建时间";

    public static final String ED_TEMPLATE_ID = "ED模板ID";

    public static final String ED_SRC_ID = "ED源点ID";

    public static final String BAUDRATE = "波特率";

    public static final String MAX_CONN = "最大连接";

    public static final String CONNECT_TIMEOUT = "连接超时";

    public static final String READ_TIMEOUT = "数据超时";

    public static final String MAX_SURVIVE = "链接最大存活时间";

    public static final String MEDIA_TYPE = "媒体类型";

    public static final String FORCE_H264 = "强制H264";

    public static final String BITRATE = "码率";

    public static final String MODBUS_OFFSET = "Modbus位偏移";

    public static final String ZH_ABBR = "中文简称";

    public static final String EN_ABBR = "英文简称";

    public static final String REMARK = "描述";

    public static final String XY_SYSTEM = "XY坐标系";

    public static final String X = "纬度/X轴坐标";

    public static final String Y = "经度/Y轴坐标";

    public static final String Z = "高程/Z轴坐标";

    public static final String DEVICE_TYPE = "设备类型";

    public static final String DEIVCE_NO = "设备编码";

    public static final String DEVICE_SUBTYPE = "设备子类型";

    public static final String MAC = "MAC地址";

    public static final String COM_NO = "串口号";

    public static final String START_BIT = "起始位";

    public static final String END_BIT = "终止位";

    public static final String MY_DOMAIN = "国标本域";

    public static final String _INIT_PARAM = "初始化参数";

    public static final String POINT_TYPE = "点类型";

    public static final String POINT_NO = "点编号";

    public static final String CALCULATION = "计算公式";

    public static final String SERVING_DOMAIN = "服务域名";

    public static final String MEDIA_URL = "音视频URL";

    public static final String MODBUS_PHY = "Modbus物理地址";

    public static final String MODBUS_VALTYPE = "Modbus值类型";

    public static final String MODBUS_BIT_OFFSET = "Modbus单位长度";

    public static final String _ED = "ED模板";

    public static final String ED_NAME_ZH = "ED模板中文名#";

    public static final String ED_NAME_EN = "ED模板英文名#";

    public static final String ED_RULE = "ED模板创建规则#";

    public static final String ED_FIELD = "ED模板字段列表#";

    public static final String ED_SQL = "ED模板SQL定义#";

    public static final String PARAM_TEMPLATE = "参数模板";

    public static final String HTTP_API_ID = "Http接口ID";

    public static final String HTTP_METHOD = "访问方式";

    public static final String MAX_RETRY = "最大重试";

    public static final String AUTH_KEY = "鉴权Key";

    public static final String AUTH_SECRET = "鉴权Secret";

    public static final String AUTH_URL = "鉴权Url";

    public static final String AUTH_METHOD = "鉴权方式";

    public static final String AUTH_ID = "鉴权ID";

    public static final String _OBJ_CURRENT_VALUE="pv";

    public static final String _OBJ_CURRENT_RAW_VALUE="raw";

    public static final String _OBJ_LAST_UPDATE="ldate";

    public static final String VIDEO_CHANNEL="通道号";

    public static final String[] PROGAMMABLE_ATTRS = new String[]{ZH_ABBR,EN_ABBR,REMARK,XY_SYSTEM,X,Y,Z,
            DEVICE_TYPE,DEIVCE_NO,DEVICE_SUBTYPE,MAC,COM_NO,START_BIT,END_BIT,MY_DOMAIN,POINT_TYPE,POINT_NO,CALCULATION,
            SERVING_DOMAIN,MEDIA_URL,MODBUS_PHY,MODBUS_VALTYPE,MODBUS_BIT_OFFSET,
            PARAM_TEMPLATE,HTTP_API_ID,HTTP_METHOD,READ_TIMEOUT,CONNECT_TIMEOUT,
            MAX_RETRY,AUTH_KEY,AUTH_SECRET,AUTH_URL,AUTH_METHOD,AUTH_ID};

    public static final String[] DYNAMIC_ATTRS = new String[]{
            _OBJ_CURRENT_VALUE,X,Y,Z,VIDEO_CHANNEL
    };

    public static final String[] ALL_FUNCS=new String[]{
            "_P","_DP","_SDP","_SPDX","_D","_SD","_S","_DPX","_PX","_ED","_EDP","_A","_AX"
    };

    public static Map<String, String> allAttrs() {
        Field[] fields = FieldUtils.getAllFields(AttrConstants.class);
        Map<String, String> mapping = new HashMap<>();
        for (int i = 0; i < fields.length; i++)
            try {
                Field field = fields[i];
                if (field.getName().startsWith("_"))
                    continue;
                String val = field.get(null) + "";
                mapping.put(field.getName(), val);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        return mapping;
    }

}
