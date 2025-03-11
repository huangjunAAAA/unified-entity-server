package com.zjht.unified.data.storage.persist.mysql;

import cn.hutool.core.util.StrUtil;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;

import com.zjht.unified.common.core.util.MysqlDDLUtils;
import com.zjht.unified.data.entity.RawData;

import com.zjht.unified.data.storage.persist.AbstractStoreService;
import com.zjht.unified.data.storage.service.IRawDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component("mysql-store")
public class MysqlStoreService extends AbstractStoreService {

    @Resource
    private IRawDataService rawDataService;
    @Resource
    private MysqlDDLService mysqlDDLService;


    public Long saveObject(Map<String,Object> data, String tbl, List<TblCol> def, List<TblIndex> indices, Long colpId){
        createObjectTable(data,tbl,def,indices);
        MysqlDDLUtils.setJdbcType(def,data);
//        data.put(FieldConstants.SYSTEM_ID,ref.getSystemId());
//        data.put(FieldConstants.DEVICE_ID,ref.getDeviceId());
//        data.put(FieldConstants.POINT_ID,ref.getPointId());
        data.put(FieldConstants.PROJECT_ID,colpId);
//        data.put(FieldConstants.DRIVER_ID,driverId);
        String insertSQL = mysqlDDLService.insert(tbl, data, def)+";";
        String lastId="SELECT LAST_INSERT_ID();";
        jdbcTemplate.update(insertSQL);
        Long lastInsertId = jdbcTemplate.queryForObject(lastId, Long.class);
        return lastInsertId;
    }

    @Override
    public int updateObject(Map<String, Object> vals, String tbl, List<TblCol> colDef) {
        MysqlDDLUtils.setJdbcType(colDef,vals);
        MysqlDDLUtils.addUpdateConditionColumns(colDef);
        String updateSql = mysqlDDLService.update(tbl, vals, colDef);
        log.info(" table name :  {}  generate update sql :{}",tbl,updateSql);
        int update = jdbcTemplate.update(updateSql);
        return update;
    }

    @Override
    public void delExcludeObjectScope(List<Map<String, Object>> vals, String tbl, List<TblCol> colDef) {
        Optional<TblCol> pk = colDef.stream().filter(c -> c.getIsPK() == 1).findFirst();
        if(!pk.isPresent())
            return;
        String pkName=StrUtil.toUnderlineCase(pk.get().getNameEn());
        List<Object> pkVals = vals.stream().map(t -> t.get(pkName)).collect(Collectors.toList());
        String delSql = MysqlDDLUtils.deleteIn(tbl, pkName, pkVals, true);
        jdbcTemplate.execute(delSql);
    }

    public Long saveSimpleRawData(String val, String processedData, String dataType, Date eventTime, Long colpId, Long driverId){
        RawData rawData=new RawData();
        rawData.setDataTime(eventTime);
//        rawData.setSysId(ref.getSystemId());
//        rawData.setPointId(ref.getPointId());
//        rawData.setDeviceId(ref.getDeviceId());
        rawData.setColpId(colpId);
        rawData.setDriverId(driverId);
        rawData.setRawData(val);
        rawData.setProcessedData(processedData);
        rawData.setStatus(1);
        try{
            Double d=Double.parseDouble(processedData);
            rawData.setNumericData(d);
        }catch (Exception e){

        }
        rawDataService.save(rawData);
        return rawData.getId();
    }

    @PostConstruct
    protected void initDDL(){
        this.ddlService=mysqlDDLService;
    }
}
