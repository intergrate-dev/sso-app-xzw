package com.founder.sso;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 判断环境里是否定义了signFilter.disable=true属性，
 * 定义了则禁用SignFilter
 * @author huyong
 *
 */
public class SignFilterDisableCondition implements Condition {  
    @Override
    public boolean matches(
    		ConditionContext context, AnnotatedTypeMetadata metadata) {
    
    	Environment env = context.getEnvironment();
    	return !env.containsProperty("signFilter.disable");
    }
}

