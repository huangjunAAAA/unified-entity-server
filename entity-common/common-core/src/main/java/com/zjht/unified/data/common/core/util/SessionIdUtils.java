package com.zjht.unified.data.common.core.util;

public class SessionIdUtils {

    public static String randomSessionId(Number planId,Number driverId){
        return planId+"|"+System.currentTimeMillis();
    }

    public static String decodePlanId(String sessionId){
        int idx = sessionId.indexOf("|");
        return sessionId.substring(0,idx);
    }
}
