package com.zjht.unified.common.core.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.template.TemplateException;
import com.zjht.unified.common.core.domain.ddl.*;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.constants.KafkaNames;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import sun.plugin2.message.ShowDocumentMessage;

import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class DorisDDLUtils {

    private static final Map<String, String> javaToJdbcType = new HashMap<>();

    static {
        javaToJdbcType.put("Integer", "INT");
        javaToJdbcType.put("int", "INT");
        javaToJdbcType.put("Long", "BIGINT");
        javaToJdbcType.put("long", "BIGINT");
        javaToJdbcType.put("Double", "DOUBLE");
        javaToJdbcType.put("double", "DOUBLE");
        javaToJdbcType.put("String", "varchar(255)");
        javaToJdbcType.put("Date", "datetime");
    }

    public static void setJdbcType(List<TblCol> colLst, Map<String, Object> actualData){
        for (Iterator<TblCol> iterator = colLst.iterator(); iterator.hasNext(); ) {
            TblCol col = iterator.next();
            if (col.getJdbcType() != null)
                continue;
            String jType = javaToJdbcType.get(col.getType());
            if(actualData!=null) {
                if (jType.equals("varchar(255)")) {
                    Object actualVal = actualData.get(col.getNameEn());
                    if (actualVal != null && actualVal.toString().length() > 255) {
                        jType = "STRING";
                    }
                }
            }
            col.setJdbcType(jType);
        }
    }

    public static TableCreateDDL createTable(String tbl, Map<String, Object> actualData, List<TblCol> preDefLst, List<TblIndex> indices, Map<String, String> settings, Template createTbl, Boolean requireID) {

        TblDef table = new TblDef();
        table.setName(tbl);
        table.setIndices(indices);
        List<String> colsIdx = new ArrayList<>();
        if(indices!=null) {
            Set<String> idxFields = indices.stream().flatMap(idx -> idx.getFields().stream()).collect(Collectors.toSet());
            colsIdx.addAll(idxFields);
        }

        List<TblCol> validColList = new ArrayList<>(preDefLst);
        setJdbcType(validColList,actualData);
        addSdpReferenceColumns(validColList);
        table.setCols(validColList);

        Optional<TblCol> tmpTs = validColList.stream().filter(c -> c.getNameEn().equalsIgnoreCase("data_time")).findFirst();
        if (!tmpTs.isPresent()) {
            TblCol dtCol = new TblCol();
            dtCol.setNameEn(FieldConstants.DATATIME);
            dtCol.setType("Date");
            dtCol.setJdbcType("datetime");
            dtCol.setNameZh("数据时间戳");
            Optional<TblCol> tmpTs2 = validColList.stream().filter(c -> c.getIsTempstamp()==1).findFirst();
            dtCol.setIsTempstamp(tmpTs2.isPresent()?0:1);
            validColList.add(dtCol);
        }
        if(requireID) {
            Optional<TblCol> tmpId = validColList.stream().filter(c -> c.getNameEn().equalsIgnoreCase("id")).findFirst();
            if (!tmpId.isPresent()) {
                TblCol idPk = new TblCol();
                idPk.setNameEn("id");
                idPk.setType("Long");
                idPk.setJdbcType("bigint");
                idPk.setNameZh("主键ID");
                idPk.setIsTempstamp(0);
                validColList.add(idPk);
            }
        }

        



        table.setHashCol(FieldConstants.DATATIME);
        table.setPartitionCol(FieldConstants.DATATIME);
        if (settings != null)
            table.setProperties(settings);
        else
            table.setProperties(new HashMap<>());

        try {
            Map<String, Object> variable = new HashMap<>();
            variable.put("table", table);
            variable.put("sutil", new StrUtil());
            variable.put("singleIdx", colsIdx);
            StringWriter fw = new StringWriter();
            createTbl.process(variable, fw);


            TableCreateDDL result = new TableCreateDDL();
            result.setCols(validColList);
            result.setDbType("doris");
            result.getDdl().add(fw.toString());

            return result;
        } catch (cn.hutool.extra.template.TemplateException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static void addSdpReferenceColumns(List<TblCol> validColList){
        Set<String> cSet = validColList.stream().map(c -> c.getNameEn()).collect(Collectors.toSet());

        if(!cSet.contains(FieldConstants.PROJECT_ID)) {
            TblCol planCol = new TblCol();
            planCol.setNameEn(FieldConstants.PROJECT_ID);
            planCol.setType("Long");
            planCol.setJdbcType("bigint");
            planCol.setNameZh("项目ID");
            planCol.setIsTempstamp(0);
            validColList.add(planCol);
        }


        if(!cSet.contains(FieldConstants.CLAZZ_GUID)) {
            TblCol planCol = new TblCol();
            planCol.setNameEn(FieldConstants.CLAZZ_GUID);
            planCol.setType("String");
            planCol.setJdbcType("varchar(255)");
            planCol.setNameZh("类guid");
            planCol.setIsTempstamp(0);
            validColList.add(planCol);
        }

    }

    public static String createStreamLoadPipe(String tbl, List<String> kafkaServers, Template template) {
        PipeDef pd = new PipeDef();
        pd.setName(KafkaNames.DORIS_LOAD_PRIFIX + tbl);
        pd.setTbl(tbl);
        pd.setTopic(KafkaNames.DORIS_TOPIC_PRIFIX + tbl);
        String blst = StrUtil.join(",", kafkaServers.iterator());
        pd.setBrokerList(blst);
        pd.setGroupId(tbl);


        try {
            Map<String, Object> variable = new HashMap<>();
            variable.put("pipe", pd);
            StringWriter fw = new StringWriter();
            template.process(variable, fw);
            return fw.toString();
        } catch (TemplateException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}
