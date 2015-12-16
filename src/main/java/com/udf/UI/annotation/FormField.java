package com.udf.UI.annotation;

import java.lang.annotation.*;

/**
 * Created by 张未然 on 2015/12/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface FormField {
    String name() default "";

}
