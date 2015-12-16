package com.udf.UI.annotation;

import com.udf.UI.FormType;

import java.lang.annotation.*;

/**
 * Created by 张未然 on 2015/12/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
public @interface FormBuilder {
    String name() default "";
    FormType type() default FormType.singleLine;
    int fieldPerLine() default 2;
}
