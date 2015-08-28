package org.metplus.curriculum.database.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Setting;
import org.metplus.curriculum.database.domain.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;


@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class})
public class SettingsRepositoryTest {
    @Autowired private SettingsRepository repository;
    @After
    public void setUp(){
        repository.deleteAll();
    }

    @Test
    public void testFindByName() throws Exception {
        repository.save(new Settings());
        assertEquals(1, repository.count());
        Settings setting = repository.findAll().iterator().next();
        Setting<Integer> set = new Setting<>("Bamm", 10);
        setting.addSetting(set);
        repository.save(setting);assertEquals(1, repository.count());
        setting = repository.findAll().iterator().next();
        assertEquals(10, setting.getSetting("Bamm").getData());
    }
}