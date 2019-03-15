package com.founder.sso.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.founder.sso.test.spring.Profiles;

@ContextConfiguration({ "/applicationContext.xml", "/applicationContext-persistent.xml" })
@ActiveProfiles(Profiles.UNIT_TEST)
public class LocalPrincipalDaoTest extends AbstractTransactionalJUnit4SpringContextTests{

    @Autowired
    private LocalPrincipalDao dao;

    @Test
    public void test() {
        //assertThat(dao.count()).isEqualTo(4);
        //assertThat(dao.findByUsername("zhangmc").getUser().getId()).isEqualTo(1);
        //assertThat(dao.findByEmail("w@sina.com").getUser().getId()).isEqualTo(3);
        //assertThat(dao.findByPhone("138000000")).isNull();
        //assertThat(dao.findByPhone("13011112222")).isNotNull();
        //assertThat(dao.findByUsernameOrPhoneOrEmail("15922224444")).isNotNull();
        
    }

}
