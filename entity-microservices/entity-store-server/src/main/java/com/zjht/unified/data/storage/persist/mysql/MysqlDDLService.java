package com.zjht.unified.data.storage.persist.mysql;

import com.zjht.unified.common.core.domain.ddl.TableCreateDDL;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import com.zjht.unified.common.core.util.MysqlDDLUtils;
import com.zjht.unified.data.storage.persist.TableDDLService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MysqlDDLService implements TableDDLService {
    @Override
    public TableCreateDDL createTable(String tbl, Map<String, Object> actualData, List<TblCol> preDefLst, List<TblIndex> indices) {
        return MysqlDDLUtils.createTable(tbl,actualData,preDefLst,indices, true);
    }


    public String insert(String tbl, Map<String, Object> data, List<TblCol> def) {
        return MysqlDDLUtils.insert(tbl,data,def);
    }
}
