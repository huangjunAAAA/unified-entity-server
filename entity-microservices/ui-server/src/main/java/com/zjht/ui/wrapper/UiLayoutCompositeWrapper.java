package com.zjht.ui.wrapper;

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
import com.zjht.ui.dto.*;
import com.zjht.ui.vo.*;
import java.util.function.Function;


public class UiLayoutCompositeWrapper{
    public void visitComposite(UiLayoutCompositeDTO uiLayout, Consumer<BaseEntity> visitor){
        visitor.accept(uiLayout);
    }

    public static UiLayoutCompositeWrapper build() {
        return new UiLayoutCompositeWrapper();
    }

    public UiLayoutCompositeVO entityVO(UiLayoutCompositeDTO uiLayout){
        UiLayoutWrapper wrapper=UiLayoutWrapper.build();
        UiLayoutCompositeVO vo= (UiLayoutCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(uiLayout),new UiLayoutCompositeVO(),null);

        return vo;
    }
    
    public List<UiLayoutCompositeVO> entityVOList(List<UiLayoutCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<UiLayoutCompositeVO> pageVO(IPage<UiLayoutCompositeDTO> source){
        Page<UiLayoutCompositeVO> page = new Page<UiLayoutCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}