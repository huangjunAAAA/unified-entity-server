package com.zjht.unified.data.storage.persist.doris;

import cn.hutool.core.util.StrUtil;
import com.cobber.fta.dates.DateTimeParser;
import com.wukong.core.weblog.utils.DateUtil;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.constants.KafkaNames;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;
import com.zjht.unified.common.core.util.DorisDDLUtils;
import com.zjht.unified.common.core.util.JsonUtilUnderline;
import com.zjht.unified.common.core.util.MysqlDDLUtils;
import com.zjht.unified.data.storage.persist.AbstractStoreService;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.SQLSyntaxErrorException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component("doris-store")
@Slf4j
public class DorisStoreService extends AbstractStoreService {

    @Resource
    private DorisDDLService dorisDDLService;

    @Resource
    private KafkaTemplate<String,String> kafkaTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private Admin admin;


    @Override
    public int updateObject(Map<String, Object> vals, String tbl, List<TblCol> colDef,String ver) {
        MysqlDDLUtils.setJdbcType(colDef,vals);
        MysqlDDLUtils.addUpdateConditionColumns(colDef);
        if(!vals.containsKey(FieldConstants.DATATIME)){
            vals.put(FieldConstants.DATATIME,DateUtil.formatDateTime(new Date()));
        }
        String updateSql = dorisDDLService.update(tbl, vals, colDef);
        log.info(" table name :  {}  generate update sql :{}",tbl,updateSql);
        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);

