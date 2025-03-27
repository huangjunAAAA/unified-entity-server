package com.zjht.ui.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.ui.entity.UiEventHandle;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.ui.vo.UiEventHandleVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class UiEventHandleWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static UiEventHandleWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new UiEventHandleWrapper();
    }

    public UiEventHandleVo entityVO(UiEventHandle uiEventHandle) {
        if (uiEventHandle == null)
            return null;
        UiEventHandleVo uiEventHandleVo = new UiEventHandleVo();
        BeanUtils.copyProperties(uiEventHandle, uiEventHandleVo);
        ArrayList<UiEventHandleVo> collect = new ArrayList<UiEventHandleVo>();
        collect.add(uiEventHandleVo);
        
		translateDict(collect);
        return uiEventHandleVo;
    }

    public List<UiEventHandleVo> entityVOList(List<UiEventHandle> list) {
        List<UiEventHandleVo> collect = list.stream().map(entity -> {
            UiEventHandleVo uiEventHandleVo = new UiEventHandleVo();
            BeanUtils.copyProperties(entity, uiEventHandleVo);
            return uiEventHandleVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<UiEventHandleVo> translateDict(List<UiEventHandleVo> list){
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
	
    public IPage<UiEventHandleVo> pageVO(IPage<UiEventHandle> source) {
        Page<UiEventHandleVo> page = new Page<UiEventHandleVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateUiEventHandle(UiEventHandle uiEventHandle){
      
    }

    public void initUiEventHandle(UiEventHandle uiEventHandle){
      
    }
}
