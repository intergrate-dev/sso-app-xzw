package com.founder.sso.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})  
@Retention(RetentionPolicy.RUNTIME)  
@Constraint(validatedBy = NotNullBothValidator.class)  
@Documented 
public @interface NotNullBoth {
	String message() default "{两个字段不能同时为null}";  
    Class<?>[] groups() default {};  
    Class<? extends Payload>[] payload() default {};   
}
