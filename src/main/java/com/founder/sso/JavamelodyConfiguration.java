package com.founder.sso;

import net.bull.javamelody.MonitoringFilter;
import net.bull.javamelody.SessionListener;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yuan-pc on 2019/2/22.
 */

@Configuration
public class JavamelodyConfiguration {

    @Bean
    public FilterRegistrationBean monitorFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new MonitoringFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }

    @Bean
    public ServletListenerRegistrationBean sessionListener(){
        ServletListenerRegistrationBean servletListenerRegistrationBean = new ServletListenerRegistrationBean();
        servletListenerRegistrationBean.setListener(new SessionListener());
        return servletListenerRegistrationBean;
    }

}