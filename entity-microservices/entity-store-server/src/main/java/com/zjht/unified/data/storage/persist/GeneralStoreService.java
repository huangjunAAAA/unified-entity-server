package com.zjht.unified.data.storage.persist;

import com.wukong.core.util.SpringUtil;

import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class GeneralStoreService implements IObjectEntityStore, ApplicationRunner {

    @Resource
    private PersistConfig persistConfig;

    private IObjectEntityStore objectEntityStore;


    @Override
    public List<Long> saveEntity(EntityStoreMessageDO val) {
        return objectEntityStore.saveEntity(val);
    }

    @Override
    public List<Integer> updateEntity(EntityStoreMessageDO val) {
        return objectEntityStore.updateEntity(val);
    }

    @Override
    public List<Map<String, Object>> queryEntity(String ver,ClazzDefCompositeDO clazzDef, Integer page, Integer size, String orderby, String asc, Map<String, Object> equals, Map<String, String> like, Map<String, List<Object>> in) {
        return objectEntityStore.queryEntity(ver,clazzDef, page, size, orderby, asc, equals, like, in);
    }


    @Override
    public void deleteEntity(String ver,String table, String guid, Long id) {
        objectEntityStore.deleteEntity(ver,table, guid, id);
    }

    @Override
    public Map<String, Object> getEntityByGuid(String guid) {
        return objectEntityStore.getEntityByGuid(guid);
    }

    @Override
    public void removeEntityFieldByGuid(EntityStoreMessageDO val) {
        objectEntityStore.removeEntityFieldByGuid(val);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        String beanName=persistConfig.getEngine()+"-store";
        objectEntityStore= (IObjectEntityStore) SpringUtil.getBean(beanName);
    }


}
