package org.metplus.curriculum.database;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Created by Joao Pereira on 19/08/2015.
 */
public class CruncherSettingsTest extends TestCase {
    private CruncherSettings.Setting<Integer> int_set;
    private CruncherSettings.Setting<String>  str_set;
    private CruncherSettings.Setting<String>  str1_set;
    private CruncherSettings.Setting<Integer> int1_set;

    @Override
    protected void setUp() throws Exception
    {
        int_set = new CruncherSettings.Setting<>("int 1", 1);
        int1_set = new CruncherSettings.Setting<>("int 2", 2);
        str_set = new CruncherSettings.Setting<>("str 1", "my first string");
        str1_set = new CruncherSettings.Setting<>("str 2", "my second string");
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
        CruncherSettings.Setting set = settings.getSetting("int 1");
        assertEquals(1, set.getData());
    }

    @Test
    public void testGetSettingWithoutParameter() throws Exception {
        CruncherSettings settings = new CruncherSettings();
        settings.addSetting(str_set);
        settings.addSetting(str1_set);
        Collection<CruncherSettings.Setting> set = settings.getSetting();
        assertEquals(2, set.size());

        assertEquals("my first string", ((CruncherSettings.Setting) set.toArray()[0]).getData());
    }
}