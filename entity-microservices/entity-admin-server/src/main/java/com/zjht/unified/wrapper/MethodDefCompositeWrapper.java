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


public class MethodDefCompositeWrapper{
    public void visitComposite(MethodDefCompositeDTO methodDef, Consumer<BaseEntity> visitor){
        visitor.accept(methodDef);
      	if(CollectionUtils.isNotEmpty(methodDef.getMethodIdMethodParamList())){
            methodDef.getMethodIdMethodParamList().stream().forEach(t -> MethodParamCompositeWrapper.build().visitComposite(t,visitor));
        }
    }

    public static MethodDefCompositeWrapper build() {
        return new MethodDefCompositeWrapper();
    }

    public MethodDefCompositeVO entityVO(MethodDefCompositeDTO methodDef){
        MethodDefWrapper wrapper=MethodDefWrapper.build();
        MethodDefCompositeVO vo= (MethodDefCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(methodDef),new MethodDefCompositeVO(),null);
      	if(CollectionUtils.isNotEmpty(methodDef.getMethodIdMethodParamList())){
      	    vo.setMethodIdMethodParamList(methodDef.getMethodIdMethodParamList().stream()
                    .map(t -> MethodParamCompositeWrapper.build().entityVO(t))
                    .collect(Collectors.toList()));
      	}

        return vo;
    }
    
    public List<MethodDefCompositeVO> entityVOList(List<MethodDefCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<MethodDefCompositeVO> pageVO(IPage<MethodDefCompositeDTO> source){
        Page<MethodDefCompositeVO> page = new Page<MethodDefCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}