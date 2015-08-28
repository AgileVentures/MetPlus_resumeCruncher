package org.metplus.curriculum.database.domain;

import com.mongodb.DBObject;
import org.metplus.curriculum.database.exceptions.MandatorySettingNotPresent;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Joao Pereira on 28/08/2015.
 */
public class SettingsList extends AbstractDocument{
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
    public SettingsList() {
        super();
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
    /**
     * Add a mandatory Setting
     * @param name Name of the setting that is mandatory
     */
    public void addMandatorySetting(String name) {
        mandatory.add(name);
    }
    /**
     * Check if all the mandatory settings are present
     * @throws MandatorySettingNotPresent When a mandatory setting is not present
     * @return True if all the mandatory settings are present
     */
    public boolean isMandatoryPresent() throws MandatorySettingNotPresent {
        for(String name: mandatory) {
            if(settings.get(name) == null)
                throw new MandatorySettingNotPresent(name);
        }
        return true;
    }

}
