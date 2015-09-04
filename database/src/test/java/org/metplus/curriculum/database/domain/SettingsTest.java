package org.metplus.curriculum.database.domain;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.exceptions.SettingNotFound;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by Joao Pereira on 25/08/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class})
public class SettingsTest {

    @Test
     public void testGetApplicationSetting() throws Exception {
        Settings set = new Settings();
        Setting<Integer> setting = new Setting<>("bamm", 100);
        set.addApplicationSetting(setting);
        assertEquals(100, set.getApplicationSetting("bamm").getData());
    }
    @Test
    public void testGetApplicationSettingException() throws Exception {
        Settings set = new Settings();
        try {
            set.getApplicationSetting("bamm");
            fail("SettingNotFound exception should have been thrown!");
        } catch(SettingNotFound e) {
            assertEquals("bamm is not present!", e.getMessage());
        }
    }

    @Test
    public void testAddApplicationSetting() throws Exception {
        Settings set = new Settings();
        Setting<Integer> setting = new Setting<>("bamm", 100);
        set.addApplicationSetting(setting);
        set.addApplicationSetting(setting);
        set.addApplicationSetting(setting);
        assertEquals(100, set.getApplicationSetting("bamm").getData());
        setting.setData(20);
        set.addApplicationSetting(setting);
        assertEquals(20, set.getApplicationSetting("bamm").getData());
    }


    @Test
    public void testGetCruncherSettings() throws Exception {
        Settings set = new Settings();
        CruncherSettings setting = new CruncherSettings("My cruncher");
        set.addCruncherSettings("My Cruncher", setting);
        assertEquals("My cruncher", set.getCruncherSettings("My Cruncher").getName());
    }
    @Test
    public void testGetCruncherSettingsException() throws Exception {
        Settings set = new Settings();
        try {
            set.getCruncherSettings("it shall not exist");
            fail("CruncherSettingsNotFound exception should have been thrown!");
        } catch(CruncherSettingsNotFound e) {
            assertEquals("Cruncher it shall not exist is not present!", e.getMessage());
        }
    }

    @Test
    public void testAddCruncherSetting() throws Exception {
        Settings set = new Settings();
        CruncherSettings setting = new CruncherSettings("crunch");
        set.addCruncherSettings("doit", setting);
        set.addCruncherSettings("doit", setting);
        set.addCruncherSettings("doit", setting);
        assertEquals("crunch", set.getCruncherSettings("doit").getName());
        setting.setName("cruncher1");
        set.addCruncherSettings("doit", setting);
        assertEquals("cruncher1", set.getCruncherSettings("doit").getName());
    }
}