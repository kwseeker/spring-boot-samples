package top.kwseeker.bugfix.demo1.annotation;

import top.kwseeker.bugfix.demo1.ConditionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActivityCondition {

    int id() default 0;

    ConditionType condType() default ConditionType.DEFAULT;
}

