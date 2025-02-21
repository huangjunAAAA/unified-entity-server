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


public class ClsRelationCompositeWrapper{
    public void visitComposite(ClsRelationCompositeDTO clsRelation, Consumer<BaseEntity> visitor){
        visitor.accept(clsRelation);
    }

    public static ClsRelationCompositeWrapper build() {
        return new ClsRelationCompositeWrapper();
    }

    public ClsRelationCompositeVO entityVO(ClsRelationCompositeDTO clsRelation){
        ClsRelationWrapper wrapper=ClsRelationWrapper.build();
        ClsRelationCompositeVO vo= (ClsRelationCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(clsRelation),new ClsRelationCompositeVO(),null);

        return vo;
    }
    
    public List<ClsRelationCompositeVO> entityVOList(List<ClsRelationCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<ClsRelationCompositeVO> pageVO(IPage<ClsRelationCompositeDTO> source){
        Page<ClsRelationCompositeVO> page = new Page<ClsRelationCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}