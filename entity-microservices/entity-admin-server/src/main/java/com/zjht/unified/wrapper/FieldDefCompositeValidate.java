package com.zjht.unified.wrapper;
import com.wukong.core.util.SpringUtil;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.dto.*;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.service.IClazzDefService;


public class FieldDefCompositeValidate{
    public static void preValidate(FieldDefCompositeDTO fieldDef){
    	if(fieldDef.getGuid()==null)
			fieldDef.setGuid(UUID.fastUUID().toString());
  	}
  
    public static void preCopy(FieldDefCompositeDTO fieldDef){
		fieldDef.setGuid(UUID.fastUUID().toString());
  	}
  
    public static boolean validateOnFlush(FieldDefCompositeDTO fieldDef){
		IClazzDefService clazzDefService = SpringUtil.getBean(IClazzDefService.class);
		ClazzDef cls = clazzDefService.getById(fieldDef.getClazzId());
		fieldDef.setClassGuid(cls.getGuid());
		return true;
  	}
  
    public static boolean validateOnCopy(FieldDefCompositeDTO fieldDef){
    	return false;
  	}
}