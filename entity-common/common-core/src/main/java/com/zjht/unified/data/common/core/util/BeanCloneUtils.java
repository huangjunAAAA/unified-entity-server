package com.zjht.unified.data.common.core.util;

import lombok.extern.log4j.Log4j;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Log4j
public class BeanCloneUtils extends PropertyUtilsBean {

    public static <T> T clone(T source) {
        return (T) cloneProperties(source,newInstance(source.getClass()));
    }

    public static  <T> T cloneProperties(T source, T desc, String... ignoreProperties){
        try {
            BeanCloneUtils utils = new BeanCloneUtils();
            if(ignoreProperties!=null&&ignoreProperties.length>0){
                utils.ignoreProperties= Arrays.asList(ignoreProperties);
            }
            utils.copyProperties(desc, source);
            return desc;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    private List<String> ignoreProperties;

    @Override
    public void setSimpleProperty(Object bean, String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if(ignoreProperties!=null&&ignoreProperties.contains(name))
            return;
        if(value==null||value.getClass().isPrimitive()||value instanceof String)
            super.setSimpleProperty(bean, name, value);
        else{
            if(value instanceof Collection){
                Collection cloned = (Collection) newInstance(value.getClass());
                for (Iterator iterator = ((Collection)value).iterator(); iterator.hasNext(); ) {
                    Object sub =  iterator.next();
                    cloned.add(clone(sub));
                }
                super.setSimpleProperty(bean,name,cloned);
            }else{
                super.setSimpleProperty(bean,name,clone(value));
            }
        }
    }

    private static Object newInstance(Class cls){
        try{
            return cls.newInstance();
        }catch (Exception e){

        }
        Constructor<?>[] ccLst = cls.getConstructors();
        for (int i = 0; i < ccLst.length; i++) {
            Constructor<?> cc = ccLst[i];
            Object[] args=new Object[cc.getParameterCount()];
            try{
                cc.setAccessible(true);
                return cc.newInstance(args);
            }catch (Exception e){

            }
        }
        throw new RuntimeException(cls+" cannot be auto constructed");
    }


}
