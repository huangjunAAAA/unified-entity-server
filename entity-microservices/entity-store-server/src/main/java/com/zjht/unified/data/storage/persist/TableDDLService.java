package com.zjht.unified.data.storage.persist;

import com.zjht.unified.data.common.core.domain.ddl.TableCreateDDL;
import com.zjht.unified.data.common.core.domain.ddl.TblCol;
import com.zjht.unified.data.common.core.domain.ddl.TblIndex;

import java.util.List;
import java.util.Map;

public interface TableDDLService {
    TableCreateDDL createTable(String tbl, Map<String, Object> actualData, List<TblCol> preDefLst, List<TblIndex> indices);
}
