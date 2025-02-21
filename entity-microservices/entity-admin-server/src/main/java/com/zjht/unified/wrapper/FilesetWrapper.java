package com.zjht.unified.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.unified.common.core.constants.Constants;
import com.google.gson.reflect.TypeToken;
import com.zjht.unified.common.core.json.GsonUtil;
import com.zjht.unified.common.core.util.SpringUtils;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.unified.entity.Fileset;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.vo.FilesetVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class FilesetWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static FilesetWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new FilesetWrapper();
    }

    public FilesetVo entityVO(Fileset fileset) {
        if (fileset == null)
            return null;
        FilesetVo filesetVo = new FilesetVo();
        BeanUtils.copyProperties(fileset, filesetVo);
        ArrayList<FilesetVo> collect = new ArrayList<FilesetVo>();
        collect.add(filesetVo);
        
		translateDict(collect);
        return filesetVo;
    }

    public List<FilesetVo> entityVOList(List<Fileset> list) {
        List<FilesetVo> collect = list.stream().map(entity -> {
            FilesetVo filesetVo = new FilesetVo();
            BeanUtils.copyProperties(entity, filesetVo);
            return filesetVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<FilesetVo> translateDict(List<FilesetVo> list){
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
	
    public IPage<FilesetVo> pageVO(IPage<Fileset> source) {
        Page<FilesetVo> page = new Page<FilesetVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateFileset(Fileset fileset){
      
    }

    public void initFileset(Fileset fileset){
      
    }
}
