package com.zjht.unified.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.service.IConfigGraphService;

import com.zjht.unified.service.*;


import com.zjht.unified.common.core.util.ListExtractionUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import com.zjht.unified.entity.ConfigGraph;
import com.zjht.unified.service.IConfigGraphCompositeService;
import com.zjht.unified.dto.*;
import com.zjht.unified.wrapper.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service业务层处理
 *
 * @author wangy
 */
@Service
@ConditionalOnMissingBean(name = "configGraphCompositeServiceImplExt")
@Transactional(propagation = Propagation.REQUIRED)
public class ConfigGraphCompositeServiceImpl implements IConfigGraphCompositeService {

    @Autowired
    private IConfigGraphService configGraphService;
  
    @Autowired
    private IClazzDefCompositeService  clazzDefCompositeService;
    @Autowired
    private IFsmDefCompositeService  fsmDefCompositeService;
    @Autowired
    private ISentinelDefCompositeService  sentinelDefCompositeService;
    @Autowired
    private IViewDefCompositeService  viewDefCompositeService;
    public Long submit(ConfigGraphCompositeDTO entity) {
        if(entity==null)
            return null;
        ConfigGraphCompositeValidate.preValidate(entity);
        if(entity.getId()==null){
            configGraphService.save(entity);
        }else{
            configGraphService.updateById(entity);
            ConfigGraph newestEntity= configGraphService.getById(entity.getId());
            BeanUtils.copyProperties(newestEntity,entity);
        }
        boolean updateRequired=false;
        if(entity.getNodeIdClazzDefComposite()!=null){
            if(entity.getNodeId()!=null)
                entity.getNodeIdClazzDefComposite().setId(entity.getNodeId());
            clazzDefCompositeService.submit(entity.getNodeIdClazzDefComposite());
           entity.setNodeId(entity.getNodeIdClazzDefComposite().getId());
           updateRequired=true;
           entity.setNodeType(ConfigGraphCompositeDTO.NODEID_NODETYPE_CLAZZDEF_SK);
        }
        if(entity.getNodeIdFsmDefComposite()!=null){
            if(entity.getNodeId()!=null)
                entity.getNodeIdFsmDefComposite().setId(entity.getNodeId());
            fsmDefCompositeService.submit(entity.getNodeIdFsmDefComposite());
           entity.setNodeId(entity.getNodeIdFsmDefComposite().getId());
           updateRequired=true;
           entity.setNodeType(ConfigGraphCompositeDTO.NODEID_NODETYPE_FSMDEF_SK);
        }
        if(entity.getNodeIdSentinelDefComposite()!=null){
            if(entity.getNodeId()!=null)
                entity.getNodeIdSentinelDefComposite().setId(entity.getNodeId());
            sentinelDefCompositeService.submit(entity.getNodeIdSentinelDefComposite());
           entity.setNodeId(entity.getNodeIdSentinelDefComposite().getId());
           updateRequired=true;
           entity.setNodeType(ConfigGraphCompositeDTO.NODEID_NODETYPE_SENTINELDEF_SK);
        }
        if(entity.getNodeIdViewDefComposite()!=null){
            if(entity.getNodeId()!=null)
                entity.getNodeIdViewDefComposite().setId(entity.getNodeId());
            viewDefCompositeService.submit(entity.getNodeIdViewDefComposite());
           entity.setNodeId(entity.getNodeIdViewDefComposite().getId());
           updateRequired=true;
           entity.setNodeType(ConfigGraphCompositeDTO.NODEID_NODETYPE_VIEWDEF_SK);
        }
        if(ConfigGraphCompositeValidate.validateOnFlush(entity)||updateRequired)
          configGraphService.updateById(entity);
        return entity.getId();
    }

    public void removeById(Long id) {
        ConfigGraphCompositeDTO oldEntity = selectById(id);
        if(oldEntity!=null){
            if(oldEntity.getNodeIdClazzDefComposite()!=null)
                clazzDefCompositeService.removeById(oldEntity.getNodeIdClazzDefComposite().getId());
            if(oldEntity.getNodeIdFsmDefComposite()!=null)
                fsmDefCompositeService.removeById(oldEntity.getNodeIdFsmDefComposite().getId());
            if(oldEntity.getNodeIdSentinelDefComposite()!=null)
                sentinelDefCompositeService.removeById(oldEntity.getNodeIdSentinelDefComposite().getId());
            if(oldEntity.getNodeIdViewDefComposite()!=null)
                viewDefCompositeService.removeById(oldEntity.getNodeIdViewDefComposite().getId());
        }
      configGraphService.removeById(id);
    }

