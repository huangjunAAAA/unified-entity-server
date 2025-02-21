package com.zjht.unified.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.unified.entity.FsmDef;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.vo.FsmDefVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class FsmDefWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static FsmDefWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new FsmDefWrapper();
    }

    public FsmDefVo entityVO(FsmDef fsmDef) {
        if (fsmDef == null)
            return null;
        FsmDefVo fsmDefVo = new FsmDefVo();
        BeanUtils.copyProperties(fsmDef, fsmDefVo);
        ArrayList<FsmDefVo> collect = new ArrayList<FsmDefVo>();
        collect.add(fsmDefVo);
        
		translateDict(collect);
        return fsmDefVo;
    }

    public List<FsmDefVo> entityVOList(List<FsmDef> list) {
        List<FsmDefVo> collect = list.stream().map(entity -> {
            FsmDefVo fsmDefVo = new FsmDefVo();
            BeanUtils.copyProperties(entity, fsmDefVo);
            return fsmDefVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<FsmDefVo> translateDict(List<FsmDefVo> list){
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
	
    public IPage<FsmDefVo> pageVO(IPage<FsmDef> source) {
        Page<FsmDefVo> page = new Page<FsmDefVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateFsmDef(FsmDef fsmDef){
      
    }

    public void initFsmDef(FsmDef fsmDef){
      
    }
}
