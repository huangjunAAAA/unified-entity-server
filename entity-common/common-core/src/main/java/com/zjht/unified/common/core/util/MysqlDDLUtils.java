package com.zjht.unified.common.core.util;

import cn.hutool.core.util.StrUtil;
import com.cobber.fta.dates.DateTimeParser;
import com.third.support.alidruid.sql.SQLUtils;
import com.third.support.alidruid.sql.ast.SQLDataTypeImpl;
import com.third.support.alidruid.sql.ast.SQLExpr;
import com.third.support.alidruid.sql.ast.SQLIndexDefinition;
import com.third.support.alidruid.sql.ast.SQLStatement;
import com.third.support.alidruid.sql.ast.expr.*;
import com.third.support.alidruid.sql.ast.statement.*;
import com.third.support.alidruid.sql.dialect.mysql.ast.MySqlKey;
import com.third.support.alidruid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.third.support.alidruid.sql.dialect.mysql.ast.MySqlUnique;
import com.third.support.alidruid.sql.dialect.mysql.ast.statement.*;
import com.third.support.alidruid.sql.parser.ParserException;
import com.third.support.alidruid.util.JdbcConstants;
import com.wukong.core.weblog.utils.DateUtil;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.store.TupleDate;
import com.zjht.unified.common.core.domain.ddl.TableCreateDDL;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import com.zjht.unified.common.core.domain.ddl.TblIndex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
public class MysqlDDLUtils {

    private static final Map<Class, String> valToJdbcType = new HashMap<>();

    private static final Map<String, String> javaToJdbcType = new HashMap<>();

    private static final Map<String, String> timeFmt=new HashMap<>();

    static {
        valToJdbcType.put(Integer.class, "int");
        valToJdbcType.put(int.class, "int");
        valToJdbcType.put(Long.class, "bigint");
        valToJdbcType.put(long.class, "bigint");
        valToJdbcType.put(Double.class, "double(32,8)");
        valToJdbcType.put(double.class, "double(32,8)");
        valToJdbcType.put(String.class, "varchar(255)");
        valToJdbcType.put(Date.class,"datetime");
    }

    static {
        javaToJdbcType.put("Integer", "int");
        javaToJdbcType.put("int", "int");
        javaToJdbcType.put("Long", "bigint");
        javaToJdbcType.put("long", "bigint");
        javaToJdbcType.put("Double", "double(32,8)");
        javaToJdbcType.put("double", "double(32,8)");
        javaToJdbcType.put("String", "varchar(255)");
        javaToJdbcType.put("Date","datetime");
    }

