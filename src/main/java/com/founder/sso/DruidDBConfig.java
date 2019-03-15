package com.founder.sso;

import java.util.Collections;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.pool.DruidDataSource;
import com.founder.sso.redis.DatabaseConfigurationProperties;

@Configuration  
public class DruidDBConfig {
	
	@Autowired 
	DatabaseConfigurationProperties databaseConfigurationProperties;
      
    @Bean     //声明其为Bean实例  
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource  
    public DataSource dataSource(){  
        DruidDataSource datasource = new DruidDataSource();  
          
        datasource.setUrl(databaseConfigurationProperties.getUrl());  
        datasource.setUsername(databaseConfigurationProperties.getUsername());  
        datasource.setPassword(databaseConfigurationProperties.getPassword());  
        datasource.setDriverClassName(databaseConfigurationProperties.getDriverClassName());  

        String connectionInitSqls = "SET NAMES utf8mb4";
        StringTokenizer tokenizer = new StringTokenizer(connectionInitSqls, ";");
        datasource.setConnectionInitSqls(Collections.list(tokenizer));
          
        //configuration  
        /*datasource.setInitialSize(initialSize);  
        datasource.setMinIdle(minIdle);  
        datasource.setMaxActive(maxActive);  
        datasource.setMaxWait(maxWait);  
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);  
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);  
        datasource.setValidationQuery(validationQuery);  
        datasource.setTestWhileIdle(testWhileIdle);  
        datasource.setTestOnBorrow(testOnBorrow);  
        datasource.setTestOnReturn(testOnReturn);  
        datasource.setPoolPreparedStatements(poolPreparedStatements);  
        datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);  */
        /*try {  
            datasource.setFilters(filters);  
        } catch (SQLException e) {  
            logger.error("druid configuration initialization filter", e);  
        }  
        datasource.setConnectionProperties(connectionProperties);  */
          
        return datasource;  
    }  
} 