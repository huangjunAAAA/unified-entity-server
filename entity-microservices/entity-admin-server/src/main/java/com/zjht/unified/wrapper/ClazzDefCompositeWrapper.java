package com.zjht.unified.wrapper;

import com.wukong.core.mp.base.BaseEntity;
import com.wukong.core.util.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.*;
import java.util.stream.Collectors;
import com.zjht.unified.dto.*;
import com.zjht.unified.vo.*;
import java.util.function.Function;


public class ClazzDefCompositeWrapper{
    public void visitComposite(ClazzDefCompositeDTO clazzDef, Consumer<BaseEntity> visitor){
        visitor.accept(clazzDef);
      	if(CollectionUtils.isNotEmpty(clazzDef.getClazzIdFieldDefList())){
            clazzDef.getClazzIdFieldDefList().stream().forEach(t -> FieldDefCompositeWrapper.build().visitComposite(t,visitor));
        }
      	if(CollectionUtils.isNotEmpty(clazzDef.getClazzIdMethodDefList())){
            clazzDef.getClazzIdMethodDefList().stream().forEach(t -> MethodDefCompositeWrapper.build().visitComposite(t,visitor));
        }
    }

    public static ClazzDefCompositeWrapper build() {
        return new ClazzDefCompositeWrapper();
    }

    public ClazzDefCompositeVO entityVO(ClazzDefCompositeDTO clazzDef){
        ClazzDefWrapper wrapper=ClazzDefWrapper.build();
        ClazzDefCompositeVO vo= (ClazzDefCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(clazzDef),new ClazzDefCompositeVO(),null);
      	if(CollectionUtils.isNotEmpty(clazzDef.getClazzIdFieldDefList())){
      	    vo.setClazzIdFieldDefList(clazzDef.getClazzIdFieldDefList().stream()
                    .map(t -> FieldDefCompositeWrapper.build().entityVO(t))
                    .collect(Collectors.toList()));
      	}
      	if(CollectionUtils.isNotEmpty(clazzDef.getClazzIdMethodDefList())){
      	    vo.setClazzIdMethodDefList(clazzDef.getClazzIdMethodDefList().stream()
                    .map(t -> MethodDefCompositeWrapper.build().entityVO(t))
                    .collect(Collectors.toList()));
      	}

        return vo;
    }
    
    public List<ClazzDefCompositeVO> entityVOList(List<ClazzDefCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<ClazzDefCompositeVO> pageVO(IPage<ClazzDefCompositeDTO> source){
        Page<ClazzDefCompositeVO> page = new Page<ClazzDefCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}