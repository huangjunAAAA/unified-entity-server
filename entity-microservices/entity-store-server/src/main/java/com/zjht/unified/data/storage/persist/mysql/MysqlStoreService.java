package com.zjht.unified.data.storage.persist.mysql;

import cn.hutool.core.util.StrUtil;
import com.third.support.alidruid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;

import com.zjht.unified.common.core.util.AliDruidUtils;
import com.zjht.unified.common.core.util.MysqlDDLUtils;

import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.data.storage.persist.AbstractStoreService;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component("mysql-store")
public class MysqlStoreService extends AbstractStoreService {

    @Resource
    private MysqlDDLService mysqlDDLService;



    public Long saveObject(Map<String,Object> data, String tbl, List<TblCol> def, List<TblIndex> indices, Long colpId,String ver){
        createObjectTable(data,tbl,def,indices,ver);
        MysqlDDLUtils.setJdbcType(def,data);
        if (!data.containsKey(FieldConstants.PROJECT_ID) && !data.containsKey(FieldConstants.PROJECT_ID_CAMEL)) {
            data.put(FieldConstants.PROJECT_ID,colpId);
        }
        data.put(FieldConstants.DATACREATED,new Date());
        data.put(FieldConstants.ACTIVE_STATUS,Integer.parseInt(Constants.YES));
//        data.put(FieldConstants.DRIVER_ID,driverId);
        String insertSQL = mysqlDDLService.insert(tbl, data, def)+";";
        log.info("insertSQL===========================:{}",insertSQL);
        String lastId="SELECT LAST_INSERT_ID();";
        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);
        jdbcTemplate.update(insertSQL);
        Long lastInsertId = jdbcTemplate.queryForObject(lastId, Long.class);
        return lastInsertId;
    }

    @Override
    public int updateObject(Map<String, Object> vals, String tbl, List<TblCol> colDef,String ver) {
        MysqlDDLUtils.setJdbcType(colDef,vals);
        MysqlDDLUtils.addUpdateConditionColumns(colDef);
        MysqlDDLUtils.getExtraReferenceColumns().forEach(col -> {
            if(!col.getNameEn().equals(FieldConstants.ACTIVE_STATUS))
                vals.remove(StringUtils.toUnderScoreCase(col.getNameEn()));
        });
        String updateSql = mysqlDDLService.update(tbl, vals, colDef);
        log.info(" table name :  {}  generate update sql :{}",tbl,updateSql);
        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);
        int update = jdbcTemplate.update(updateSql);
        return update;
    }

    @Override
    public void delExcludeObjectScope(List<Map<String, Object>> vals, String tbl, List<TblCol> colDef,String ver) {
        Optional<TblCol> pk = colDef.stream().filter(c -> c.getIsPK() == 1).findFirst();
        if(!pk.isPresent())
            return;
        String pkName=StrUtil.toUnderlineCase(pk.get().getNameEn());
        List<Object> pkVals = vals.stream().map(t -> t.get(pkName)).collect(Collectors.toList());
        String delSql = MysqlDDLUtils.deleteIn(tbl, pkName, pkVals, true);
        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);

        jdbcTemplate.execute(delSql);
    }

    @PostConstruct
    protected void initDDL(){
        this.ddlService=mysqlDDLService;
    }


    @Override
    public Map<String, Object> getEntityByGuid(String guid) {
        return Collections.emptyMap();
    }
}
