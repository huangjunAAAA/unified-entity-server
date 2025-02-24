package com.zjht.unified.jsengine.v8.annotation;



import com.zjht.unified.jsengine.v8.enums.BusinessType;

import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 * 
 * @author zjht
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 模块
     */
    public String title() default "";

    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER;

}
