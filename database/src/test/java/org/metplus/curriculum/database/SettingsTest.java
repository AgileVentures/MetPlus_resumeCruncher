package org.metplus.curriculum.database;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Joao Pereira on 25/08/2015.
 */
public class SettingsTest {

    @Test
    public void testGetSettings() throws Exception {
        Settings set = new Settings();
        assertEquals(0, set.getSettings().size());
        CruncherSettings.Setting<Integer> setting = new CruncherSettings.Setting<>("bamm", 1);
        set.addSetting(setting);
        assertEquals(1, set.getSettings().size());
    }

    @Test
    public void testGetSetting() throws Exception {
        Settings set = new Settings();
        CruncherSettings.Setting<Integer> setting = new CruncherSettings.Setting<>("bamm", 100);
        set.addSetting(setting);
        assertEquals(100, set.getSetting("bamm").getData());
    }

    @Test
    public void testAddSetting() throws Exception {
        Settings set = new Settings();
        CruncherSettings.Setting<Integer> setting = new CruncherSettings.Setting<>("bamm", 100);
        set.addSetting(setting);
        set.addSetting(setting);
        set.addSetting(setting);
        assertEquals(100, set.getSetting("bamm").getData());
        setting.setData(20);
        set.addSetting(setting);
        assertEquals(20, set.getSetting("bamm").getData());
    }
}