package com.founder.sso.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.founder.sso.test.category.UnStable;
import com.founder.sso.test.spring.Profiles;
@ContextConfiguration({"/applicationContext.xml","/applicationContext-persistent.xml"})
@ActiveProfiles(Profiles.UNIT_TEST)
public class OauthClientConfigDaoTest  extends AbstractTransactionalJUnit4SpringContextTests{
    @Autowired
    OauthClientConfigDao dao;

    @Test
    @Category(UnStable.class)
    public void testFindByProvider() {
        assertNotNull(dao);
    }
    @Test
    @Category(UnStable.class)
    public void testSaveS() {
        fail("Not yet implemented");
    }

}
