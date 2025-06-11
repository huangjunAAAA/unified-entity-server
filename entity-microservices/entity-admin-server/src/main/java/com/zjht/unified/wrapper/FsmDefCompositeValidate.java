package com.zjht.unified.wrapper;
import com.zjht.unified.dto.*;

import java.util.UUID;


public class FsmDefCompositeValidate{
    public static void preValidate(FsmDefCompositeDTO fsmDef){
    	if(fsmDef.getGuid()==null){
			fsmDef.setGuid(UUID.randomUUID().toString());
		}
  	}
  
    public static void preCopy(FsmDefCompositeDTO fsmDef){
    	fsmDef.setGuid(UUID.randomUUID().toString());
  	}
  
    public static boolean validateOnFlush(FsmDefCompositeDTO fsmDef){
    	return false;
  	}
  
    public static boolean validateOnCopy(FsmDefCompositeDTO fsmDef){
    	return false;
  	}
}