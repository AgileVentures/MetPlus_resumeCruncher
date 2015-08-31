package org.metplus.curriculum.database.domain;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonView;
import org.metplus.curriculum.database.exceptions.CruncherSettingsNotFound;
import org.metplus.curriculum.database.exceptions.MandatorySettingNotPresent;
import org.metplus.curriculum.database.exceptions.SettingNotFound;
import org.metplus.curriculum.database.template.TemplatePackage;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

@Document(collection="settings")
@TypeAlias("settings")
public class Settings extends AbstractDocument implements Serializable{
    private static final long serialVersionUID = -7788619177798124712L;
    private final String CRUNCHER_SETTINGS_NAME = "CRUNCHER_SETTINGS_NAME";
    @JsonView(TemplatePackage.class)
    private HashMap<String, CruncherSettings> cruncherSettings;
    @JsonView(TemplatePackage.class)
    private SettingsList appSettings;

    /**
     * Class constructor
     */
    public Settings() {
        super();
        cruncherSettings = new HashMap<>();
        appSettings = new SettingsList();
        appSettings.addMandatorySetting("test");
        Setting<String> set = new Setting<>("test", "haha");
        appSettings.addSetting(set);
        cruncherSettings.put("bamm", new CruncherSettings("New cruncher"));
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

    /**
     * Validate all the settings
     * The method will check if all the mandatory settings are present
     * for the application and also for the crunchers
     * @return True if everything is ok
     * @throws MandatorySettingNotPresent When is missing so attribute
     */
    public boolean validate() throws MandatorySettingNotPresent {
        appSettings.isMandatoryPresent();
        for(CruncherSettings settings: cruncherSettings.values()) {
            settings.isMandatoryPresent();
        }
        return true;
    }

    @Override
    public String toString() {
        String result = "Settings: {";
        result += "cruncher: { ";
        for(String cruncher: cruncherSettings.keySet()) {
            result += "'" + cruncher + "': " + cruncherSettings.get(cruncher) + ",";
        }
        result += "}, app: {" + appSettings + "}";
        return result;
    }
}
