package org.metplus.curriculum.database.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Settings;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Joao Pereira on 25/08/2015.
 */

@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class})
public class SettingsTest {

    @Test
    public void testGetSettings() throws Exception {
        Settings set = new Settings();
        assertEquals(0, set.getSettings().size());
        Setting<Integer> setting = new Setting<>("bamm", 1);
        set.addSetting(setting);
        assertEquals(1, set.getSettings().size());
    }

    @Test
    public void testGetSetting() throws Exception {
        Settings set = new Settings();
        Setting<Integer> setting = new Setting<>("bamm", 100);
        set.addSetting(setting);
        assertEquals(100, set.getSetting("bamm").getData());
    }

    @Test
    public void testAddSetting() throws Exception {
        Settings set = new Settings();
        Setting<Integer> setting = new Setting<>("bamm", 100);
        set.addSetting(setting);
        set.addSetting(setting);
        set.addSetting(setting);
        assertEquals(100, set.getSetting("bamm").getData());
        setting.setData(20);
        set.addSetting(setting);
        assertEquals(20, set.getSetting("bamm").getData());
    }
}