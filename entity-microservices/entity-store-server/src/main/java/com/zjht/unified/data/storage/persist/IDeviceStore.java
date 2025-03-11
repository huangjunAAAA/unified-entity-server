package com.zjht.unified.data.storage.persist;



import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;

import java.util.List;

public interface IDeviceStore extends IPointStore{
    List<Long> saveObjectPoint(EntityStoreMessageDO val);

    List<Integer> updateEntity(EntityStoreMessageDO val);
 }