        int update = jdbcTemplate.update(updateSql);
        return update;
    }

    private final Map<String,Boolean> streamRoutineExistence=new HashMap<>();

    private static final Map<String, String> timeFmt=new HashMap<>();

    @Override
    public void createObjectTable(Map<String, Object> data, String tbl, List<TblCol> def, List<TblIndex> indices,String ver) {
        super.createObjectTable(data, tbl, def, indices,ver);
        createStreamLoad(tbl,ver);
    }


    public void createStreamLoad(String tbl,String ver){
        if(!checkStreamRoutine(tbl,ver)){
            synchronized (streamRoutineExistence){
                String sql = dorisDDLService.createStreamRoutine(tbl);
                log.info("creating table:"+tbl+", with sql:\n"+sql);
                JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);

                jdbcTemplate.execute(sql);
                streamRoutineExistence.put(tbl,true);
            }
        }
    }

    public boolean checkStreamRoutine(String name,String ver){
        Boolean exist = streamRoutineExistence.get(name);
        if(exist!=null)
            return exist;
        synchronized (streamRoutineExistence){
            exist = streamRoutineExistence.get(name);
            if(exist!=null)
                return exist;
            try {
                JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);
                SqlRowSet rs = jdbcTemplate.queryForRowSet("SHOW ROUTINE LOAD FOR load_" + name+"Job");
                exist = rs.next();
            } catch (Exception e){
                if(!(e.getCause() instanceof SQLSyntaxErrorException))
                    log.warn(e.getMessage(),e);
                exist=false;
            }
            streamRoutineExistence.put(name,exist);
            return exist;
        }
    }

    public Long saveObject(Map<String, Object> data, String tbl, List<TblCol> preDefLst, List<TblIndex> indices,Long colpId,String ver) {
        DorisDDLUtils.setJdbcType(preDefLst,data);
        createObjectTable(data,tbl,preDefLst,indices,ver);
        createKafkaTopic(KafkaNames.DORIS_TOPIC_PRIFIX+tbl);
        Map<String, TblCol> colMap = preDefLst.stream().collect(Collectors.toMap(d -> d.getNameEn(), Function.identity()));
        data.entrySet().stream().forEach(e -> {
            TblCol colDef = colMap.get(e.getKey());
            Object val = e.getValue();
            if(colDef.getJdbcType().equals("datetime")){
                if(val instanceof String) {
                    String k = tbl + "." + colDef.getNameEn();
                    String dmt = timeFmt.get(k);
                    if (dmt == null) {
                        final DateTimeParser dtp = new DateTimeParser().withDateResolutionMode(DateTimeParser.DateResolutionMode.MonthFirst).withLocale(Locale.ENGLISH);
                        dmt = dtp.determineFormatString(val.toString());
                        timeFmt.put(k, dmt);
                    }
                    val = DateUtil.parse(val.toString(), dmt);
                }
                e.setValue(DateUtil.formatDateTime((Date) val));
            }
        });

        Optional<TblCol> tmpTs = preDefLst.stream().filter(c -> c.getIsTempstamp() == 1 || c.getNameEn().equalsIgnoreCase(FieldConstants.DATATIME)).findFirst();
        if (!tmpTs.isPresent()) {
            data.put(FieldConstants.DATATIME,DateUtil.formatDateTime(new Date()));
        }else{
            data.put(FieldConstants.DATATIME,data.get(tmpTs.get().getNameEn()));
        }

        Map<String,Object> sData=new LinkedHashMap<>();

        data.entrySet().stream().forEach(e->{
            sData.put(StrUtil.toUnderlineCase(e.getKey()),e.getValue());
        });
        String idName= Constants.DORIS_ID_PREFIX+tbl;
        Long id = redisTemplate.opsForValue().increment(idName);
        sData.put("id",id);
//        sData.put(FieldConstants.SYSTEM_ID,ref.getSystemId());
//        sData.put(FieldConstants.DEVICE_ID,ref.getDeviceId());
//        sData.put(FieldConstants.POINT_ID,ref.getPointId());
        sData.put(FieldConstants.PROJECT_ID,colpId);
//        sData.put(FieldConstants.DRIVER_ID,driverId);
        String vss = JsonUtilUnderline.toJson(sData);
        kafkaTemplate.send(KafkaNames.DORIS_TOPIC_PRIFIX+tbl,"rt", vss);
        return id;
    }

    @Override
    public void delExcludeObjectScope(List<Map<String, Object>> vals, String tbl, List<TblCol> colDef,String ver) {
        Optional<TblCol> pk = colDef.stream().filter(c -> c.getIsPK() == 1).findFirst();
        if(!pk.isPresent())
            return;
        String pkName=StrUtil.toUnderlineCase(pk.get().getNameEn());
        List<Object> pkVals = vals.stream().map(t -> t.get(pkName)).collect(Collectors.toList());
        String delSql = MysqlDDLUtils.deleteIn(tbl, pkName, pkVals, true);

        String deleteWithoutPartition="set delete_without_partition=true;";
        delSql=deleteWithoutPartition+delSql;
        //无法得知其他数据所在的分区
//        Optional<TblCol> tmpTs = colDef.stream().filter(c -> c.getIsTempstamp() == 1 || c.getNameEn().equalsIgnoreCase(FieldConstants.DATATIME)).findFirst();
//        if (!tmpTs.isPresent()) {
//            String deleteWithoutPartition="set delete_without_partition=true;";
//            delSql=deleteWithoutPartition+delSql;
//        }else{
//            Set<String> monthSet = vals.stream().map(t -> {
//                Date ts = (Date) t.get(tmpTs.get().getNameEn());
//                String time = DateUtil.format(ts, "yyyyMM");
//                return "P" + time;
//            }).collect(Collectors.toSet());
//            String partitionList = " PARTITION ("+StrUtil.join(",", monthSet)+") WHERE";
//            delSql=delSql.replace("WHERE",partitionList);
//        }
        JdbcTemplate jdbcTemplate = dynamicDataSourceService.getJdbcTemplateForVersion(ver);

        jdbcTemplate.execute(delSql);
    }

    private Set<String> topics=new HashSet<>();
    public void createKafkaTopic(String topicName) {
        try {
            if(topics.contains(topicName))
                return;
            log.info("query if topic exist:"+topicName);
            ListTopicsResult topicsResult = admin.listTopics();
            Set<String> topics = topicsResult.names().get();
            if (topics.contains(topicName)) {
                topics.add(topicName);
                return;
            }

            log.info("creating topic:"+topicName);
            int partitions = 1;
            short replicationFactor = 1;
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            CreateTopicsResult result = admin.createTopics(
                    Collections.singleton(newTopic)
            );


            KafkaFuture<Void> future = result.values().get(topicName);
            future.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }



    @PostConstruct
    protected void initDDL(){
        this.ddlService=dorisDDLService;
    }

    @Override
    public Map<String, Object> getEntityByGuid(String guid) {
        return Collections.emptyMap();
    }
}
