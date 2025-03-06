package com.zjht.unified.common.core.domain.store;

import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EntityStoreMessageDO {
    private Object data;

    private String tblName;

    private List<TblCol> cols;

    private List<TblIndex> indices;

    private Long prjId;
}

