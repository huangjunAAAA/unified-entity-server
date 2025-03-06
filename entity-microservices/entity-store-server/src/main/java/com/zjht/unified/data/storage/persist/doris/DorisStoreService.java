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
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.data.redis.core.RedisTemplate;
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
    public Long saveObject(Map<String, Object> vals, String tbl, List<TblCol> colDef, List<TblIndex> indices, Long colpId) {
        return 0L;
    }

    private final Map<String,Boolean> streamRoutineExistence=new HashMap<>();

    private static final Map<String, String> timeFmt=new HashMap<>();

    @Override
    public void createObjectTable(Map<String, Object> data, String tbl, List<TblCol> def, List<TblIndex> indices) {
        super.createObjectTable(data, tbl, def, indices);
        createStreamLoad(tbl);
    }

    public void createStreamLoad(String tbl){
        if(!checkStreamRoutine(tbl)){
            synchronized (streamRoutineExistence){
                String sql = dorisDDLService.createStreamRoutine(tbl);
                log.info("creating table:"+tbl+", with sql:\n"+sql);
                jdbcTemplate.execute(sql);
                streamRoutineExistence.put(tbl,true);
            }
        }
    }

    public boolean checkStreamRoutine(String name){
        Boolean exist = streamRoutineExistence.get(name);
        if(exist!=null)
            return exist;
        synchronized (streamRoutineExistence){
            exist = streamRoutineExistence.get(name);
            if(exist!=null)
                return exist;
            try {
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

    public Long saveSimpleRawData(String val, String processedData,  String dataType, Date eventTime, Long colpId, Long driverId) {
        String idName= Constants.DORIS_ID_PREFIX+ FieldConstants.SIMPLE_DATA_TABLE;
        Long id = redisTemplate.opsForValue().increment(idName);
        Map<String,Object> values=new HashMap<>();
        values.put("id",id);
        values.put(FieldConstants.DATATIME,eventTime);
        values.put(FieldConstants.PROJECT_ID,colpId);
        values.put(FieldConstants.DRIVER_ID,driverId);
//        values.put(FieldConstants.SYSTEM_ID,ref.getSystemId());
//        values.put(FieldConstants.DEVICE_ID,ref.getDeviceId());
//        values.put(FieldConstants.POINT_ID,ref.getPointId());
        values.put(FieldConstants.SIMPLE_RAW_DATA,val);
        values.put(FieldConstants.PROCESSED_DATA,processedData);
        values.put(FieldConstants.STATUS,1);
        values.put(FieldConstants.DATATYPE,dataType);
        try{
            Double d=Double.parseDouble(processedData);
            values.put(FieldConstants.SIMPLE_NUMERIC_DATA,d);
        }catch (Exception e){

        }
        values.put("status",1);
        String ts = DateUtil.formatDateTime(new Date());
        values.put("create_time",ts);
        values.put("update_time",ts);
        String vss = JsonUtilUnderline.toJson(values);
        createStreamLoad(FieldConstants.SIMPLE_DATA_TABLE);
        kafkaTemplate.send(KafkaNames.DORIS_TOPIC_PRIFIX+FieldConstants.SIMPLE_DATA_TABLE,"rt", vss);
        return id;
    }


    public Long saveObject(Map<String, Object> data, String tbl, List<TblCol> preDefLst, List<TblIndex> indices,Long colpId, Long driverId) {
        DorisDDLUtils.setJdbcType(preDefLst,data);
        createObjectTable(data,tbl,preDefLst,indices);
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
        sData.put(FieldConstants.DRIVER_ID,driverId);
        String vss = JsonUtilUnderline.toJson(sData);
        kafkaTemplate.send(KafkaNames.DORIS_TOPIC_PRIFIX+tbl,"rt", vss);
        return id;
    }

    @Override
    public void delExcludeObjectScope(List<Map<String, Object>> vals, String tbl, List<TblCol> colDef) {
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
}
