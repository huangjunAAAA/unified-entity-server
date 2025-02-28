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


public class ConfigGraphCompositeWrapper{
    public void visitComposite(ConfigGraphCompositeDTO configGraph, Consumer<BaseEntity> visitor){
        visitor.accept(configGraph);
      	if(configGraph.getNodeIdClazzDefComposite()!=null)
            visitor.accept(configGraph.getNodeIdClazzDefComposite());
      	if(configGraph.getParentIdConfigGraphComposite()!=null)
            visitor.accept(configGraph.getParentIdConfigGraphComposite());
      	if(configGraph.getNodeIdFsmDefComposite()!=null)
            visitor.accept(configGraph.getNodeIdFsmDefComposite());
      	if(configGraph.getNodeIdSentinelDefComposite()!=null)
            visitor.accept(configGraph.getNodeIdSentinelDefComposite());
      	if(configGraph.getNodeIdViewDefComposite()!=null)
            visitor.accept(configGraph.getNodeIdViewDefComposite());
    }

    public static ConfigGraphCompositeWrapper build() {
        return new ConfigGraphCompositeWrapper();
    }

    public ConfigGraphCompositeVO entityVO(ConfigGraphCompositeDTO configGraph){
        ConfigGraphWrapper wrapper=ConfigGraphWrapper.build();
        ConfigGraphCompositeVO vo= (ConfigGraphCompositeVO)BeanCopyUtils.shallowCopy(wrapper.entityVO(configGraph),new ConfigGraphCompositeVO(),null);
      	if(configGraph.getNodeIdClazzDefComposite()!=null)
           vo.setNodeIdClazzDefComposite(ClazzDefCompositeWrapper.build().entityVO(configGraph.getNodeIdClazzDefComposite()));
      	if(configGraph.getParentIdConfigGraphComposite()!=null)
           vo.setParentIdConfigGraphComposite(ConfigGraphCompositeWrapper.build().entityVO(configGraph.getParentIdConfigGraphComposite()));
      	if(configGraph.getNodeIdFsmDefComposite()!=null)
           vo.setNodeIdFsmDefComposite(FsmDefCompositeWrapper.build().entityVO(configGraph.getNodeIdFsmDefComposite()));
      	if(configGraph.getNodeIdSentinelDefComposite()!=null)
           vo.setNodeIdSentinelDefComposite(SentinelDefCompositeWrapper.build().entityVO(configGraph.getNodeIdSentinelDefComposite()));
      	if(configGraph.getNodeIdViewDefComposite()!=null)
           vo.setNodeIdViewDefComposite(ViewDefCompositeWrapper.build().entityVO(configGraph.getNodeIdViewDefComposite()));

        return vo;
    }
    
    public List<ConfigGraphCompositeVO> entityVOList(List<ConfigGraphCompositeDTO> list){
        return list.stream().map(t->entityVO(t)).collect(Collectors.toList());
    }

    public IPage<ConfigGraphCompositeVO> pageVO(IPage<ConfigGraphCompositeDTO> source){
        Page<ConfigGraphCompositeVO> page = new Page<ConfigGraphCompositeVO>();
        BeanUtils.copyProperties(source, page, "records");
        page.setRecords(entityVOList(source.getRecords()));
        return page;
    }
}