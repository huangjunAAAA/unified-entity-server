package com.zjht.unified.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.unified.entity.PrjDep;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.vo.PrjDepVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class PrjDepWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static PrjDepWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new PrjDepWrapper();
    }

    public PrjDepVo entityVO(PrjDep prjDep) {
        if (prjDep == null)
            return null;
        PrjDepVo prjDepVo = new PrjDepVo();
        BeanUtils.copyProperties(prjDep, prjDepVo);
        ArrayList<PrjDepVo> collect = new ArrayList<PrjDepVo>();
        collect.add(prjDepVo);
        
		translateDict(collect);
        return prjDepVo;
    }

    public List<PrjDepVo> entityVOList(List<PrjDep> list) {
        List<PrjDepVo> collect = list.stream().map(entity -> {
            PrjDepVo prjDepVo = new PrjDepVo();
            BeanUtils.copyProperties(entity, prjDepVo);
            return prjDepVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<PrjDepVo> translateDict(List<PrjDepVo> list){
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
	
    public IPage<PrjDepVo> pageVO(IPage<PrjDep> source) {
        Page<PrjDepVo> page = new Page<PrjDepVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validatePrjDep(PrjDep prjDep){
      
    }

    public void initPrjDep(PrjDep prjDep){
      
    }
}
