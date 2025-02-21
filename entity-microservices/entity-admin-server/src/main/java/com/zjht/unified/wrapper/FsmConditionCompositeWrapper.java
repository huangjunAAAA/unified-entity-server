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


public class FsmConditionCompositeWrapper{
    public void visitComposite(FsmConditionCompositeDTO fsmCondition, Consumer<BaseEntity> visitor){
        visitor.accept(fsmCondition);
    }

    public static FsmConditionCompositeWrapper build() {
        return new FsmConditionCompositeWrapper();
    }

    public FsmConditionCompositeVO entityVO(FsmConditionCompositeDTO fsmCondition){
        FsmConditionWrapper wrapper=FsmConditionWrapper.build();
        FsmConditionCompositeVO vo= (FsmConditionCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(fsmCondition),new FsmConditionCompositeVO(),null);

        return vo;
    }
    
    public List<FsmConditionCompositeVO> entityVOList(List<FsmConditionCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<FsmConditionCompositeVO> pageVO(IPage<FsmConditionCompositeDTO> source){
        Page<FsmConditionCompositeVO> page = new Page<FsmConditionCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}