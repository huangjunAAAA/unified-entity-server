package com.zjht.unified.wrapper;
import com.wukong.core.util.SpringUtil;
import com.zjht.unified.dto.*;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.entity.MethodDef;
import com.zjht.unified.service.IClazzDefService;
import com.zjht.unified.service.IMethodDefService;

import java.util.UUID;


public class MethodDefCompositeValidate{
    public static void preValidate(MethodDefCompositeDTO methodDef){
		if(methodDef.getGuid()==null){
			methodDef.setGuid(UUID.randomUUID().toString());
		}
  	}
  
    public static void preCopy(MethodDefCompositeDTO methodDef){
		methodDef.setGuid(UUID.randomUUID().toString());
  	}
  
    public static boolean validateOnFlush(MethodDefCompositeDTO methodDef){
		IClazzDefService clazzDefService = SpringUtil.getBean(IClazzDefService.class);
		ClazzDef cls = clazzDefService.getById(methodDef.getClazzId());
		methodDef.setClazzGuid(cls.getGuid());
		return true;
  	}
  
    public static boolean validateOnCopy(MethodDefCompositeDTO methodDef){
    	return false;
  	}
}