    @Override
    public void batchRemove(List<Long> entityIdLst) {
        if(CollectionUtils.isNotEmpty(entityIdLst)){
            entityIdLst.stream().filter(t->t!=null).forEach(this::removeById);
        }
    }

    @Override
    public void batchSubmit(List<ConfigGraphCompositeDTO> entityList) {
        if(CollectionUtils.isNotEmpty(entityList)){
            entityList.stream().filter(t->t!=null).forEach(this::submit);
        }
    }

    public ConfigGraphCompositeDTO selectById(Long id) {
        ConfigGraph configGraph = configGraphService.getById(id);
        if(configGraph==null)
          return null;
        ConfigGraphCompositeDTO configGraphDTO=new ConfigGraphCompositeDTO();
        BeanUtils.copyProperties(configGraph,configGraphDTO);
        if ( configGraph.getNodeType().equals(ConfigGraphCompositeDTO.NODEID_NODETYPE_CLAZZDEF_SK ))
        configGraphDTO.setNodeIdClazzDefComposite(clazzDefCompositeService.selectById(configGraph.getNodeId()));
        if ( configGraph.getNodeType().equals(ConfigGraphCompositeDTO.NODEID_NODETYPE_FSMDEF_SK ))
        configGraphDTO.setNodeIdFsmDefComposite(fsmDefCompositeService.selectById(configGraph.getNodeId()));
        if ( configGraph.getNodeType().equals(ConfigGraphCompositeDTO.NODEID_NODETYPE_SENTINELDEF_SK ))
        configGraphDTO.setNodeIdSentinelDefComposite(sentinelDefCompositeService.selectById(configGraph.getNodeId()));
        if ( configGraph.getNodeType().equals(ConfigGraphCompositeDTO.NODEID_NODETYPE_VIEWDEF_SK ))
        configGraphDTO.setNodeIdViewDefComposite(viewDefCompositeService.selectById(configGraph.getNodeId()));
        return configGraphDTO;
    }

    @Override
    public List<ConfigGraphCompositeDTO> selectList(ConfigGraphCompositeDTO param) {
        List<ConfigGraph> configGraphList = configGraphService.list(new QueryWrapper<>(param));
        return configGraphList.stream().map(t->selectById(t.getId())).collect(Collectors.toList());
    }

    @Override
    public ConfigGraphCompositeDTO selectOne(ConfigGraphCompositeDTO param) {
        ConfigGraph configGraph = configGraphService.getOne(new QueryWrapper<>(param));
        if(configGraph!=null){
            return selectById(configGraph.getId());
        }
        return null;
    }
  
  	public ConfigGraphCompositeDTO deepCopyById(Long id){
    		if(id==null)
          return null;
      	ConfigGraphCompositeDTO entity=selectById(id);
        if(entity==null)
            return null;
      	return deepCopy(entity);
  	}
    public ConfigGraphCompositeDTO deepCopy(ConfigGraphCompositeDTO entity){      	
        ConfigGraphCompositeValidate.preCopy(entity);
        entity.setOriginalId(entity.getId());
      	entity.setId(null);
        configGraphService.save(entity);
      if(entity.getNodeIdClazzDefComposite()!=null)	{
        entity.setNodeIdClazzDefComposite(clazzDefCompositeService.deepCopy(entity.getNodeIdClazzDefComposite()));
        entity.setNodeId(entity.getNodeIdClazzDefComposite().getId());      
      }
      if(entity.getNodeIdFsmDefComposite()!=null)	{
        entity.setNodeIdFsmDefComposite(fsmDefCompositeService.deepCopy(entity.getNodeIdFsmDefComposite()));
        entity.setNodeId(entity.getNodeIdFsmDefComposite().getId());      
      }
      if(entity.getNodeIdSentinelDefComposite()!=null)	{
        entity.setNodeIdSentinelDefComposite(sentinelDefCompositeService.deepCopy(entity.getNodeIdSentinelDefComposite()));
        entity.setNodeId(entity.getNodeIdSentinelDefComposite().getId());      
      }
      if(entity.getNodeIdViewDefComposite()!=null)	{
        entity.setNodeIdViewDefComposite(viewDefCompositeService.deepCopy(entity.getNodeIdViewDefComposite()));
        entity.setNodeId(entity.getNodeIdViewDefComposite().getId());      
      }
        configGraphService.updateById(entity);
        if(ConfigGraphCompositeValidate.validateOnCopy(entity))
          configGraphService.updateById(entity);
        return entity;
    }
}