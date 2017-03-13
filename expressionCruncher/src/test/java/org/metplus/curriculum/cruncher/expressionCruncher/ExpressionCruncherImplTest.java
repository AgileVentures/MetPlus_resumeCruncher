package org.metplus.curriculum.cruncher.expressionCruncher;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.properties.PropertyMapping;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.*;

@ActiveProfiles("development")
//@ContextConfiguration(classes = {ExpressionCruncher.class,SpringMongoConfig.class, DatabaseConfig.class, CrunchersList.class, MatcherList.class})
@ContextConfiguration(classes = {ExpressionCruncher.class, CrunchersList.class, MatcherList.class})
@RunWith(SpringRunner.class)
@DataMongoTest
@EnableMongoRepositories(basePackages = "org.metplus.curriculum.database.repository")


//@ComponentScan(basePackages={"org.metplus.curriculum.database"})
public class ExpressionCruncherImplTest {
    @Autowired
    private ExpressionCruncher cruncher;

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
    public void checkDefaultSettings() throws CruncherSettingsNotFound {
        Settings set = repository.findAll().iterator().next();
        CruncherSettings cSettings = set.getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
        CruncherImpl cruncherImpl = (CruncherImpl)cruncher.getCruncher();
        assertEquals(false, cruncherImpl.isCaseSensitive());
        assertEquals(2, cruncherImpl.getMergeList().size());
        assertEquals(2, cruncherImpl.getMergeList().get("cook").size());
        assertEquals(20, cruncherImpl.getIgnoreList().size());
    }

    @Test
    public void changeDefault() throws CruncherSettingsNotFound {
        Settings set = repository.findAll().iterator().next();
        CruncherSettings cSettings = set.getCruncherSettings(CruncherImpl.CRUNCHER_NAME);
        List<String> ignore = cruncher.getIgnoreList();
        int totalIgnore = ignore.size();
        ignore.add("bamm");
        ignore.add("test");
        ignore.add("test1");
        cruncher.setIgnoreList(ignore);
        cruncher.save();
        CruncherImpl cruncherImpl = (CruncherImpl)cruncher.getCruncher();
        assertEquals(false, cruncherImpl.isCaseSensitive());
        assertEquals(2, cruncherImpl.getMergeList().size());
        assertEquals(2, cruncherImpl.getMergeList().get("cook").size());
        assertEquals(totalIgnore+3, cruncherImpl.getIgnoreList().size());
    }
}