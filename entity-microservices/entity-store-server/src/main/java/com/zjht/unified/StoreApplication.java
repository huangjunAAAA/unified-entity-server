package com.zjht.unified;

import com.google.gson.Gson;
import com.wukong.core.weblog.utils.DateUtil;
import com.wukong.core.weblog.utils.JsonUtil;

import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.store.StoreMessageDO;
import com.zjht.unified.common.core.util.JsonUtilExt;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.*;

@ComponentScan(value = {"com.zjht","com.wukong"})
@EntityScan(basePackages = {"com.zjht"})
@SpringCloudApplication
@EnableFeignClients(basePackages = "com.zjht.**")
@MapperScan("com.zjht.**.mapper.**")
@EnableAsync
@Slf4j
public class StoreApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext app = SpringApplication.run(StoreApplication.class, args);
        log.info("数据存储模块启动成功");
//        test(app);
    }

    private static void test(ConfigurableApplicationContext app) throws Exception{
        StoreMessageDO msg1=new StoreMessageDO();
        msg1.setDataType("MI");

        msg1.setPersistId(303L);
        msg1.setProtocol("http");
        msg1.setSessionId(System.currentTimeMillis()+"");
        msg1.setPlanId(4L);
        msg1.setDriverId(101L);
        msg1.setEtLst(Arrays.asList(new Date()));
        Map<String,Object> data=new HashMap<>();
        data.put("ticketTime",new Date());
        data.put("Name","黄某@"+ DateUtil.formatDateTime(new Date()));
        data.put("Price",1.1d+Math.abs(new Random().nextDouble()));
        data.put("buyNumber",Math.abs(new Random().nextInt()));
        msg1.setData(JsonUtilExt.toJson(Arrays.asList(data)));
//        msg1.setData(3.1d);
        String msg= JsonUtil.toJson(msg1);
//        app.getBean(KafkaConsumerListener.class).onMessage(msg);

//        String pl = DorisDDLUtils.createStreamLoadPipe(KafkaNames.POINT_DATA, app.getBean(BigdataKafkaProperties.class).getBootstrapServers(), (Template) app.getBean("create-pipe-template"));
//        System.out.println(pl);
        List<TblCol> cols=new ArrayList<>();
        cols.add(new TblCol("ticketTime","出票时间","Date",null,0,1));
        cols.add(new TblCol("Name","购票人","String",null,0,0));
        cols.add(new TblCol("Price","购票价格","Double",null,0,0));
        cols.add(new TblCol("buyNumber","人数","Integer",null,0,0));
//
        System.out.println(new Gson().toJson(cols));
//        List<TblIndex> indices=new ArrayList<>();
//        indices.add(new TblIndex("index1111",new ArrayList<>(Arrays.asList("Name","ticketTime"))));
//        System.out.println(new Gson().toJson(indices));
//        TableCreateDDL createSQL = app.getBean(DorisDDLService.class).createTable("table1", data, cols, indices);
//        System.out.println(createSQL.getDdl());


//        String xx="create table table1\n" +
//                "(\n" +
//                "    ticket_time  datetime comment \"出票时间\" ,\n" +
//                "    name  varchar(255) comment \"购票人\" \n" +
//                ",\n" +
//                "        INDEX table1_ticket_time (ticket_time) USING INVERTED  ,\n" +
//                "        INDEX table1_name (name) USING INVERTED  \n" +
//                ") ENGINE=olap\n" +
//                "PARTITION BY RANGE(ticket_time) ()\n" +
//                "DISTRIBUTED BY HASH(ticket_time)\n" +
//                "PROPERTIES\n" +
//                "(\n" +
//                "        \"dynamic_partition.enable\" = \"true\" ,\n" +
//                "        \"dynamic_partition.time_unit\" = \"MONTH\" ,\n" +
//                "        \"dynamic_partition.end\" = \"2\" ,\n" +
//                "        \"dynamic_partition.prefix\" = \"p\" ,\n" +
//                "        \"dynamic_partition.buckets\" = \"8\" ,\n" +
//                "        \"dynamic_partition.start_day_of_month\" = \"1\" \n" +
//                ");\n" +
//                "\n" +
//                "    CREATE ROUTINE LOAD load_table1Job on table1\n" +
//                "        PROPERTIES\n" +
//                "        (\n" +
//                "            \"desired_concurrent_number\"=\"1\",\n" +
//                "            \"strict_mode\"=\"false\",\n" +
//                "            \"format\" = \"json\"\n" +
//                "        )\n" +
//                "        FROM KAFKA\n" +
//                "        (\n" +
//                "            \"kafka_broker_list\"= \"192.168.4.11:9092,192.168.4.12:9092,192.168.4.13:9092\",\n" +
//                "            \"kafka_topic\" = \"doris_stream_load_table1\",\n" +
//                "            \"property.group.id\" = \"table1\",\n" +
//                "            \"property.kafka_default_offsets\" = \"OFFSET_BEGINNING\",\n" +
//                "            \"property.enable.auto.commit\" = \"false\"\n" +
//                "        );";

//        JdbcTemplate jdbc = app.getBean(JdbcTemplate.class);
//        jdbc.execute(xx);
//        boolean is=app.getBean(DorisStoreService.class).checkTableExist("table1");
//
//
//        if(!is){
//            for (Iterator<String> iterator = createSQL.getDdl().iterator(); iterator.hasNext(); ) {
//                String sql =  iterator.next();
//                jdbc.execute(sql);
//            }
//
//        }
//        app.getBean(DorisStoreService.class).saveObject(data,"table1",ref,cols,indices);
    }

}
