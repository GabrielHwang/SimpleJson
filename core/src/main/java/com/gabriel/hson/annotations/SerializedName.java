package com.gabriel.hson.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface SerializedName {
    String value();
    String[] alternate() default {};
}
