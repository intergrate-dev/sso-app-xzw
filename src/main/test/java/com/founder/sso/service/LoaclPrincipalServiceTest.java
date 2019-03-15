package com.founder.sso.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.test.spring.Profiles;

@ContextConfiguration({ "/applicationContext.xml", "/applicationContext-persistent.xml" })
@ActiveProfiles(Profiles.UNIT_TEST)
public class LoaclPrincipalServiceTest extends AbstractTransactionalJUnit4SpringContextTests {
    @Autowired
    private LocalPrincipalService service;

    @Test
    public void test() {
        /*
    	assertThat(service.findPrincipal("w@sina.com")).isNotNull();
        assertThat(service.findPrincipal("13011112222")).isNotNull();
        assertThat(service.findPrincipal("zhangmc")).isNotNull();
        assertThat(service.findPrincipal("xxx")).isNull();

        assertThat(service.findPrincipal("w@sina.com", LocalPrincipal.IdentityType.EMAIL)).isNotNull();
        assertThat(service.findPrincipal("13011112222", LocalPrincipal.IdentityType.PHONE)).isNotNull();
        assertThat(service.findPrincipal("zhangmc", LocalPrincipal.IdentityType.USERNAME)).isNotNull();

        assertThat(service.findPrincipal("xxx@qq.com", LocalPrincipal.IdentityType.EMAIL)).isNull();
        assertThat(service.findPrincipal("zhangmc", LocalPrincipal.IdentityType.EMAIL)).isNull();
        */
    }

}
