package com.zjht.unified.admin.utils;

import com.wukong.core.util.BeanCopyUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.simple.ClsRelationDO;
import com.zjht.unified.domain.simple.FsmConditionDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.domain.simple.MethodParamDO;
import com.zjht.unified.dto.ClazzDefCompositeDTO;
import com.zjht.unified.dto.FsmDefCompositeDTO;
import com.zjht.unified.dto.MethodDefCompositeDTO;
import com.zjht.unified.utils.JsonUtilExt;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class EntityDoUtils {
    public static ClazzDefCompositeDO convert(ClazzDefCompositeDTO dto){
        ClazzDefCompositeDO cdo=new ClazzDefCompositeDO();
        BeanCopyUtils.copyProperties(dto,cdo);
        if(CollectionUtils.isNotEmpty(dto.getClazzIdFieldDefList())){
            cdo.setClazzIdFieldDefList(dto.getClazzIdFieldDefList().stream().map(f->{
                FieldDefCompositeDO fdo=new FieldDefCompositeDO();
                if(f.getClsRelIdClsRelationComposite()!=null){
                    fdo.setClsRelIdClsRelation(JsonUtilExt.jsonCast(f.getClsRelIdClsRelationComposite(), ClsRelationDO.class));
                }
                BeanCopyUtils.copyProperties(f,fdo);
                return fdo;
            }).collect(Collectors.toList()));
        }
        if(CollectionUtils.isNotEmpty(dto.getClazzIdMethodDefList())){
            cdo.setClazzIdMethodDefList(dto.getClazzIdMethodDefList().stream().map(m->convert(m)).collect(Collectors.toList()));
        }
        return cdo;
    }

    public static MethodDefCompositeDO convert(MethodDefCompositeDTO dto){
        MethodDefCompositeDO mdo=new MethodDefCompositeDO();
        BeanCopyUtils.copyProperties(dto,mdo);
        if(CollectionUtils.isNotEmpty(dto.getMethodIdMethodParamList())){
            List<MethodParamDO> pLst = dto.getMethodIdMethodParamList().stream().map(p -> {
                MethodParamDO mpo = new MethodParamDO();
                BeanCopyUtils.copyProperties(p, mpo);
                return mpo;
            }).collect(Collectors.toList());
            mdo.setMethodIdMethodParamList(pLst);
        }
        return mdo;
    }

    public static FsmDefCompositeDO convert(FsmDefCompositeDTO dto){
        FsmDefCompositeDO fdo=new FsmDefCompositeDO();
        BeanCopyUtils.copyProperties(dto,fdo);
        if(CollectionUtils.isNotEmpty(dto.getFsmIdFsmConditionList())){
            List<FsmConditionDO> fcLst = dto.getFsmIdFsmConditionList().stream().map(f -> {
                FsmConditionDO fc = new FsmConditionDO();
                BeanCopyUtils.copyProperties(f, fc);
                return fc;
            }).collect(Collectors.toList());
            fdo.setFsmIdFsmConditionList(fcLst);
        }
        return fdo;
    }
}
