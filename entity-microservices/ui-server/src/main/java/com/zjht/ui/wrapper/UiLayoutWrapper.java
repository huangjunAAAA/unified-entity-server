package com.zjht.ui.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.ui.entity.UiLayout;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.ui.vo.UiLayoutVo;
import com.zjht.unified.common.core.util.SpringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class UiLayoutWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static UiLayoutWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new UiLayoutWrapper();
    }

    public UiLayoutVo entityVO(UiLayout uiLayout) {
        if (uiLayout == null)
            return null;
        UiLayoutVo uiLayoutVo = new UiLayoutVo();
        BeanUtils.copyProperties(uiLayout, uiLayoutVo);
        ArrayList<UiLayoutVo> collect = new ArrayList<UiLayoutVo>();
        collect.add(uiLayoutVo);
        
		translateDict(collect);
        return uiLayoutVo;
    }

    public List<UiLayoutVo> entityVOList(List<UiLayout> list) {
        List<UiLayoutVo> collect = list.stream().map(entity -> {
            UiLayoutVo uiLayoutVo = new UiLayoutVo();
            BeanUtils.copyProperties(entity, uiLayoutVo);
            return uiLayoutVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<UiLayoutVo> translateDict(List<UiLayoutVo> list){
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
	
    public IPage<UiLayoutVo> pageVO(IPage<UiLayout> source) {
        Page<UiLayoutVo> page = new Page<UiLayoutVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateUiLayout(UiLayout uiLayout){
      
    }

    public void initUiLayout(UiLayout uiLayout){
      
    }
}
