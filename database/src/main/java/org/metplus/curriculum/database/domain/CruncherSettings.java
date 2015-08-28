package org.metplus.curriculum.database.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joao Pereira on 19/08/2015.
 */
public class CruncherSettings extends SettingsList {
    private final String NAME_SETTING = "Name";
    public CruncherSettings() {
        super();
        addMandatorySetting(NAME_SETTING);
    }

    public CruncherSettings(String name) {
        super();
        addMandatorySetting(NAME_SETTING);
        Setting setting = new Setting<>(NAME_SETTING, name);
        addSetting(setting);
    }

    public String getName() {
        return (String)getSetting(NAME_SETTING).getData();
    }
    public void setName(String name) {
        addSetting(new Setting<>(NAME_SETTING, name));
    }
}
