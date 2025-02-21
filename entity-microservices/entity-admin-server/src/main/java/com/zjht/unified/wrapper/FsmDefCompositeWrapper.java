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


public class FsmDefCompositeWrapper{
    public void visitComposite(FsmDefCompositeDTO fsmDef, Consumer<BaseEntity> visitor){
        visitor.accept(fsmDef);
      	if(CollectionUtils.isNotEmpty(fsmDef.getFsmIdFsmConditionList())){
            fsmDef.getFsmIdFsmConditionList().stream().forEach(t -> FsmConditionCompositeWrapper.build().visitComposite(t,visitor));
        }
    }

    public static FsmDefCompositeWrapper build() {
        return new FsmDefCompositeWrapper();
    }

    public FsmDefCompositeVO entityVO(FsmDefCompositeDTO fsmDef){
        FsmDefWrapper wrapper=FsmDefWrapper.build();
        FsmDefCompositeVO vo= (FsmDefCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(fsmDef),new FsmDefCompositeVO(),null);
      	if(CollectionUtils.isNotEmpty(fsmDef.getFsmIdFsmConditionList())){
      	    vo.setFsmIdFsmConditionList(fsmDef.getFsmIdFsmConditionList().stream()
                    .map(t -> FsmConditionCompositeWrapper.build().entityVO(t))
                    .collect(Collectors.toList()));
      	}

        return vo;
    }
    
    public List<FsmDefCompositeVO> entityVOList(List<FsmDefCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<FsmDefCompositeVO> pageVO(IPage<FsmDefCompositeDTO> source){
        Page<FsmDefCompositeVO> page = new Page<FsmDefCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}