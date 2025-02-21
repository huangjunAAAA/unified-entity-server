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


public class FieldDefCompositeWrapper{
    public void visitComposite(FieldDefCompositeDTO fieldDef, Consumer<BaseEntity> visitor){
        visitor.accept(fieldDef);
    }

    public static FieldDefCompositeWrapper build() {
        return new FieldDefCompositeWrapper();
    }

    public FieldDefCompositeVO entityVO(FieldDefCompositeDTO fieldDef){
        FieldDefWrapper wrapper=FieldDefWrapper.build();
        FieldDefCompositeVO vo= (FieldDefCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(fieldDef),new FieldDefCompositeVO(),null);

        return vo;
    }
    
    public List<FieldDefCompositeVO> entityVOList(List<FieldDefCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<FieldDefCompositeVO> pageVO(IPage<FieldDefCompositeDTO> source){
        Page<FieldDefCompositeVO> page = new Page<FieldDefCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}