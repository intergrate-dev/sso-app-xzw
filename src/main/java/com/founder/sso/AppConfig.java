package com.founder.sso;

import javax.servlet.Filter;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.google.common.collect.Sets;

/**
 * redis配置采用基于xml形式，以便在集群和单机模式间切换
 */
@Configuration
@ImportResource(locations={"classpath:redis-bean.xml"})
public class AppConfig {
	
	@Bean
	@Conditional(SignFilterDisableCondition.class)
	public SignFilter signFilter(){
		return new SignFilter();
	}
	
	@Bean
	@Conditional(SignFilterDisableCondition.class)
    public FilterRegistrationBean signFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        SignFilter signFilter = signFilter();
        registrationBean.setFilter(signFilter);
        registrationBean.setUrlPatterns(Sets.newHashSet("/api/*"));
        registrationBean.setOrder(Integer.MAX_VALUE);
        return registrationBean;
    }
    
    @Bean
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }
}
