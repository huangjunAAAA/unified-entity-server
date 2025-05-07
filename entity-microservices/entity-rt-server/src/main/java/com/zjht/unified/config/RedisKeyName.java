package com.zjht.unified.config;

import com.zjht.unified.common.core.constants.Constants;

public class RedisKeyName {
    public static final String RUNNING_PROJECT ="running:entity:";
    public static final String ATTR_REQ ="attr:req:";
    public static final String ATTR_AFFECTED ="attr:affect:";

    public static final String ALL_RUNNING_PROJECT ="running:entity:all";


    public static String getEntityProjectKey(Object id){
        return RUNNING_PROJECT +id;
    }

    public static String getPreQKey(String ver, String qualifiedName){
        return ver+":"+ ATTR_REQ +qualifiedName;
    }

    public static String getPreQOriginalKey(String key){
        String[] parts = key.split(ATTR_REQ);
        return parts[parts.length-1];
    }

    public static String getAttrDepKey(String ver, String qualifiedName){
        return ver+":"+ ATTR_AFFECTED +qualifiedName;
    }

    public static String nameGUIDKey(String ver,String name,String prjGuid,String prjVer){
        return ver+":"+ Constants.STATIC_NAME_GUID+ ":"+name+":"+prjGuid+":"+prjVer;
    }

    public static String getDepOriginalKey(String key){
        String[] parts = key.split(ATTR_AFFECTED);
        return parts[parts.length-1];
    }

    public static String getStaticKey(String prefix,String ver,String prjGuid,String prjVer){
        return prefix+":"+ver+":"+prjGuid+":"+prjVer;
    }

    /**
     * 用来存储属性的计算公式（如果有）
     * @param clsGuid
     * @param ver
     * @param attrName
     * @return
     */
    public static String getObjectAttrDefKey(String prjVer, String clsGuid, String ver, String attrName){
        return ver+":"+prjVer+":"+clsGuid+":"+attrName;
    }

    public static String getObjectAttrDefKey(String prjVer, String fieldGuid, String ver){
        return ver+":"+prjVer+":"+fieldGuid;
    }

    public static String getObjectKey(String guid,String ver,String prjGuid,String prjVer){
        return prjGuid+":"+prjVer+":"+ver+":"+guid;
    }

    public static String getClsDefKey(String ver,String prjVer,String clsGuid){
        return ver+":"+prjVer+":"+clsGuid;
    }

    public static String getObjectRtKey(String guid,String ver,String prjGuid,String prjVer){
        return prjGuid+":"+prjVer+":"+ver+":rt:"+guid;
    }
}
