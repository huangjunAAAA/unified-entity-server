package com.zjht.unified.data.common.core.util;

import cn.hutool.core.util.StrUtil;
import com.third.support.alidruid.DbType;
import com.third.support.alidruid.sql.ast.*;
import com.third.support.alidruid.sql.ast.expr.*;
import com.third.support.alidruid.sql.ast.statement.*;
import com.third.support.alidruid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.wukong.core.weblog.utils.DateUtil;

import com.zjht.unified.data.common.core.constants.Constants;
import com.zjht.unified.data.common.core.constants.FieldConstants;
import com.zjht.unified.data.common.core.domain.dto.BaseQueryDTO;
import com.zjht.unified.data.common.core.domain.dto.StatQueryDTO;
import com.zjht.unified.data.common.core.domain.dto.TimeRangeQuery;
import com.zjht.unified.data.common.core.domain.store.ValueExpr;
import com.zjht.unified.data.common.core.domain.ddl.TblCol;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;

import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AliDruidUtils {
    public static SQLExpr toSQLExpr(Object v){
        if(v==null){
            return new SQLNullExpr();
        }
        if(v instanceof Integer){
            return new SQLIntegerExpr((Integer)v);
        }
        if(v instanceof Long){
            return new SQLIntegerExpr((Long)v);
        }
        if(v instanceof Double){
            return new SQLNumberExpr((Number)v);
        }

        if(v instanceof Float){
            return new SQLNumberExpr((Number)v);
        }

        return new SQLCharExpr(v.toString());
    }

    public static SQLBinaryOpExpr createBinaryOp(String k,Object v){
        return createBinaryOp(k,v,SQLBinaryOperator.Equality);
    }

    public static void appendTimeRange(MySqlSelectQueryBlock sQuery,Date dt,boolean greater){
        if(dt==null)
            return;
        SQLBinaryOpExpr timeWhere = null;
        if(greater){
            timeWhere = AliDruidUtils.createBinaryOp(FieldConstants.DATATIME, dt,SQLBinaryOperator.GreaterThanOrEqual);
        }else{
            timeWhere = AliDruidUtils.createBinaryOp(FieldConstants.DATATIME, dt,SQLBinaryOperator.LessThanOrEqual);
        }
        if(sQuery==null){
            sQuery.setWhere(timeWhere);
        }else{
            AliDruidUtils.combineBinaryOp(sQuery.getWhere(),SQLBinaryOperator.BooleanAnd,timeWhere);
        }
    }

    public static void addGroupBy(MySqlSelectQueryBlock sQuery, List<String> groupByList){
        SQLSelectGroupByClause gc=sQuery.getGroupBy();
        if(gc==null){
            gc=new SQLSelectGroupByClause();
            sQuery.setGroupBy(gc);
        }
        for (Iterator<String> iterator = groupByList.iterator(); iterator.hasNext(); ) {
            String gy =  iterator.next();
            gc.addItem(new SQLIdentifierExpr(StrUtil.toUnderlineCase(gy)));
        }

    }

    public static void addGroupBy(MySqlSelectQueryBlock sQuery, String gy){
        SQLSelectGroupByClause gc=sQuery.getGroupBy();
        if(gc==null){
            gc=new SQLSelectGroupByClause();
            sQuery.setGroupBy(gc);
        }

        gc.addItem(new SQLIdentifierExpr(StrUtil.toUnderlineCase(gy)));
    }

    public static void addGroupBy(MySqlSelectQueryBlock sQuery, SQLExpr expr){
        SQLSelectGroupByClause gc=sQuery.getGroupBy();
        if(gc==null){
            gc=new SQLSelectGroupByClause();
            sQuery.setGroupBy(gc);
        }
        gc.addItem(expr);
    }

    public static void addAggrExpr(MySqlSelectQueryBlock sQuery, String method, String colName,String alias){
        SQLAggregateExpr aggr = new SQLAggregateExpr(method,null,colName==null||colName.equals("*")?new SQLAllColumnExpr():new SQLIdentifierExpr(colName));
        sQuery.addSelectItem(new SQLSelectItem(aggr,alias));
    }

    public static SQLMethodInvokeExpr addDATEFORMAT(MySqlSelectQueryBlock sQuery, String colName,String dmt, String alias){
        SQLMethodInvokeExpr invoke = getDATEFORMAT(colName, dmt);
        sQuery.addSelectItem(new SQLSelectItem(invoke,alias));
        return invoke;
    }

    public static SQLMethodInvokeExpr getDATEFORMAT(String colName,String dmt){
        SQLMethodInvokeExpr invoke=new SQLMethodInvokeExpr();
        invoke.setMethodName("DATE_FORMAT");
        invoke.addArgument(new SQLIdentifierExpr(colName));
        invoke.addArgument(new SQLCharExpr(dmt));
        return invoke;
    }

    public static void setOrderByAndLimit(MySqlSelectQueryBlock sQuery,int page, int size,String orderby,String asc){
        SQLLimit limit = new SQLLimit();
        limit.setOffset(page * size);
        limit.setRowCount(size);
        sQuery.setLimit(limit);

        if(StringUtils.isNotEmpty(orderby)){
            SQLOrderBy orderBy=new SQLOrderBy();
            if(StringUtils.isEmpty(asc)){
                asc="ASC";
            }
            orderBy.addItem(new SQLSelectOrderByItem(new SQLIdentifierExpr(StrUtil.toUnderlineCase(orderby)), SQLOrderingSpecification.valueOf(asc.toUpperCase())));
        }
    }

    public static SQLBinaryOpExpr createBinaryOp(String k,Object v,SQLBinaryOperator op){
        SQLBinaryOpExpr initial=new SQLBinaryOpExpr();
        initial.setLeft(new SQLIdentifierExpr(StrUtil.toUnderlineCase(k)));
        initial.setRight(toSQLExpr(v));
        initial.setOperator(op);
        return initial;
    }

    public static SQLBinaryOpExpr createBinaryOp(String k,Object v,SQLBinaryOperator op,SQLExpr another){
        SQLBinaryOpExpr left = createBinaryOp(k, v);
        return combineBinaryOp(left,op,another);
    }

    public static SQLBinaryOpExpr combineBinaryOp(SQLExpr left,SQLBinaryOperator op, SQLExpr right){
        SQLBinaryOpExpr together=new SQLBinaryOpExpr();
        together.setOperator(op);
        together.setLeft(left);
        together.setRight(right);
        return together;
    }

    public static SQLExpr convertTblColToSQLObject(Object val, TblCol def) {
        if(val==null){
            return new SQLNullExpr();
        }

        if(def.getJdbcType().contains("bigint")){
            return new SQLIntegerExpr(Long.parseLong(val.toString()));
        }

        if(def.getJdbcType().contains("int")){
            return new SQLIntegerExpr(Integer.parseInt(val.toString()));
        }

        if(def.getJdbcType().contains("double")){
            return new SQLNumberExpr(Double.parseDouble(val.toString()));
        }

        if(def.getJdbcType().contains("float")){
            return new SQLNumberExpr(Double.parseDouble(val.toString()));
        }

        if(def.getJdbcType().contains("varchar") || def.getJdbcType().contains("text")){
            return new SQLCharExpr(val.toString());
        }

        if(def.getJdbcType().contains("datetime")){
            if(val instanceof Date)
                return new SQLCharExpr(DateUtil.formatDateTime((Date) val));
            else{
                return new SQLCharExpr(val.toString());
            }
        }

        throw new RuntimeException("unknown jdbc type:"+def.getJdbcType());
    }

    private static final List<String> sdpFields=new ArrayList<>(Arrays.asList(FieldConstants.DATATIME,FieldConstants.DEVICE_ID,FieldConstants.DRIVER_ID,FieldConstants.PROJECT_ID,FieldConstants.POINT_ID,FieldConstants.SYSTEM_ID));

    public static MySqlSelectQueryBlock createObjectAggrSQL(List<String> valLst,List<String> gyLst, String tbl,String timeslice) {
        MySqlSelectQueryBlock sQuery = new MySqlSelectQueryBlock();
        valLst.stream().map(v -> StrUtil.toUnderlineCase(v)).forEach(vcl -> {
            AliDruidUtils.addAggrExpr(sQuery, "max", vcl, "max_" + vcl);
            AliDruidUtils.addAggrExpr(sQuery, "min", vcl, "min_" + vcl);
            AliDruidUtils.addAggrExpr(sQuery, "avg", vcl, "avg_" + vcl);
            AliDruidUtils.addAggrExpr(sQuery, "sum", vcl, "sum_" + vcl);
        });
        AliDruidUtils.addAggrExpr(sQuery,"count",null,FieldConstants.DATACOUNT);
        gyLst.stream().map(v -> StrUtil.toUnderlineCase(v)).forEach(vcl -> {
            sQuery.addSelectItem(new SQLSelectItem(new SQLIdentifierExpr(vcl)));
        });

        AliDruidUtils.addGroupBy(sQuery, gyLst);
        if(StringUtils.isNotEmpty(timeslice)) {
            String timePattern= Constants.TIME_FORMAT.get(Constants.TIME_SUFFIX.indexOf(timeslice));
            AliDruidUtils.addDATEFORMAT(sQuery, FieldConstants.DATATIME, timePattern, FieldConstants.DATATIME);
            SQLMethodInvokeExpr invoke=getDATEFORMAT(FieldConstants.DATATIME,timePattern);
            addGroupBy(sQuery,invoke);
        }
        sQuery.setFrom(new SQLExprTableSource(tbl));
        return sQuery;
    }








    public static MySqlSelectQueryBlock createQueryFromTable(String tbl, Map<String,Object> fullCondition){
        MySqlSelectQueryBlock sQuery=new MySqlSelectQueryBlock();
        if(StringUtils.isNotBlank(tbl))
            sQuery.setFrom(new SQLExprTableSource(tbl));
        for (Iterator<Map.Entry<String, Object>> iterator = fullCondition.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> condi =  iterator.next();
            if(sQuery.getWhere()==null){
                SQLBinaryOpExpr b = AliDruidUtils.createBinaryOp(condi.getKey(), condi.getValue());
                sQuery.setWhere(b);
            }else{
                SQLBinaryOpExpr r = AliDruidUtils.createBinaryOp(condi.getKey(), condi.getValue(), SQLBinaryOperator.BooleanAnd, sQuery.getWhere());
                sQuery.setWhere(r);
            }
        }
        return sQuery;
    }

    public static MySqlSelectQueryBlock createGeneralValueSQLFromTable(String tbl, List<String> cols, Map<String,Object> fullCondition){
        MySqlSelectQueryBlock sQuery = createQueryFromTable(tbl, fullCondition);
        for (Iterator<String> iterator = cols.iterator(); iterator.hasNext(); ) {
            String c =  iterator.next();
            sQuery.addSelectItem(new SQLIdentifierExpr(c));
        }
        return sQuery;
    }



}
