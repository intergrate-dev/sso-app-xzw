package com.founder.sso.admin.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.founder.sso.test.spring.Profiles;
@ContextConfiguration({"/applicationContext.xml","/applicationContext-persistent.xml"})
@ActiveProfiles(Profiles.UNIT_TEST)
public class AdminAccountDaoTest extends AbstractTransactionalJUnit4SpringContextTests{
    
    @Autowired
    AdminAccountDao accountDao;

    @Test
    public void testFindByLoginname() {
        //assertEquals(6, accountDao.count());
        //assertNotNull(accountDao.findByLoginname("sa"));
        //assertNull(accountDao.findByLoginname("xxxx"));
    }
    //@Test
    public void testFindByLoginnameAndEnabledIsTrue() {
        assertNotNull(accountDao.findByLoginname("sa"));
        assertNotNull(accountDao.findByLoginnameAndEnabledIsTrue("sa"));
        assertTrue(accountDao.findByLoginnameAndEnabledIsTrue("sa").isEnabled());
        assertNull(accountDao.findByLoginnameAndEnabledIsTrue("sa1"));
    }
    //@Test
    public void testFindByEnabledSuperAdmin() {
        assertEquals(1, accountDao.findEnabledSuperAdmins().size());
    }

}
