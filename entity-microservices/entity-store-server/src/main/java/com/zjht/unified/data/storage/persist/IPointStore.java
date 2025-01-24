package com.zjht.unified.data.storage.persist;

import com.zjht.unified.data.common.core.domain.store.StoreMessageDO;

public interface IPointStore {
    Long saveSimplePoint(StoreMessageDO val);
}
