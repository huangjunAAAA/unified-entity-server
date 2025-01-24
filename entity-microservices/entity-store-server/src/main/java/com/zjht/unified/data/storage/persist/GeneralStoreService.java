package com.zjht.unified.data.storage.persist;

import com.wukong.core.util.SpringUtil;

import com.zjht.unified.common.core.domain.store.StoreMessageDO;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GeneralStoreService implements IDeviceStore,IPointStore, ApplicationRunner {

    @Resource
    private PersistConfig persistConfig;

    private IDeviceStore deviceStore;

    @Resource

    @Override
    public List<Long> saveObjectPoint(StoreMessageDO val) {
        return deviceStore.saveObjectPoint(val);
    }

    @Override
    public Long saveSimplePoint(StoreMessageDO val) {
        return deviceStore.saveSimplePoint(val);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String beanName=persistConfig.getEngine()+"-store";
        deviceStore= (IDeviceStore) SpringUtil.getBean(beanName);
    }
}
