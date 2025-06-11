package com.zjht.unified.wrapper;
import com.zjht.unified.dto.*;

import java.util.UUID;


public class ViewDefCompositeValidate{
    public static void preValidate(ViewDefCompositeDTO viewDef){
    	if(viewDef.getGuid()==null){
			viewDef.setGuid(UUID.randomUUID().toString());
		}
  	}
  
    public static void preCopy(ViewDefCompositeDTO viewDef){
    	viewDef.setGuid(UUID.randomUUID().toString());
  	}
  
    public static boolean validateOnFlush(ViewDefCompositeDTO viewDef){
    	return false;
  	}
  
    public static boolean validateOnCopy(ViewDefCompositeDTO viewDef){
    	return false;
  	}
}