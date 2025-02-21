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


public class SentinelDefCompositeWrapper{
    public void visitComposite(SentinelDefCompositeDTO sentinelDef, Consumer<BaseEntity> visitor){
        visitor.accept(sentinelDef);
    }

    public static SentinelDefCompositeWrapper build() {
        return new SentinelDefCompositeWrapper();
    }

    public SentinelDefCompositeVO entityVO(SentinelDefCompositeDTO sentinelDef){
        SentinelDefWrapper wrapper=SentinelDefWrapper.build();
        SentinelDefCompositeVO vo= (SentinelDefCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(sentinelDef),new SentinelDefCompositeVO(),null);

        return vo;
    }
    
    public List<SentinelDefCompositeVO> entityVOList(List<SentinelDefCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<SentinelDefCompositeVO> pageVO(IPage<SentinelDefCompositeDTO> source){
        Page<SentinelDefCompositeVO> page = new Page<SentinelDefCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}