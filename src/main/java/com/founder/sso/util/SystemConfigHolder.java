package com.founder.sso.util;

import java.io.File;
import java.io.IOException;

import org.ini4j.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class SystemConfigHolder {
    //TODO 完成日志
    private static final Logger log = LoggerFactory.getLogger(SystemConfigHolder.class);
    private static final String DEFAULT_CONFIG_FILENAME = "systemConfig.cfg";
    private static final Object CONFIG_LOCK = new Object();
    private static Options configurations;

    static {
        if (configurations == null) {
            synchronized (CONFIG_LOCK) {
                if (configurations == null) {
                    try {
                        File cfgFile = new ClassPathResource(DEFAULT_CONFIG_FILENAME).getFile();
                        configurations = new Options(cfgFile);
                    } catch (IOException e) {
                       throw new RuntimeException("无法读取基础配置文件["+new File(DEFAULT_CONFIG_FILENAME).getAbsolutePath()+"]-系统启动失败", e);
                    }
                }
            }
        }

    }
    
    public static <T> T getConfig(String param,Class<T> clazz) {
        return configurations.fetch(param, clazz);
    }
    public static String getConfig(String param) {
        return configurations.fetch(param);
    }
    
}
