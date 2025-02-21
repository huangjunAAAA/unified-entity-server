package com.zjht.unified.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.unified.entity.UePrj;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.vo.UePrjVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class UePrjWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static UePrjWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new UePrjWrapper();
    }

    public UePrjVo entityVO(UePrj uePrj) {
        if (uePrj == null)
            return null;
        UePrjVo uePrjVo = new UePrjVo();
        BeanUtils.copyProperties(uePrj, uePrjVo);
        ArrayList<UePrjVo> collect = new ArrayList<UePrjVo>();
        collect.add(uePrjVo);
        
		translateDict(collect);
        return uePrjVo;
    }

    public List<UePrjVo> entityVOList(List<UePrj> list) {
        List<UePrjVo> collect = list.stream().map(entity -> {
            UePrjVo uePrjVo = new UePrjVo();
            BeanUtils.copyProperties(entity, uePrjVo);
            return uePrjVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<UePrjVo> translateDict(List<UePrjVo> list){
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
	
    public IPage<UePrjVo> pageVO(IPage<UePrj> source) {
        Page<UePrjVo> page = new Page<UePrjVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateUePrj(UePrj uePrj){
      
    }

    public void initUePrj(UePrj uePrj){
      
    }
}
