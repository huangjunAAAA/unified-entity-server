package com.zjht.unified.data.storage.persist.doris;

import com.wukong.bigdata.kafka.config.BigdataKafkaProperties;
import com.zjht.unified.data.common.core.domain.ddl.TableCreateDDL;
import com.zjht.unified.data.common.core.domain.ddl.TblCol;
import com.zjht.unified.data.common.core.domain.ddl.TblIndex;
import com.zjht.unified.data.common.core.util.DorisDDLUtils;
import com.zjht.unified.data.storage.persist.PersistConfig;
import com.zjht.unified.data.storage.persist.TableDDLService;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DorisDDLService implements TableDDLService {

    @Autowired
    private BigdataKafkaProperties properties;

    @Resource
    private PersistConfig persistConfig;

    @Resource(name = "create-table-template")
    private Template createTblTemp;

    @Resource(name = "create-pipe-template")
    private Template createPipeTemp;


    @Override
    public TableCreateDDL createTable(String tbl, Map<String, Object> actualData, List<TblCol> preDefLst, List<TblIndex> indices) {
        TableCreateDDL ddl = DorisDDLUtils.createTable(tbl, actualData, preDefLst, indices, persistConfig.getDoris(), createTblTemp, true);
        return ddl;
    }

    public String createStreamRoutine(String tbl) {
        String pddl = DorisDDLUtils.createStreamLoadPipe(tbl, properties.getBootstrapServers(), createPipeTemp);
        return pddl;
    }
}
