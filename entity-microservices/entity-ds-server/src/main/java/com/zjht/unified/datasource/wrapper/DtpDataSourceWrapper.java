package com.zjht.unified.datasource.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zjht.unified.datasource.dto.SystemSpec;
import com.zjht.unified.datasource.entity.DtpDataSource;
import com.zjht.unified.datasource.service.SwaggerApiService;
import com.zjht.unified.datasource.vo.DtpDataSourceVo;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.unified.common.core.util.SpringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class DtpDataSourceWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static DtpDataSourceWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new DtpDataSourceWrapper();
    }

    public DtpDataSourceVo entityVO(DtpDataSource dtpDataSource) {
        if (dtpDataSource == null)
            return null;
        DtpDataSourceVo dtpDataSourceVo = new DtpDataSourceVo();
        BeanUtils.copyProperties(dtpDataSource, dtpDataSourceVo);
        ArrayList<DtpDataSourceVo> collect = new ArrayList<DtpDataSourceVo>();
        collect.add(dtpDataSourceVo);
        
		translateDict(collect);
        return dtpDataSourceVo;
    }

    public List<DtpDataSourceVo> entityVOList(List<DtpDataSource> list) {
        List<DtpDataSourceVo> collect = list.stream().map(entity -> {
            DtpDataSourceVo dtpDataSourceVo = new DtpDataSourceVo();
            BeanUtils.copyProperties(entity, dtpDataSourceVo);
            return dtpDataSourceVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<DtpDataSourceVo> translateDict(List<DtpDataSourceVo> list){
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
	
    public IPage<DtpDataSourceVo> pageVO(IPage<DtpDataSource> source) {
        Page<DtpDataSourceVo> page = new Page<DtpDataSourceVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateDtpDataSource(DtpDataSource dtpDataSource){
        SwaggerApiService apiService=SpringUtils.getBean(SwaggerApiService.class);
        try {
            SystemSpec spec = apiService.convert(dtpDataSource);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public void initDtpDataSource(DtpDataSource dtpDataSource){
      
    }
}
