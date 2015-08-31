package org.metplus.curriculum.cruncher.expressionCruncher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ExpressionCruncher.class,SpringMongoConfig.class, DatabaseConfig.class})
public class ExpressionCruncherImplTest {
    @Autowired private ExpressionCruncher cruncher;
    @Autowired
    private SettingsRepository repository;
    @Before
    public void setUp() {
        repository.deleteAll();
        repository.save(new Settings());
        cruncher.init();
    }
    @After
    public void tearDown() {
        repository.deleteAll();
        repository.save(new Settings());
    }


    @Test
    public void testBasic() throws CruncherSettingsNotFound {
        Settings set = repository.findAll().iterator().next();
        CruncherSettings cSettings = set.getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
        CruncherImpl cruncherImpl = (CruncherImpl)cruncher.getCruncher();
        assertEquals(false, cruncherImpl.isCaseSensitive());
        assertEquals(2, cruncherImpl.getMergeList().size());
        assertEquals(2, cruncherImpl.getMergeList().get("cook").size());
        assertEquals(5, cruncherImpl.getIgnoreList().size());
    }
}