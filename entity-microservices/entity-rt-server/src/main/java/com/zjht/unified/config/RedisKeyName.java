package com.zjht.unified.config;

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

    public static String getDepOriginalKey(String key){
        String[] parts = key.split(ATTR_AFFECTED);
        return parts[parts.length-1];
    }

    public static String getObjectAttrKey(String guid,String ver,String attrName){
        return ver+":"+guid+":"+attrName;
    }
}
