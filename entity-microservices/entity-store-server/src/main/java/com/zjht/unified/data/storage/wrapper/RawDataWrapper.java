package com.zjht.unified.data.storage.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.data.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.unified.data.entity.RawData;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.data.storage.vo.RawDataVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class RawDataWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static RawDataWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new RawDataWrapper();
    }

    public RawDataVo entityVO(RawData rawData) {
        if (rawData == null)
            return null;
        RawDataVo rawDataVo = new RawDataVo();
        BeanUtils.copyProperties(rawData, rawDataVo);
        ArrayList<RawDataVo> collect = new ArrayList<RawDataVo>();
        collect.add(rawDataVo);
        
		translateDict(collect);
        return rawDataVo;
    }

    public List<RawDataVo> entityVOList(List<RawData> list) {
        List<RawDataVo> collect = list.stream().map(entity -> {
            RawDataVo rawDataVo = new RawDataVo();
            BeanUtils.copyProperties(entity, rawDataVo);
            return rawDataVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<RawDataVo> translateDict(List<RawDataVo> list){
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
	
    public IPage<RawDataVo> pageVO(IPage<RawData> source) {
        Page<RawDataVo> page = new Page<RawDataVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateRawData(RawData rawData){
      
    }

    public void initRawData(RawData rawData){
      
    }
}
