package com.zjht.unified.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.unified.entity.ConfigGraph;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.vo.ConfigGraphVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class ConfigGraphWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static ConfigGraphWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new ConfigGraphWrapper();
    }

    public ConfigGraphVo entityVO(ConfigGraph configGraph) {
        if (configGraph == null)
            return null;
        ConfigGraphVo configGraphVo = new ConfigGraphVo();
        BeanUtils.copyProperties(configGraph, configGraphVo);
        ArrayList<ConfigGraphVo> collect = new ArrayList<ConfigGraphVo>();
        collect.add(configGraphVo);
        
		translateDict(collect);
        return configGraphVo;
    }

    public List<ConfigGraphVo> entityVOList(List<ConfigGraph> list) {
        List<ConfigGraphVo> collect = list.stream().map(entity -> {
            ConfigGraphVo configGraphVo = new ConfigGraphVo();
            BeanUtils.copyProperties(entity, configGraphVo);
            return configGraphVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<ConfigGraphVo> translateDict(List<ConfigGraphVo> list){
        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(dicttypeLst))
            return list;

        Map<String,List<SysDictDataDO>> result = remoteDictService.dictTypes(dicttypeLst).getData();
        if(MapUtils.isEmpty(result)){
            return list;
        }
        HashMap<String,Map<String,String>> map = new HashMap<>();
        for(Map.Entry<String,List<SysDictDataDO>> entry:result.entrySet()){
            Map<String,String> m = new HashMap<String,String>();
            for(SysDictDataDO data:entry.getValue())
                m.put(data.getDictValue(),data.getDictName());
            map.put(entry.getKey(),m);
        }
        list.stream().forEach(vo -> {
			Map<String, String> cache = null;
        });
        return list;
    }
	
    public IPage<ConfigGraphVo> pageVO(IPage<ConfigGraph> source) {
        Page<ConfigGraphVo> page = new Page<ConfigGraphVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateConfigGraph(ConfigGraph configGraph){
      
    }

    public void initConfigGraph(ConfigGraph configGraph){
      
    }
}
