package com.zjht.unified.data.storage.persist;


import com.zjht.unified.data.common.core.domain.store.StoreMessageDO;
import com.zjht.unified.data.common.core.domain.ddl.TableCreateDDL;
import com.zjht.unified.data.common.core.domain.ddl.TblCol;
import com.zjht.unified.data.common.core.domain.ddl.TblIndex;
import com.zjht.unified.data.common.core.util.MysqlDDLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.annotation.Resource;
import java.sql.SQLSyntaxErrorException;
import java.util.*;

@Slf4j
public abstract class AbstractStoreService implements IDeviceStore {


    @Resource
    protected JdbcTemplate jdbcTemplate;

    protected TableDDLService ddlService;

    @Override
    public List<Long> saveObjectPoint(StoreMessageDO sMsg) {

        return null;
    }


    public abstract void delExcludeObjectScope(List<Map<String,Object>> vals,String tbl,List<TblCol> colDef);

    @Override
    public Long saveSimplePoint(StoreMessageDO val) {
        return null;
    }

    public void createObjectTable(Map<String,Object> data,String tbl, List<TblCol> def,List<TblIndex> indices){
        if(!checkTableExist(tbl)){
            synchronized (tableExistence){
                if(!checkTableExist(tbl)){
                    TableCreateDDL createTable = ddlService.createTable(tbl, data, def, indices);
                    for (Iterator<String> iterator = createTable.getDdl().iterator(); iterator.hasNext(); ) {
                        String sql =  iterator.next();
                        log.info("creating table:"+tbl+", with sql:\n"+sql);
                        jdbcTemplate.execute(sql);
                    }
                    tableExistence.put(tbl,true);
                }
            }
        }
        MysqlDDLUtils.addSdpReferenceColumns(def);
    }

    private final Map<String,Boolean> tableExistence=new HashMap<>();

    public boolean checkTableExist(String name){
        Boolean exist = tableExistence.get(name);
        if(exist!=null)
            return exist;
        synchronized (tableExistence){
            exist = tableExistence.get(name);
            if(exist!=null)
                return exist;
            try {
                SqlRowSet rs = jdbcTemplate.queryForRowSet("show create table " + name);
                exist = rs.next();
            } catch (Exception e){
                if(!(e.getCause() instanceof SQLSyntaxErrorException))
                    log.warn(e.getMessage(),e);
                exist=false;
            }
            tableExistence.put(name,exist);
            return exist;
        }
    }



}
