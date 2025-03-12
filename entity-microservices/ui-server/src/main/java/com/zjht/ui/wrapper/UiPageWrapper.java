package com.zjht.ui.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.ui.entity.UiPage;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.ui.vo.UiPageVo;
import com.zjht.unified.common.core.util.SpringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class UiPageWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static UiPageWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new UiPageWrapper();
    }

    public UiPageVo entityVO(UiPage uiPage) {
        if (uiPage == null)
            return null;
        UiPageVo uiPageVo = new UiPageVo();
        BeanUtils.copyProperties(uiPage, uiPageVo);
        ArrayList<UiPageVo> collect = new ArrayList<UiPageVo>();
        collect.add(uiPageVo);
        
		translateDict(collect);
        return uiPageVo;
    }

    public List<UiPageVo> entityVOList(List<UiPage> list) {
        List<UiPageVo> collect = list.stream().map(entity -> {
            UiPageVo uiPageVo = new UiPageVo();
            BeanUtils.copyProperties(entity, uiPageVo);
            return uiPageVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<UiPageVo> translateDict(List<UiPageVo> list){
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
	
    public IPage<UiPageVo> pageVO(IPage<UiPage> source) {
        Page<UiPageVo> page = new Page<UiPageVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateUiPage(UiPage uiPage){
      
    }

    public void initUiPage(UiPage uiPage){
      
    }
}
