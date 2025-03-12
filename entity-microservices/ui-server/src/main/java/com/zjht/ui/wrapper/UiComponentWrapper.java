package com.zjht.ui.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.ui.entity.UiComponent;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.ui.vo.UiComponentVo;
import com.zjht.unified.common.core.util.SpringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class UiComponentWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static UiComponentWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new UiComponentWrapper();
    }

    public UiComponentVo entityVO(UiComponent uiComponent) {
        if (uiComponent == null)
            return null;
        UiComponentVo uiComponentVo = new UiComponentVo();
        BeanUtils.copyProperties(uiComponent, uiComponentVo);
        ArrayList<UiComponentVo> collect = new ArrayList<UiComponentVo>();
        collect.add(uiComponentVo);
        
		translateDict(collect);
        return uiComponentVo;
    }

    public List<UiComponentVo> entityVOList(List<UiComponent> list) {
        List<UiComponentVo> collect = list.stream().map(entity -> {
            UiComponentVo uiComponentVo = new UiComponentVo();
            BeanUtils.copyProperties(entity, uiComponentVo);
            return uiComponentVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<UiComponentVo> translateDict(List<UiComponentVo> list){
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
	
    public IPage<UiComponentVo> pageVO(IPage<UiComponent> source) {
        Page<UiComponentVo> page = new Page<UiComponentVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateUiComponent(UiComponent uiComponent){
      
    }

    public void initUiComponent(UiComponent uiComponent){
      
    }
}
