package com.zjht.unified.data.storage.persist;



import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.common.core.domain.store.StoreMessageDO;

import java.util.List;

public interface IDeviceStore extends IPointStore{
    List<Long> saveObjectPoint(EntityStoreMessageDO val);
}
