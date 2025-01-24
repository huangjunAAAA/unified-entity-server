package com.zjht.unified.data.storage.persist;


import com.zjht.unified.common.core.domain.store.StoreMessageDO;

public interface IPointStore {
    Long saveSimplePoint(StoreMessageDO val);
}
