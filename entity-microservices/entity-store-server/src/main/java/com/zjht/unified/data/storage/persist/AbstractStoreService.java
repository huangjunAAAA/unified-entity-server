package com.zjht.unified.data.storage.persist;



import com.third.support.alidruid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.ddl.TableCreateDDL;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.common.core.util.AliDruidUtils;
import com.zjht.unified.common.core.util.MysqlDDLUtils;
import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.data.storage.service.DynamicDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.annotation.Resource;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractStoreService implements IObjectEntityStore {


    protected TableDDLService ddlService;

    @Resource
    protected DynamicDataSourceService dynamicDataSourceService;

    @Override
    public List<Integer> updateEntity(EntityStoreMessageDO sMsg) {
        List<TblCol> colDef = sMsg.getCols();
        List<Integer> ids=new ArrayList<>();
        if(colDef!=null){
            List<Map<String,Object>> data= EntityStoreMessageDO.getDataAsObjectList(sMsg);
            List<TblIndex> indices = sMsg.getIndices();

            for (Iterator<Map<String, Object>> iterator = data.iterator(); iterator.hasNext(); ) {
                Map<String, Object> vals =  iterator.next();
                int id=updateObject(vals,sMsg.getTblName(),colDef,sMsg.getVer());
                ids.add(id);
            }
        }
        return ids;
    }

    @Override
    public List<Long> saveEntity(EntityStoreMessageDO sMsg) {

        List<TblCol> colDef = sMsg.getCols();
        List<Long> ids=new ArrayList<>();
        if(colDef!=null){
            List<Map<String,Object>> data= EntityStoreMessageDO.getDataAsObjectList(sMsg);
            List<TblIndex> indices = sMsg.getIndices();

            for (Iterator<Map<String, Object>> iterator = data.iterator(); iterator.hasNext(); ) {
                Map<String, Object> vals =  iterator.next();
                Long id=saveObject(vals,sMsg.getTblName(),colDef,indices,sMsg.getPrjId(),sMsg.getVer());
                ids.add(id);
            }
        }
        return ids;
    }


    public abstract void delExcludeObjectScope(List<Map<String,Object>> vals,String tbl,List<TblCol> colDef,String ver);


    public void createObjectTable(Map<String,Object> data,String tbl, List<TblCol> def,List<TblIndex> indices,String ver){
        if(!checkTableExist(tbl,ver)){
            synchronized (tableExistence){
                if(!checkTableExist(tbl,ver)){
                    TableCreateDDL createTable = ddlService.createTable(tbl, data, def, indices);
                    for (Iterator<String> iterator = createTable.getDdl().iterator(); iterator.hasNext(); ) {
                        String sql =  iterator.next();
                        log.info("creating table:"+tbl+", with sql:\n"+sql);
                        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);
                        jdbcTemplate.execute(sql);
                    }
                    tableExistence.put(tbl,true);
                }
            }
        }
    }

    public abstract Long saveObject(Map<String, Object> vals, String tbl, List<TblCol> colDef, List<TblIndex> indices,Long colpId,String ver);

    public abstract int updateObject(Map<String, Object> vals, String tbl, List<TblCol> colDef,String ver);

    private final Map<String,Boolean> tableExistence=new HashMap<>();

    public boolean checkTableExist(String name,String ver){
        Boolean exist = tableExistence.get(name);
        if(exist!=null)
            return exist;
        synchronized (tableExistence){
            exist = tableExistence.get(name);
            if(exist!=null)
                return exist;
            try {
                JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);
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

    @Override
    public List<Map<String,Object>> queryEntity(String ver,ClazzDefCompositeDO clazzDef, Integer page, Integer size, String orderby, String asc,
                                         Map<String, Object> equals, Map<String, String> like, Map<String, List<Object>> in){
        List<String> allCols = clazzDef.getClazzIdFieldDefList().stream().map(f -> StringUtils.toUnderScoreCase(f.getName())).collect(Collectors.toList());
        MysqlDDLUtils.getExtraReferenceColumns().forEach(c -> allCols.add(c.getNameEn()));
        Map<String,Object> sEquals = null;
        if(equals!=null&&equals.size()>0){
            for (Iterator<String> iterator = equals.keySet().iterator(); iterator.hasNext(); ) {
                String kc =  iterator.next();
                String nkc=StringUtils.toUnderScoreCase(kc);
                if(allCols.contains(nkc)){
                    sEquals.put(nkc,equals.get(kc));
                }
            }
        }

        String tbl=clazzDef.getTbl();
        if(StringUtils.isBlank(tbl)){
            tbl=StringUtils.toUnderScoreCase(clazzDef.getName());
        }
        MySqlSelectQueryBlock sQuery = AliDruidUtils.createGeneralValueSQLFromTable(tbl, allCols, sEquals);

        if(like!=null&&like.size()>0) {
            Map<String,String> sLikes = new HashMap<>();
            for (Iterator<String> iterator = like.keySet().iterator(); iterator.hasNext(); ) {
                String kc =  iterator.next();
                String nkc=StringUtils.toUnderScoreCase(kc);
                if(allCols.contains(nkc)){
                    String val=like.get(kc);
                    if (!val.startsWith("%") && !val.endsWith("%")) {
                        val = "%" + val + "%";
                    }
                    sLikes.put(nkc,val);
                }
            }
            AliDruidUtils.createQueryLikesFromTable(sQuery, sLikes);
        }

        if(in!=null&&in.size()>0){
            for (Map.Entry<String, List<Object>> entry : in.entrySet()) {
                String k = StringUtils.toUnderScoreCase(entry.getKey());
                if(allCols.contains(k)) {
                    List<Object> vals = entry.getValue();
                    if (vals != null && vals.size() > 0) {
                        AliDruidUtils.createQueryInFromTable(sQuery, k, vals);
                    }
                }
            }
        }

        AliDruidUtils.setOrderByAndLimit(sQuery,page,size,orderby,asc);
        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sQuery.toString());
        return list;
    }

    @Override
    public void deleteEntity(String ver,String table, String guid, Long id) {
        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);
        if(id!=null)
            jdbcTemplate.update("delete from "+table+" where id=?",id);
        else if(guid!=null)
            jdbcTemplate.update("delete from "+table+" where guid=?",guid);
    }

    @Override
    public void removeEntityFieldByGuid(EntityStoreMessageDO sMsg) {
        List<Map<String,Object>> data= EntityStoreMessageDO.getDataAsObjectList(sMsg);
        for (Iterator<Map<String, Object>> iterator = data.iterator(); iterator.hasNext(); ) {
            Map<String, Object> valLst = iterator.next();
            for (Iterator<TblCol> iter2 = sMsg.getCols().iterator(); iter2.hasNext(); ) {
                TblCol col = iter2.next();
                valLst.put(col.getNameEn(),null);
            }
            updateObject(valLst,sMsg.getTblName(),sMsg.getCols(),sMsg.getVer());
        }
    }
}