    public static void main(String[] args) {
        String insert="INSERT INTO `cockpit_config`.`config_cockpit`(`id`, `cockpit_code`, `cockpit_name`, `monitor_show`, `max_boards_num`, `bottom_menus`, `sort`, `create_time`, `update_time`, `status`, `create_user`, `update_user`, `monitor_ids`, `map_id`) VALUES (6, 'forestFire', '森林防火驾驶舱', 0, 5, 'desktop,alarm,task,message,log,address_book', 0.563343, '2023-12-08 09:44:40', '2023-12-13 12:09:21', 1, NULL, NULL, 'gb28181/192.168.18.94/00871486411310021513,gb28181/192.168.18.9/00871486411311415228,gb28181/192.168.18.138/00871486411311660000,gb28181/192.168.18.38/00871486411310496125,gb28181/192.168.18.130/00871486411313216198,gb28181/192.168.18.115/00871486411318595080', 2200000000000)";
        MySqlInsertStatement stmt = null;
        try {
            stmt = (MySqlInsertStatement) SQLUtils.parseStatements(insert, JdbcConstants.MYSQL).get(0);
        } catch (ParserException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println(stmt);

        String create="CREATE TABLE `x_ticket_2_hour` (\n" +
                "  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',\n" +
                "  `kount` int(11) DEFAULT NULL,\n" +
                "  `data_time` datetime DEFAULT NULL,\n" +
                "  `min_price` double(32,8) DEFAULT NULL,\n" +
                "  `max_price` double(32,8) DEFAULT NULL,\n" +
                "  `avg_price` double(32,8) DEFAULT NULL,\n" +
                "  `sum_price` double(32,8) DEFAULT NULL,\n" +
                "  `min_buy_number` double(32,8) DEFAULT NULL,\n" +
                "  `max_buy_number` double(32,8) DEFAULT NULL,\n" +
                "  `avg_buy_number` double(32,8) DEFAULT NULL,\n" +
                "  `sum_buy_number` double(32,8) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `data_time` (`data_time`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;";

        String delete="delete FROM `x_ticket_2_day` where id not in ('a','b')";
        SQLStatement stmt2 = null;
        try {
            stmt2 = SQLUtils.parseStatements(delete, JdbcConstants.ORACLE).get(0);
        } catch (ParserException e) {
            log.error(e.getMessage(), e);
        }
        System.out.println(stmt2.toString());
    }

    public static String deleteIn(String table,String pk, List<Object> valList,boolean reverse){
        MySqlDeleteStatement del=new MySqlDeleteStatement();
        del.setTableName(table);
        SQLInListExpr wherein=new SQLInListExpr();
        wherein.setNot(reverse);
        wherein.setExpr(new SQLIdentifierExpr(pk));
        List<SQLExpr> tLst = valList.stream().map(t -> AliDruidUtils.toSQLExpr(t)).collect(Collectors.toList());
        wherein.setTargetList(tLst);
        del.setWhere(wherein);
        return del.toString();
    }

    public static void setJdbcType(List<TblCol> colLst,Map<String, Object> actualData){
        for (Iterator<TblCol> iterator = colLst.iterator(); iterator.hasNext(); ) {
            TblCol col =  iterator.next();
            if(col.getJdbcType()!=null)
                continue;
            String jType = javaToJdbcType.get(col.getType());
            if(actualData!=null) {
                if (jType.equals("varchar(255)")) {
                    Object actualVal = actualData.get(col.getNameEn());
                    if (actualVal != null) {
                        if (actualVal.toString().length() > 65535) {
                            jType = "medium text";
                        } else if (actualVal.toString().length() > 255) {
                            jType = "text";
                        }
                    }
                }
            }
            col.setJdbcType(jType);
        }
    }

    public static TableCreateDDL createTable(String tbl, Map<String, Object> actualData, List<TblCol> preDefLst, List<TblIndex> indices, Boolean hasSdpRef) {
        List<TblCol> validColList = new ArrayList<>();
        if (CollectionUtils.isEmpty(preDefLst)) {
            validColList = createTblColFromData(actualData);
        }else{
            validColList.addAll(preDefLst);
        }

        setJdbcType(validColList,actualData);

        TblCol pkCol = null;
        Optional<TblCol> tmpPk = validColList.stream().filter(c -> c.getIsPK() == 1 || c.getNameEn().equalsIgnoreCase("id")).findFirst();
        if (tmpPk.isPresent()) {
            pkCol = tmpPk.get();
        } else {
            pkCol = new TblCol();
            pkCol.setNameEn("id");
            pkCol.setType("Long");
            pkCol.setJdbcType("bigint");
            pkCol.setNameZh("主键ID");
            pkCol.setIsPK(1);
        }

        TblCol dtCol = null;
        Optional<TblCol> tmpTs = validColList.stream().filter(c -> c.getNameEn().equalsIgnoreCase("data_time")).findFirst();
        if (!tmpTs.isPresent()) {
            dtCol = new TblCol();
            dtCol.setNameEn(FieldConstants.DATATIME);
            dtCol.setType("Date");
            dtCol.setJdbcType("datetime");
            dtCol.setNameZh("数据时间戳");
            Optional<TblCol> tmpTs2 = validColList.stream().filter(c -> c.getIsTempstamp()==1).findFirst();
            dtCol.setIsTempstamp(tmpTs2.isPresent()?1:0);
            validColList.add(dtCol);
        }

        // sdp ref columns
        if(hasSdpRef)
            addSdpReferenceColumns(validColList);


        MySqlCreateTableStatement create = new MySqlCreateTableStatement();
        create.setTableName(tbl);
        SQLColumnDefinition id = new SQLColumnDefinition();
        id.setDataType(new SQLDataTypeImpl(pkCol.getJdbcType()));
        id.setName(StrUtil.toUnderlineCase(pkCol.getNameEn()));
        id.setComment(pkCol.getNameZh());
        if (pkCol.getJdbcType().toLowerCase().contains("int"))
            id.setAutoIncrement(true);
        create.addColumn(id);

        MySqlPrimaryKey pk = new MySqlPrimaryKey();
        SQLIndexDefinition pkdef = pk.getIndexDefinition();
        pkdef.setType("primary");
        pkdef.setKey(true);
        pkdef.setColumns(new ArrayList<>());
        SQLSelectOrderByItem idcol = new SQLSelectOrderByItem();
        idcol.setExpr(new SQLIdentifierExpr(pkCol.getNameEn()));
        pkdef.getColumns().add(idcol);
        create.getTableElementList().add(pk);


        for (Iterator<TblCol> iterator = validColList.iterator(); iterator.hasNext(); ) {
            TblCol col = iterator.next();
            if (col == pkCol)
                continue;
            SQLColumnDefinition newCol = new SQLColumnDefinition();
            newCol.setName(StrUtil.toUnderlineCase(col.getNameEn()));
            newCol.setDataType(new SQLDataTypeImpl(col.getJdbcType()));
            newCol.setComment(col.getNameZh());
            create.addColumn(newCol);
        }

        if(!CollectionUtils.isEmpty(indices)) {
            for (Iterator<TblIndex> iterator = indices.iterator(); iterator.hasNext(); ) {
                TblIndex idx = iterator.next();
                MySqlKey key = new MySqlKey();
                if("UNIQUE".equalsIgnoreCase(idx.getIndexType())){
                    key=new MySqlUnique();
                }
                SQLIndexDefinition indexDef = key.getIndexDefinition();
                indexDef.setKey(true);
                indexDef.setName(new SQLIdentifierExpr(idx.getIndexName()));
                indexDef.setType(idx.getIndexType());
                for (Iterator<String> tblColIterator = idx.getFields().iterator(); tblColIterator.hasNext(); ) {
                    String col = tblColIterator.next();
                    SQLSelectOrderByItem c2 = new SQLSelectOrderByItem();
                    c2.setExpr(new SQLIdentifierExpr(StrUtil.toUnderlineCase(col)));
                    indexDef.getColumns().add(c2);
                }
                create.getTableElementList().add(key);
            }
        }

        return new TableCreateDDL(new ArrayList<>(Collections.singleton(create.toString())), validColList, pkCol, "mysql");
    }

    private static List<TblCol> createTblColFromData(Map<String, Object> data) {
        List<TblCol> rLst = data.entrySet().stream().map(e -> {
            TblCol col = new TblCol();
            col.setNameEn(e.getKey());
            col.setJdbcType(valToJdbcType.get(e.getValue().getClass()));
            return col;
        }).collect(Collectors.toList());
        return rLst;
    }

    public static void addSdpReferenceColumns(List<TblCol> validColList){
        Set<String> cSet = validColList.stream().map(c -> c.getNameEn()).collect(Collectors.toSet());

        if(!cSet.contains(FieldConstants.POINT_ID)) {
            TblCol pointCol = new TblCol();
            pointCol.setNameEn(FieldConstants.POINT_ID);
            pointCol.setType("Long");
            pointCol.setJdbcType("bigint");
            pointCol.setNameZh("点ID");
            pointCol.setIsTempstamp(0);
            validColList.add(pointCol);
        }

        if(!cSet.contains(FieldConstants.DEVICE_ID)) {
            TblCol devCol = new TblCol();
            devCol.setNameEn(FieldConstants.DEVICE_ID);
            devCol.setType("Long");
            devCol.setJdbcType("bigint");
            devCol.setNameZh("设备ID");
            devCol.setIsTempstamp(0);
            validColList.add(devCol);
        }

        if(!cSet.contains(FieldConstants.SYSTEM_ID)) {
            TblCol sysCol = new TblCol();
            sysCol.setNameEn(FieldConstants.SYSTEM_ID);
            sysCol.setType("Long");
            sysCol.setJdbcType("bigint");
            sysCol.setNameZh("系统ID");
            sysCol.setIsTempstamp(0);
            validColList.add(sysCol);
        }

        if(!cSet.contains(FieldConstants.PROJECT_ID)) {
            TblCol planCol = new TblCol();
            planCol.setNameEn(FieldConstants.PROJECT_ID);
            planCol.setType("Long");
            planCol.setJdbcType("bigint");
            planCol.setNameZh("采集项目ID");
            planCol.setIsTempstamp(0);
            validColList.add(planCol);
        }

        if(!cSet.contains(FieldConstants.DRIVER_ID)) {
            TblCol driverCol = new TblCol();
            driverCol.setNameEn(FieldConstants.DRIVER_ID);
            driverCol.setType("Long");
            driverCol.setJdbcType("bigint");
            driverCol.setNameZh("采集计划ID");
            driverCol.setIsTempstamp(0);
            validColList.add(driverCol);
        }
    }


    public static String insert(String tbl, Map<String, Object> actualData, List<TblCol> preDefLst) {
        Map<String, TblCol> colMap = preDefLst.stream().collect(Collectors.toMap(d -> d.getNameEn(), Function.identity()));
        MySqlInsertStatement insert = new MySqlInsertStatement();
        insert.setTableName(new SQLIdentifierExpr(StrUtil.toUnderlineCase(tbl)));
        SQLInsertStatement.ValuesClause vc = new SQLInsertStatement.ValuesClause();
        insert.addValueCause(vc);
        actualData.entrySet().stream().forEach(e -> {
            insert.addColumn(new SQLIdentifierExpr(StrUtil.toUnderlineCase(e.getKey())));
            TblCol colDef = colMap.get(e.getKey());
            Object val = e.getValue();
            if(colDef==null)
                System.out.println();
            if(colDef.getJdbcType().equals("datetime")&&val instanceof String){
                String k=tbl+"."+colDef.getNameEn();
                String dmt=timeFmt.get(k);
                if(dmt==null) {
                    final DateTimeParser dtp = new DateTimeParser().withDateResolutionMode(DateTimeParser.DateResolutionMode.MonthFirst).withLocale(Locale.ENGLISH);
                    dmt = dtp.determineFormatString(val.toString());
                    timeFmt.put(k,dmt);
                }
                val=DateUtil.parse(val.toString(),dmt);
                e.setValue(val);
            }
            vc.addValue(AliDruidUtils.convertTblColToSQLObject(val, colDef));
        });

        if(!actualData.containsKey("data_time")) {
            insert.addColumn(new SQLIdentifierExpr("data_time"));
            Optional<TblCol> tmpTs = preDefLst.stream().filter(c -> c.getIsTempstamp() == 1).findFirst();
            TblCol dtCol = new TblCol();
            dtCol.setNameEn("data_time");
            if (!tmpTs.isPresent()) {
                dtCol.setJdbcType("datetime");
                vc.addValue(AliDruidUtils.convertTblColToSQLObject(new Date(), dtCol));
            } else {
                String dtName = tmpTs.get().getNameEn();
                vc.addValue(AliDruidUtils.convertTblColToSQLObject(actualData.get(dtName), tmpTs.get()));
            }
        }
        return insert.toString();
    }



    public static String update(String tbl, Map<String, Object> actualData, List<TblCol> preDefLst) {
        MySqlUpdateStatement update=new MySqlUpdateStatement();
        update.setTableSource(new SQLExprTableSource(tbl));
        for (Iterator<TblCol> iterator = preDefLst.iterator(); iterator.hasNext(); ) {
            TblCol col =  iterator.next();
            if(col.getIsPK()==1){
                SQLBinaryOpExpr where=new SQLBinaryOpExpr();
                where.setOperator(SQLBinaryOperator.Equality);
                where.setLeft(new SQLIdentifierExpr(StrUtil.toUnderlineCase(col.getNameEn())));
                where.setRight(AliDruidUtils.convertTblColToSQLObject(actualData.get(col.getNameEn()),col));
                update.setWhere(where);
            }else{
                SQLUpdateSetItem us=new SQLUpdateSetItem();
                us.setColumn(new SQLIdentifierExpr(StrUtil.toUnderlineCase(col.getNameEn())));
                us.setValue(AliDruidUtils.convertTblColToSQLObject(actualData.get(col.getNameEn()),col));
                update.addItem(us);
            }
        }
        return update.toString();
    }

    public static List<TblCol> createDefaultTblDef(Map<String,Object> values){
        List<TblCol> defLst=new ArrayList<>();
        values.entrySet().stream().forEach(e->{
            TblCol tc=new TblCol();
            tc.setNameEn(StrUtil.toUnderlineCase(e.getKey()));
            if(e.getValue() instanceof Date || e.getValue() instanceof TupleDate){
                tc.setType("Date");
                tc.setJdbcType("datetime");
            }else if(e.getValue() instanceof Number){
                tc.setType("Double");
                tc.setJdbcType("double");
            }else{
                tc.setType("String");
                tc.setJdbcType("varchar(255)");
            }
            defLst.add(tc);
        });
        return defLst;
    }
}
