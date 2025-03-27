package com.zjht.ui.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.ui.entity.UiPrj;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.ui.vo.UiPrjVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class UiPrjWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static UiPrjWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new UiPrjWrapper();
    }

    public UiPrjVo entityVO(UiPrj uiPrj) {
        if (uiPrj == null)
            return null;
        UiPrjVo uiPrjVo = new UiPrjVo();
        BeanUtils.copyProperties(uiPrj, uiPrjVo);
        ArrayList<UiPrjVo> collect = new ArrayList<UiPrjVo>();
        collect.add(uiPrjVo);
        
		translateDict(collect);
        return uiPrjVo;
    }

    public List<UiPrjVo> entityVOList(List<UiPrj> list) {
        List<UiPrjVo> collect = list.stream().map(entity -> {
            UiPrjVo uiPrjVo = new UiPrjVo();
            BeanUtils.copyProperties(entity, uiPrjVo);
            return uiPrjVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<UiPrjVo> translateDict(List<UiPrjVo> list){
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
	
    public IPage<UiPrjVo> pageVO(IPage<UiPrj> source) {
        Page<UiPrjVo> page = new Page<UiPrjVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateUiPrj(UiPrj uiPrj){
      
    }

    public void initUiPrj(UiPrj uiPrj){
      
    }
}
