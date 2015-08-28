package org.metplus.curriculum.database.domain;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.metplus.curriculum.config.DatabaseConfig;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.CruncherSettings;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import java.util.Collection;

/**
 * Created by Joao Pereira on 19/08/2015.
 */
@ActiveProfiles("development")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfig.class, DatabaseConfig.class})
public class CruncherSettingsTest {
    private Setting<Integer> int_set;
    private Setting<String>  str_set;
    private Setting<String>  str1_set;
    private Setting<Integer> int1_set;

    @Before
    public void setUp() throws Exception
    {
        int_set = new Setting<>("int 1", 1);
        int1_set = new Setting<>("int 2", 2);
        str_set = new Setting<>("str 1", "my first string");
        str1_set = new Setting<>("str 2", "my second string");
    }
    @Test
    public void testAddSetting() throws Exception {
        CruncherSettings settings = new CruncherSettings();
        assertEquals(0, settings.size());
        settings.addSetting(int_set);
        assertEquals(1, settings.size());
        settings.addSetting(str_set);
        assertEquals(2, settings.size());
        settings.addSetting(str_set);
        settings.addSetting(str_set);
        settings.addSetting(str_set);
        settings.addSetting(str_set);
        assertEquals(2, settings.size());
    }

    @Test
    public void testGetSettingWithParameter() throws Exception {
        CruncherSettings settings = new CruncherSettings();
        settings.addSetting(int_set);
        settings.addSetting(int1_set);
        Setting set = settings.getSetting("int 1");
        assertEquals(1, set.getData());
    }

    @Test
    public void testGetSettingWithoutParameter() throws Exception {
        CruncherSettings settings = new CruncherSettings();
        settings.addSetting(str_set);
        settings.addSetting(str1_set);
        Collection<Setting> set = settings.getSetting();
        assertEquals(2, set.size());

        assertEquals("my first string", ((Setting) set.toArray()[0]).getData());
    }
}