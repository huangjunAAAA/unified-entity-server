package com.zjht.unified.service.v8exec.model;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.values.V8Value;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.simple.ClazzDefDO;
import com.zjht.unified.jsengine.v8.utils.V8BeanUtils;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
public class ClsDf extends ClazzDefDO {
    private List<Object> fieldDfs;
    private List<MethodDf> methodDfs;
    @JsonIgnore
    private TaskContext taskContext;

    public ClsDf(TaskContext ctx) {
        this.taskContext = ctx;
    }

    @V8Function(name = "getField")
    public V8Value getField(V8Value fieldName){
        for (Iterator<Object> iterator = fieldDfs.iterator(); iterator.hasNext(); ) {
            Object f =  iterator.next();
            if(f instanceof FieldDf && ((FieldDf)f).getName().equals(fieldName.toString())){
                try{
                    return V8BeanUtils.toV8Value(fieldName.getV8Runtime(),f);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                    return fieldName.getV8Runtime().createV8ValueUndefined();
                }
            }
            if(f instanceof NNRel && ((NNRel)f).getFrom().getName().equals(fieldName.toString())){
                try{
                    return V8BeanUtils.toV8Value(fieldName.getV8Runtime(),f);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                    return fieldName.getV8Runtime().createV8ValueUndefined();
                }
            }
        }
        return fieldName.getV8Runtime().createV8ValueUndefined();
    }

    @V8Function(name = "getMethod")
    public V8Value getMethod(V8Value fieldName){
        for (Iterator<MethodDf> iterator = methodDfs.iterator(); iterator.hasNext(); ) {
            MethodDf m =  iterator.next();
            if(m.getName().equals(fieldName.toString())){
                try{
                    return V8BeanUtils.toV8Value(fieldName.getV8Runtime(),m);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                    return fieldName.getV8Runtime().createV8ValueUndefined();
                }
            }
        }
        return fieldName.getV8Runtime().createV8ValueUndefined();
    }

    /**
     * 将ClazzDefCompositeDO转成ClsDf，对应的属性都用beanutil.copy转过来
     * @param cls ClazzDefCompositeDO对象
     * @return 转换后的ClsDf对象
     */
    public static ClsDf from(ClazzDefCompositeDO cls, TaskContext ctx) {
        ClsDf clsDf = new ClsDf(ctx);

        // 使用BeanUtils.copyProperties将ClazzDefCompositeDO的属性复制到ClsDf中
        BeanUtils.copyProperties(cls, clsDf);

        // 处理ClazzDefCompositeDO中的字段列表
        if (cls.getClazzIdFieldDefList() != null) {
            List<Object> fieldDfList = new ArrayList<>();
            for (FieldDefCompositeDO fieldDefCompositeDO : cls.getClazzIdFieldDefList()) {
                if(fieldDefCompositeDO.getClsRelIdClsRelation()!=null){
                    fieldDfList.add(NNRel.from(fieldDefCompositeDO.getClsRelIdClsRelation(),ctx));
                }else {
                    FieldDf fieldDf = new FieldDf();
                    BeanUtils.copyProperties(fieldDefCompositeDO, fieldDf);
                    fieldDfList.add(fieldDf);
                }
            }
            clsDf.setFieldDfs(fieldDfList);
        }

        // 处理ClazzDefCompositeDO中的方法列表
        if (cls.getClazzIdMethodDefList() != null) {
            List<MethodDf> methodDfList = new ArrayList<>();
            for (MethodDefCompositeDO methodDefCompositeDO : cls.getClazzIdMethodDefList()) {
                MethodDf methodDf = new MethodDf();
                BeanUtils.copyProperties(methodDefCompositeDO, methodDf);
                methodDfList.add(methodDf);
            }
            clsDf.setMethodDfs(methodDfList);
        }

        return clsDf;
    }
}


