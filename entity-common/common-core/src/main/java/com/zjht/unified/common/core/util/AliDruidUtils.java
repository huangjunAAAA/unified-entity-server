package com.zjht.unified.common.core.util;

import cn.hutool.core.util.StrUtil;
import com.third.support.alidruid.sql.ast.*;
import com.third.support.alidruid.sql.ast.expr.*;
import com.third.support.alidruid.sql.ast.statement.*;
import com.third.support.alidruid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.wukong.core.weblog.utils.DateUtil;

import com.zjht.unified.common.core.constants.Constants;
import com.zjht.unified.common.core.constants.FieldConstants;
import com.zjht.unified.common.core.domain.ddl.TblCol;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AliDruidUtils {

    /**
     * 将Java对象转换为SQL表达式。
     *
     * @param v 要转换的Java对象
     * @return 对应的SQL表达式
     */
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

    /**
     * 创建一个二元操作表达式，默认使用等于运算符。
     *
     * @param k 键名
     * @param v 值
     * @return 二元操作表达式
     */
    public static SQLBinaryOpExpr createBinaryOp(String k,Object v){
        return createBinaryOp(k,v,SQLBinaryOperator.Equality);
    }

    /**
     * 向查询中添加时间范围条件。
     *
     * @param sQuery 查询块
     * @param dt 时间值
     * @param greater 是否大于等于时间值
     */
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

    /**
     * 向查询中添加分组字段列表。
     *
     * @param sQuery 查询块
     * @param groupByList 分组字段列表
     */
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

    /**
     * 向查询中添加单个分组字段。
     *
     * @param sQuery 查询块
     * @param gy 分组字段
     */
    public static void addGroupBy(MySqlSelectQueryBlock sQuery, String gy){
        SQLSelectGroupByClause gc=sQuery.getGroupBy();
        if(gc==null){
            gc=new SQLSelectGroupByClause();
            sQuery.setGroupBy(gc);
        }

        gc.addItem(new SQLIdentifierExpr(StrUtil.toUnderlineCase(gy)));
    }

    /**
     * 向查询中添加自定义的分组表达式。
     *
     * @param sQuery 查询块
     * @param expr 分组表达式
     */
    public static void addGroupBy(MySqlSelectQueryBlock sQuery, SQLExpr expr){
        SQLSelectGroupByClause gc=sQuery.getGroupBy();
        if(gc==null){
            gc=new SQLSelectGroupByClause();
            sQuery.setGroupBy(gc);
        }
        gc.addItem(expr);
    }

    /**
     * 向查询中添加聚合函数表达式。
     *
     * @param sQuery 查询块
     * @param method 聚合函数名称（如 max、min、avg、sum 等）
     * @param colName 列名
     * @param alias 别名
     */
    public static void addAggrExpr(MySqlSelectQueryBlock sQuery, String method, String colName,String alias){
        SQLAggregateExpr aggr = new SQLAggregateExpr(method,null,colName==null||colName.equals("*")?new SQLAllColumnExpr():new SQLIdentifierExpr(colName));
        sQuery.addSelectItem(new SQLSelectItem(aggr,alias));
    }

    /**
     * 向查询中添加日期格式化函数，并返回生成的表达式。
     *
     * @param sQuery 查询块
     * @param colName 列名
     * @param dmt 日期格式模式
     * @param alias 别名
     * @return 日期格式化表达式
     */
    public static SQLMethodInvokeExpr addDATEFORMAT(MySqlSelectQueryBlock sQuery, String colName,String dmt, String alias){
        SQLMethodInvokeExpr invoke = getDATEFORMAT(colName, dmt);
        sQuery.addSelectItem(new SQLSelectItem(invoke,alias));
        return invoke;
    }

    /**
     * 创建日期格式化函数表达式。
     *
     * @param colName 列名
     * @param dmt 日期格式模式
     * @return 日期格式化表达式
     */
    public static SQLMethodInvokeExpr getDATEFORMAT(String colName,String dmt){
        SQLMethodInvokeExpr invoke=new SQLMethodInvokeExpr();
        invoke.setMethodName("DATE_FORMAT");
        invoke.addArgument(new SQLIdentifierExpr(colName));
        invoke.addArgument(new SQLCharExpr(dmt));
        return invoke;
    }

    /**
     * 设置查询的排序和分页信息。
     *
     * @param sQuery 查询块
     * @param page 当前页码
     * @param size 每页大小
     * @param orderby 排序列
     * @param asc 排序方式（ASC 或 DESC）
     */
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

    /**
     * 创建一个二元操作表达式。
     *
     * @param k 键名
     * @param v 值
     * @param op 运算符
     * @return 二元操作表达式
     */
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
    /**
     * 组合两个二元操作表达式。
     *
     * @param left 左边的表达式
     * @param op 运算符
     * @param right 右边的表达式
     * @return 组合后的二元操作表达式
     */
    public static SQLBinaryOpExpr combineBinaryOp(SQLExpr left,SQLBinaryOperator op, SQLExpr right){
        SQLBinaryOpExpr together=new SQLBinaryOpExpr();
        together.setOperator(op);
        together.setLeft(left);
        together.setRight(right);
        return together;
    }

    /**
     * 将表列值转换为SQL对象。
     *
     * @param val 表列值
     * @param def 列定义
     * @return 对应的SQL表达式
     */
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

    /**
     * 创建基于表的对象聚合查询。
     *
     * @param valLst 聚合列列表
     * @param gyLst 分组列列表
     * @param tbl 表名
     * @param timeslice 时间切片
     * @return 查询块
     */
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

    /**
     * 根据表和条件创建查询。
     *
     * @param tbl 表名
     * @param fullCondition 条件映射
     * @return 查询块
     */
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

    /**
     * 创建通用值查询。
     *
     * @param tbl 表名
     * @param cols 列列表
     * @param fullCondition 条件映射
     * @return 查询块
     */
    public static MySqlSelectQueryBlock createGeneralValueSQLFromTable(String tbl, List<String> cols, Map<String,Object> fullCondition){
        MySqlSelectQueryBlock sQuery = createQueryFromTable(tbl, fullCondition);
        for (Iterator<String> iterator = cols.iterator(); iterator.hasNext(); ) {
            String c =  iterator.next();
            sQuery.addSelectItem(new SQLIdentifierExpr(c));
        }
        return sQuery;
    }



}
