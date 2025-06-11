package com.zjht.unified.wrapper;
import com.wukong.core.util.SpringUtil;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.dto.*;
import com.zjht.unified.entity.ClazzDef;
import com.zjht.unified.service.IClazzDefService;


public class ClazzDefCompositeValidate{
    public static void preValidate(ClazzDefCompositeDTO clazzDef){
		if(clazzDef.getGuid()==null)
			clazzDef.setGuid(UUID.fastUUID().toString());
		if(clazzDef.getParentId()!=null){
			IClazzDefService clazzDefService =SpringUtil.getBean(IClazzDefService.class);
			ClazzDef parent = clazzDefService.getById(clazzDef.getParentId());
			if(parent!=null){
				clazzDef.setParentGuid(parent.getGuid());
				clazzDef.setParentPrj(parent.getPrjId());
			}
		}
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