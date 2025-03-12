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


public class UiComponentCompositeWrapper{
    public void visitComposite(UiComponentCompositeDTO uiComponent, Consumer<BaseEntity> visitor){
        visitor.accept(uiComponent);
      	if(CollectionUtils.isNotEmpty(uiComponent.getBelongtoIdFilesetList())){
            uiComponent.getBelongtoIdFilesetList().stream().forEach(t -> FilesetCompositeWrapper.build().visitComposite(t,visitor));
        }
    }

    public static UiComponentCompositeWrapper build() {
        return new UiComponentCompositeWrapper();
    }

    public UiComponentCompositeVO entityVO(UiComponentCompositeDTO uiComponent){
        UiComponentWrapper wrapper=UiComponentWrapper.build();
        UiComponentCompositeVO vo= (UiComponentCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(uiComponent),new UiComponentCompositeVO(),null);
      	if(CollectionUtils.isNotEmpty(uiComponent.getBelongtoIdFilesetList())){
      	    vo.setBelongtoIdFilesetList(uiComponent.getBelongtoIdFilesetList().stream()
                    .map(t -> FilesetCompositeWrapper.build().entityVO(t))
                    .collect(Collectors.toList()));
      	}

        return vo;
    }
    
    public List<UiComponentCompositeVO> entityVOList(List<UiComponentCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<UiComponentCompositeVO> pageVO(IPage<UiComponentCompositeDTO> source){
        Page<UiComponentCompositeVO> page = new Page<UiComponentCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}