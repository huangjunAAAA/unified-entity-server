package com.zjht.unified.admin.utils;

import com.wukong.core.util.BeanCopyUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.FsmDefCompositeDO;
import com.zjht.unified.domain.simple.ClsRelationDO;
import com.zjht.unified.domain.simple.FsmConditionDO;
import com.zjht.unified.domain.simple.MethodDefDO;
import com.zjht.unified.dto.ClazzDefCompositeDTO;
import com.zjht.unified.dto.FsmDefCompositeDTO;
import com.zjht.unified.utils.JsonUtilExt;
import org.apache.commons.collections4.CollectionUtils;

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
            cdo.setClazzIdMethodDefList(dto.getClazzIdMethodDefList().stream().map(m->{
                MethodDefDO mf=new MethodDefDO();
                BeanCopyUtils.copyProperties(m,mf);
                return mf;
            }).collect(Collectors.toList()));
        }
        return cdo;
    }

    public static FsmDefCompositeDO convert(FsmDefCompositeDTO dto){
        FsmDefCompositeDO fdo=new FsmDefCompositeDO();
        BeanCopyUtils.copyProperties(dto,fdo);
        if(CollectionUtils.isNotEmpty(dto.getFsmIdFsmConditionList())){
            dto.getFsmIdFsmConditionList().stream().map(f->{
                FsmConditionDO fc=new FsmConditionDO();
                BeanCopyUtils.copyProperties(f,fc);
                return fc;
            }).collect(Collectors.toList());
        }
        return fdo;
    }
}
