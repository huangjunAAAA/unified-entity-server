package com.zjht.unified.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.unified.entity.FsmCondition;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.vo.FsmConditionVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class FsmConditionWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static FsmConditionWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new FsmConditionWrapper();
    }

    public FsmConditionVo entityVO(FsmCondition fsmCondition) {
        if (fsmCondition == null)
            return null;
        FsmConditionVo fsmConditionVo = new FsmConditionVo();
        BeanUtils.copyProperties(fsmCondition, fsmConditionVo);
        ArrayList<FsmConditionVo> collect = new ArrayList<FsmConditionVo>();
        collect.add(fsmConditionVo);
        
		translateDict(collect);
        return fsmConditionVo;
    }

    public List<FsmConditionVo> entityVOList(List<FsmCondition> list) {
        List<FsmConditionVo> collect = list.stream().map(entity -> {
            FsmConditionVo fsmConditionVo = new FsmConditionVo();
            BeanUtils.copyProperties(entity, fsmConditionVo);
            return fsmConditionVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<FsmConditionVo> translateDict(List<FsmConditionVo> list){
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
	
    public IPage<FsmConditionVo> pageVO(IPage<FsmCondition> source) {
        Page<FsmConditionVo> page = new Page<FsmConditionVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateFsmCondition(FsmCondition fsmCondition){
      
    }

    public void initFsmCondition(FsmCondition fsmCondition){
      
    }
}
