package com.zjht.unified.wrapper;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.dto.*;


public class ClazzDefCompositeValidate{
    public static void preValidate(ClazzDefCompositeDTO clazzDef){
		if(clazzDef.getGuid()==null)
			clazzDef.setGuid(UUID.fastUUID().toString());
  	}
  
    public static void preCopy(ClazzDefCompositeDTO clazzDef){
		clazzDef.setGuid(UUID.fastUUID().toString());
  	}
  
    public static boolean validateOnFlush(ClazzDefCompositeDTO clazzDef){
    	return false;
  	}
  
    public static boolean validateOnCopy(ClazzDefCompositeDTO clazzDef){
    	return false;
  	}
}