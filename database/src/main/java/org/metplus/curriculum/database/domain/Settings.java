package org.metplus.curriculum.database.domain;


import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.exceptions.SettingNotFound;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.HashMap;

@Document(collection="settings")
@TypeAlias("settings")
public class Settings extends AbstractDocument {
    private final String CRUNCHER_SETTINGS_NAME = "CRUNCHER_SETTINGS_NAME";
    private HashMap<String, CruncherSettings> cruncherSettings;
    private SettingsList appSettings;

    /**
     * Class constructor
     */
    public Settings() {
        super();
        cruncherSettings = new HashMap<>();
        appSettings = new SettingsList();
    }

    /**
     * Add a new Application setting
     * @param setting Setting to be added
     */
    public void addApplicationSetting(Setting setting) {
        appSettings.addSetting(setting);
    }

    /**
     * Retrieve a setting from the Application
     * @param name Name of the setting to retrieve
     * @return The setting object
     * @throws SettingNotFound When the setting cannot be found
     */
    public Setting getApplicationSetting(String name) throws SettingNotFound {
        Setting result = appSettings.getSetting(name);
        if(null == result)
            throw new SettingNotFound(name);
        return result;
    }

    /**
     * Amount of application settings present
     * @return Number of settings
     */
    public int applicationSettingsSize() {
        return appSettings.size();
    }

    /**
     * Add a new cruncher settings
     * @param name Name of the cruncher
     * @param settings Cruncher Settings
     */
    public void addCruncherSettings(String name, CruncherSettings settings) {
        cruncherSettings.put(name, settings);
    }

    /**
     * Retrieve the settings of the cruncher
     * @param name Name of the cruncher
     * @return Settings of the cruncher
     * @throws CruncherSettingsNotFound When the cruncher configuration is not found
     */
    public CruncherSettings getCruncherSettings(String name) throws CruncherSettingsNotFound {
        CruncherSettings result = cruncherSettings.get(name);
        if(null == result)
            throw new CruncherSettingsNotFound(name);
        return result;
    }
}
