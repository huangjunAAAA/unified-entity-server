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


public class UiEventHandleCompositeWrapper{
    public void visitComposite(UiEventHandleCompositeDTO uiEventHandle, Consumer<BaseEntity> visitor){
        visitor.accept(uiEventHandle);
    }

    public static UiEventHandleCompositeWrapper build() {
        return new UiEventHandleCompositeWrapper();
    }

    public UiEventHandleCompositeVO entityVO(UiEventHandleCompositeDTO uiEventHandle){
        UiEventHandleWrapper wrapper=UiEventHandleWrapper.build();
        UiEventHandleCompositeVO vo= (UiEventHandleCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(uiEventHandle),new UiEventHandleCompositeVO(),null);

        return vo;
    }
    
    public List<UiEventHandleCompositeVO> entityVOList(List<UiEventHandleCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<UiEventHandleCompositeVO> pageVO(IPage<UiEventHandleCompositeDTO> source){
        Page<UiEventHandleCompositeVO> page = new Page<UiEventHandleCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}