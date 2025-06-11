package com.zjht.unified.wrapper;
import com.wukong.core.util.SpringUtil;
import com.zjht.unified.dto.*;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.service.IMethodDefService;

import java.util.UUID;


public class MethodParamCompositeValidate{
    public static void preValidate(MethodParamCompositeDTO methodParam){
    	if(methodParam.getGuid()==null){
			methodParam.setGuid(UUID.randomUUID().toString());
		}
  	}
  
    public static void preCopy(MethodParamCompositeDTO methodParam){
		methodParam.setGuid(UUID.randomUUID().toString());
  	}
  
    public static boolean validateOnFlush(MethodParamCompositeDTO methodParam){
		IMethodDefService methodDefService = SpringUtil.getBean(IMethodDefService.class);
		MethodDef method = methodDefService.getById(methodParam.getMethodId());
		methodParam.setMethodGuid(method.getGuid());
    	return true;
  	}
  
    public static boolean validateOnCopy(MethodParamCompositeDTO methodParam){
    	return false;
  	}
}