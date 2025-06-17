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


public class UiPageCompositeWrapper{
    public void visitComposite(UiPageCompositeDTO uiPage, Consumer<BaseEntity> visitor){
        visitor.accept(uiPage);
      	if(CollectionUtils.isNotEmpty(uiPage.getPageIdUiComponentList())){
            uiPage.getPageIdUiComponentList().stream().forEach(t -> UiComponentCompositeWrapper.build().visitComposite(t,visitor));
        }
    }

    public static UiPageCompositeWrapper build() {
        return new UiPageCompositeWrapper();
    }

    public UiPageCompositeVO entityVO(UiPageCompositeDTO uiPage){
        UiPageWrapper wrapper=UiPageWrapper.build();
        UiPageCompositeVO vo= (UiPageCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(uiPage),new UiPageCompositeVO(),null);
      	if(CollectionUtils.isNotEmpty(uiPage.getPageIdUiComponentList())){
      	    vo.setPageIdUiComponentList(uiPage.getPageIdUiComponentList().stream()
                    .map(t -> UiComponentCompositeWrapper.build().entityVO(t))
                    .collect(Collectors.toList()));
      	}

        return vo;
    }
    
    public List<UiPageCompositeVO> entityVOList(List<UiPageCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<UiPageCompositeVO> pageVO(IPage<UiPageCompositeDTO> source){
        Page<UiPageCompositeVO> page = new Page<UiPageCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}