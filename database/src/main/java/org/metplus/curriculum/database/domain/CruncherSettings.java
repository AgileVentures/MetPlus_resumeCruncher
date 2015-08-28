package org.metplus.curriculum.database.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joao Pereira on 19/08/2015.
 */
public class CruncherSettings {
    /**
     * Storage of all the settings
     */
    private HashMap<String, Setting> settings;

    /**
     * Mandatory settings
     */
    private List<String> mandatory;

    /**
     * Class constructor
     */
    public CruncherSettings() {
        settings = new HashMap<>();
        mandatory = new ArrayList<>();
    }

    /**
     * Add a new setting
     * @param setting New setting
     */
    public void addSetting(Setting setting) {
        settings.put(setting.getName(), setting);
    }

    /**
     * Retrieve all the settings
     * @return Collection with all settings
     */
    public Collection<Setting> getSetting() {
        return settings.values();
    }

    /**
     * Retrieve a setting
     * @param name Name of the setting to retrieve
     * @return The setting
     */
    public Setting getSetting(String name) {
        return settings.get(name);
    }

    /**
     * Retrieve the amount of settings
     * @return Amount of settings
     */
    public int size(){return settings.size();}

}
