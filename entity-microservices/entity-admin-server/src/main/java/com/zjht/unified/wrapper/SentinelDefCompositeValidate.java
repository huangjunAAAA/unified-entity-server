package com.zjht.unified.wrapper;
import com.zjht.unified.dto.*;

import java.util.UUID;


public class SentinelDefCompositeValidate{
    public static void preValidate(SentinelDefCompositeDTO sentinelDef){
		if(sentinelDef.getGuid()==null){
			sentinelDef.setGuid(UUID.randomUUID().toString());
		}
  	}
  
    public static void preCopy(SentinelDefCompositeDTO sentinelDef){
    	sentinelDef.setGuid(UUID.randomUUID().toString());
  	}
  
    public static boolean validateOnFlush(SentinelDefCompositeDTO sentinelDef){
    	return false;
  	}
  
    public static boolean validateOnCopy(SentinelDefCompositeDTO sentinelDef){
    	return false;
  	}
}