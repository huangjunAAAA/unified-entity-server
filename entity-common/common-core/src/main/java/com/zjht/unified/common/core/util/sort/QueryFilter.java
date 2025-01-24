package com.zjht.unified.common.core.util.sort;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Objects;


public class QueryFilter<T> {
    private T target;
    private T cond;
    private String[] likes;

    public QueryFilter(T target, T cond, String... likes) {
        this.target = target;
        this.cond = cond;
        this.likes = likes;
    }

    public boolean isEqual() {
        if (target == null || cond == null)
            return false;
        Field[] all = FieldUtils.getAllFields(target.getClass());
        for (int i = 0; i < all.length; i++)
            try {
                Field field = all[i];
                field.setAccessible(true);
                Object v1 = FieldUtils.readField(field, cond);
                if(v1==null)
                    continue;
                Object v2 = FieldUtils.readField(field, target);
                if(v2==null)
                    return false;
                if(isLike(field.getName())){
                    if(!v2.toString().contains(v1.toString())){
                        return false;
                    };
                }else{
                    if(!Objects.equals(v1,v2)){
                        return false;
                    }
                }
            } catch (Exception e) {
                return false;
            }
        return true;
    }

    private boolean isLike(String fieldName) {
        if (likes == null)
            return false;
        for (int i = 0; i < likes.length; i++) {
            String like = likes[i];
            if (like.equals(fieldName))
                return true;
        }
        return false;
    }
}
