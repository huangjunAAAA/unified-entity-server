package com.zjht.unified.data.storage.persist;



import com.third.support.alidruid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.zjht.unified.common.core.domain.ddl.TableCreateDDL;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import com.zjht.unified.common.core.domain.store.EntityStoreMessageDO;
import com.zjht.unified.common.core.util.AliDruidUtils;
import com.zjht.unified.common.core.util.MysqlDDLUtils;
import com.zjht.unified.common.core.util.StringUtils;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.annotation.Resource;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractStoreService implements IObjectEntityStore {


    @Resource
    protected JdbcTemplate jdbcTemplate;

    protected TableDDLService ddlService;

    @Override
    public List<Integer> updateEntity(EntityStoreMessageDO sMsg) {
        List<TblCol> colDef = sMsg.getCols();
        List<Integer> ids=new ArrayList<>();
        if(colDef!=null){
            List<Map<String,Object>> data= EntityStoreMessageDO.getDataAsObjectList(sMsg);
            List<TblIndex> indices = sMsg.getIndices();

            for (Iterator<Map<String, Object>> iterator = data.iterator(); iterator.hasNext(); ) {
                Map<String, Object> vals =  iterator.next();
                int id=updateObject(vals,sMsg.getTblName(),colDef);
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
                Long id=saveObject(vals,sMsg.getTblName(),colDef,indices,sMsg.getPrjId());
                ids.add(id);
            }
        }
        return ids;
    }


    public abstract void delExcludeObjectScope(List<Map<String,Object>> vals,String tbl,List<TblCol> colDef);


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
        MysqlDDLUtils.addEntityReferenceColumns(def);
    }

    public abstract Long saveObject(Map<String, Object> vals, String tbl, List<TblCol> colDef, List<TblIndex> indices,Long colpId);

    public abstract int updateObject(Map<String, Object> vals, String tbl, List<TblCol> colDef);

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

    @Override
    public List<Map<String,Object>> queryEntity(ClazzDefCompositeDO clazzDef, Integer page, Integer size, String orderby, String asc,
                                         Map<String, Object> equals, Map<String, String> like, Map<String, List<Object>> in){
        List<String> cols = clazzDef.getClazzIdFieldDefList().stream().map(f -> StringUtils.toUnderScoreCase(f.getName())).collect(Collectors.toList());
        Map<String,Object> sEquals = null;
        if(equals!=null&&equals.size()>0)
            sEquals=equals.entrySet().stream().collect(Collectors.toMap(k -> StringUtils.toUnderScoreCase(k.getKey()), Map.Entry::getValue));
        MySqlSelectQueryBlock sQuery = AliDruidUtils.createGeneralValueSQLFromTable(clazzDef.getTbl(), cols, sEquals);

        if(like!=null&&like.size()>0) {
            Map<String,String> sLikes = like.entrySet().stream().collect(Collectors.toMap(k -> StringUtils.toUnderScoreCase(k.getKey()), v -> {
                String val = v.getValue();
                if (!val.startsWith("%") && !val.endsWith("%")) {
                    val = "%" + val + "%";
                }
                return val;
            }));
            AliDruidUtils.createQueryLikesFromTable(sQuery, sLikes);
        }

        if(in!=null&&in.size()>0){
            for (Map.Entry<String, List<Object>> entry : in.entrySet()) {
                String k = StringUtils.toUnderScoreCase(entry.getKey());
                List<Object> vals = entry.getValue();
                if(vals!=null&&vals.size()>0){
                    AliDruidUtils.createQueryInFromTable(sQuery, k,vals);
                }
            }
        }

        AliDruidUtils.setOrderByAndLimit(sQuery,page,size,orderby,asc);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sQuery.toString());
        return list;
    }

    @Override
    public void deleteEntity(String table, String guid, Long id) {
        if(id!=null)
            jdbcTemplate.update("delete from "+table+" where id=?",id);
        else if(guid!=null)
            jdbcTemplate.update("delete from "+table+" where guid=?",guid);
    }
}
