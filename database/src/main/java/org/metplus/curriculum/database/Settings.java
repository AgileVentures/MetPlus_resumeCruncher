package org.metplus.curriculum.database;


import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="settings")
@TypeAlias("settings")
public class Settings extends AbstractDocument {

    private CruncherSettings settings;

    /**
     * Class constructor
     */
    public Settings() {
        settings = new CruncherSettings();
    }

    /**
     * Retrieve all the settings
     * @return Settings object
     */
    public CruncherSettings getSettings() {
        return settings;
    }

    /**
     * Retrieve all the settings
     * @return Settings object
     */
    public CruncherSettings.Setting getSetting(String name) {
        return settings.getSetting(name);
    }

    /**
     * Add a new setting if the setting do not exist
     * If it exists will override the previous
     * @param setting Setting to add
     */
    public void addSetting(CruncherSettings.Setting setting) {
        settings.addSetting(setting);
    }
}
