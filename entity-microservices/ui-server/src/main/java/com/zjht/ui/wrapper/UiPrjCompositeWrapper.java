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


public class UiPrjCompositeWrapper{
    public void visitComposite(UiPrjCompositeDTO uiPrj, Consumer<BaseEntity> visitor){
        visitor.accept(uiPrj);
      	if(CollectionUtils.isNotEmpty(uiPrj.getRprjIdUiPageList())){
            uiPrj.getRprjIdUiPageList().stream().forEach(t -> UiPageCompositeWrapper.build().visitComposite(t,visitor));
        }
      	if(uiPrj.getGitIdGitStoreComposite()!=null)
            visitor.accept(uiPrj.getGitIdGitStoreComposite());
    }

    public static UiPrjCompositeWrapper build() {
        return new UiPrjCompositeWrapper();
    }

    public UiPrjCompositeVO entityVO(UiPrjCompositeDTO uiPrj){
        UiPrjWrapper wrapper=UiPrjWrapper.build();
        UiPrjCompositeVO vo= (UiPrjCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(uiPrj),new UiPrjCompositeVO(),null);
      	if(CollectionUtils.isNotEmpty(uiPrj.getRprjIdUiPageList())){
      	    vo.setRprjIdUiPageList(uiPrj.getRprjIdUiPageList().stream()
                    .map(t -> UiPageCompositeWrapper.build().entityVO(t))
                    .collect(Collectors.toList()));
      	}
      	if(uiPrj.getGitIdGitStoreComposite()!=null)
           vo.setGitIdGitStoreComposite(GitStoreCompositeWrapper.build().entityVO(uiPrj.getGitIdGitStoreComposite()));

        return vo;
    }
    
    public List<UiPrjCompositeVO> entityVOList(List<UiPrjCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<UiPrjCompositeVO> pageVO(IPage<UiPrjCompositeDTO> source){
        Page<UiPrjCompositeVO> page = new Page<UiPrjCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}