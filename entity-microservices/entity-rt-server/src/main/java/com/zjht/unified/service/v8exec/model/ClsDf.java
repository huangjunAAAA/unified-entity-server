package com.zjht.unified.service.v8exec.model;

import com.zjht.unified.domain.composite.ClazzDefCompositeDO;
import com.zjht.unified.domain.composite.FieldDefCompositeDO;
import com.zjht.unified.domain.composite.MethodDefCompositeDO;
import com.zjht.unified.domain.simple.ClazzDefDO;
import com.zjht.unified.service.ctx.TaskContext;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClsDf extends ClazzDefDO {
    private List<Object> fieldDfs;
    private List<MethodDf> methodDfs;

    /**
     * 将ClazzDefCompositeDO转成ClsDf，对应的属性都用beanutil.copy转过来
     * @param cls ClazzDefCompositeDO对象
     * @return 转换后的ClsDf对象
     */
    public static ClsDf from(ClazzDefCompositeDO cls, TaskContext ctx) {
        ClsDf clsDf = new ClsDf();

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


