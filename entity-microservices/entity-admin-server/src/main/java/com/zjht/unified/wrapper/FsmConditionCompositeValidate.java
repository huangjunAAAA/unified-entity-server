package com.zjht.unified.wrapper;
import com.wukong.core.util.SpringUtil;
import com.zjht.unified.dto.*;
import com.zjht.unified.entity.FsmDef;
import com.zjht.unified.service.IFsmDefService;


public class FsmConditionCompositeValidate{
    public static void preValidate(FsmConditionCompositeDTO fsmCondition){

  	}
  
    public static void preCopy(FsmConditionCompositeDTO fsmCondition){
    
  	}
  
    public static boolean validateOnFlush(FsmConditionCompositeDTO fsmCondition){
		IFsmDefService fsmDefService= SpringUtil.getBean(IFsmDefService.class);
		FsmDef fsm = fsmDefService.getById(fsmCondition.getFsmId());
		fsmCondition.setFsmGuid(fsm.getGuid());
    	return true;
  	}
  
    public static boolean validateOnCopy(FsmConditionCompositeDTO fsmCondition){
    	return false;
  	}
}