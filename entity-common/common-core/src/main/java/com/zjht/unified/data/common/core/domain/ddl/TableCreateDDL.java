package com.zjht.unified.data.common.core.domain.ddl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableCreateDDL {
    private List<String> ddl=new ArrayList<>();
    private List<TblCol> cols;
    private TblCol pk;
    private String dbType;
}
