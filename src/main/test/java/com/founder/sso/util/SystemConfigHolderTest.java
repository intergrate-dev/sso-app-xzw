package com.founder.sso.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class SystemConfigHolderTest {

    @Test
    public void testGetConfigStringClassOfT() {
        String baseDomain = SystemConfigHolder.getConfig("base_domain", String.class);
        String ssoDomain = SystemConfigHolder.getConfig("sso_domain", String.class);
        String context = SystemConfigHolder.getConfig("context", String.class);
        String protocol = SystemConfigHolder.getConfig("protocol", String.class);
        String baseUrl = SystemConfigHolder.getConfig("default_callback_base_url", String.class);
        String callbackUrl = SystemConfigHolder.getConfig("default_auth_callback_url", String.class);
        //String flag = SystemConfigHolder.getConfig("default_provider_flag", String.class);
        //ssertEquals("provider", flag);

    }

    @Test
    public void testGetConfigString() {
        //fail("Not yet implemented");
    }

}
