package com.zjht.unified.wrapper;
import com.zjht.unified.common.core.util.UUID;
import com.zjht.unified.dto.*;


public class ClsRelationCompositeValidate{
    public static void preValidate(ClsRelationCompositeDTO clsRelation){
    	if(clsRelation.getGuid()==null){
			clsRelation.setGuid(UUID.fastUUID().toString());
		}
  	}
  
    public static void preCopy(ClsRelationCompositeDTO clsRelation){
		clsRelation.setGuid(UUID.fastUUID().toString());
  	}
  
    public static boolean validateOnFlush(ClsRelationCompositeDTO clsRelation){
    	return false;
  	}
  
    public static boolean validateOnCopy(ClsRelationCompositeDTO clsRelation){
    	return false;
  	}
}