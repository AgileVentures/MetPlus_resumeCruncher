package org.metplus.curriculum.database.domain;


import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="settings")
@TypeAlias("settings")
public class Settings extends AbstractDocument {

    private CruncherSettings cruncherSettings;

    /**
     * Class constructor
     */
    public Settings() {
        cruncherSettings = new CruncherSettings();
    }

    /**
     * Retrieve all the settings
     * @return Settings object
     */
    public CruncherSettings getSettings() {
        return cruncherSettings;
    }

    /**
     * Retrieve all the settings
     * @return Settings object
     */
    public Setting getSetting(String name) {
        return cruncherSettings.getSetting(name);
    }

    /**
     * Add a new setting if the setting do not exist
     * If it exists will override the previous
     * @param setting Setting to add
     */
    public void addSetting(Setting setting) {
        cruncherSettings.addSetting(setting);
    }
}
