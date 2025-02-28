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


public class MethodParamCompositeWrapper{
    public void visitComposite(MethodParamCompositeDTO methodParam, Consumer<BaseEntity> visitor){
        visitor.accept(methodParam);
    }

    public static MethodParamCompositeWrapper build() {
        return new MethodParamCompositeWrapper();
    }

    public MethodParamCompositeVO entityVO(MethodParamCompositeDTO methodParam){
        MethodParamWrapper wrapper=MethodParamWrapper.build();
        MethodParamCompositeVO vo= (MethodParamCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(methodParam),new MethodParamCompositeVO(),null);

        return vo;
    }
    
    public List<MethodParamCompositeVO> entityVOList(List<MethodParamCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<MethodParamCompositeVO> pageVO(IPage<MethodParamCompositeDTO> source){
        Page<MethodParamCompositeVO> page = new Page<MethodParamCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}