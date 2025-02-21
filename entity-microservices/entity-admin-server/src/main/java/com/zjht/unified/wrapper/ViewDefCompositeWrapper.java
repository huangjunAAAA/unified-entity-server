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


public class ViewDefCompositeWrapper{
    public void visitComposite(ViewDefCompositeDTO viewDef, Consumer<BaseEntity> visitor){
        visitor.accept(viewDef);
    }

    public static ViewDefCompositeWrapper build() {
        return new ViewDefCompositeWrapper();
    }

    public ViewDefCompositeVO entityVO(ViewDefCompositeDTO viewDef){
        ViewDefWrapper wrapper=ViewDefWrapper.build();
        ViewDefCompositeVO vo= (ViewDefCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(viewDef),new ViewDefCompositeVO(),null);

        return vo;
    }
    
    public List<ViewDefCompositeVO> entityVOList(List<ViewDefCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<ViewDefCompositeVO> pageVO(IPage<ViewDefCompositeDTO> source){
        Page<ViewDefCompositeVO> page = new Page<ViewDefCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}