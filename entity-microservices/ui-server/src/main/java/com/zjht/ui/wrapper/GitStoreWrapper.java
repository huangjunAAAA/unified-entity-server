package com.zjht.ui.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjht.infrastructure.system.client.RemoteDictService;
import com.zjht.ui.entity.GitStore;
import com.zjht.infrastructure.system.entity.SysDictDataDO;
import com.zjht.ui.vo.GitStoreVo;
import com.zjht.unified.common.core.util.SpringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


public class GitStoreWrapper {


    protected static RestTemplate restTemplate;
	
	protected static RemoteDictService remoteDictService;
	
	protected static List<String> dicttypeLst;

    public static GitStoreWrapper build() {
        if(restTemplate==null){
			restTemplate = SpringUtils.getBean(RestTemplate.class);
			remoteDictService = SpringUtils.getBean(RemoteDictService.class);
			Set<String> set = new HashSet<>();
			dicttypeLst = new ArrayList<>(set);
        }
        return new GitStoreWrapper();
    }

    public GitStoreVo entityVO(GitStore gitStore) {
        if (gitStore == null)
            return null;
        GitStoreVo gitStoreVo = new GitStoreVo();
        BeanUtils.copyProperties(gitStore, gitStoreVo);
        ArrayList<GitStoreVo> collect = new ArrayList<GitStoreVo>();
        collect.add(gitStoreVo);
        
		translateDict(collect);
        return gitStoreVo;
    }

    public List<GitStoreVo> entityVOList(List<GitStore> list) {
        List<GitStoreVo> collect = list.stream().map(entity -> {
            GitStoreVo gitStoreVo = new GitStoreVo();
            BeanUtils.copyProperties(entity, gitStoreVo);
            return gitStoreVo;
        }).collect(Collectors.toList());
		collect=translateDict(collect);
        return collect;
    }
	
	public List<GitStoreVo> translateDict(List<GitStoreVo> list){
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
	
    public IPage<GitStoreVo> pageVO(IPage<GitStore> source) {
        Page<GitStoreVo> page = new Page<GitStoreVo>();
        BeanUtils.copyProperties(source, page, "records");
        try {
            page.setRecords(entityVOList(source.getRecords()));
        } catch (Exception e) {
            throw e;
        }
        return page;
    }
  
  	public void validateGitStore(GitStore gitStore){
      
    }

    public void initGitStore(GitStore gitStore){
      
    }
}
