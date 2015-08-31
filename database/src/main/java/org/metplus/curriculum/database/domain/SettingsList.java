package org.metplus.curriculum.database.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.mongodb.DBObject;
import org.metplus.curriculum.database.exceptions.MandatorySettingNotPresent;
import org.metplus.curriculum.database.template.TemplatePackage;
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
    @JsonView(TemplatePackage.class)
    private HashMap<String, Setting> settings;

    /**
     * Mandatory settings
     */
    @JsonView(TemplatePackage.class)
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
     * Add a List of new settings
     * @param settings Collection of settings
     */
    public void setSettings(HashMap<String, Setting> settings) {
        settings.values().forEach(this::addSetting);
    }

    /**
     * Retrieve all the settings
     * @return Collection with all settings
     */
    @JsonIgnore
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
    @JsonIgnore
    public boolean isMandatoryPresent() throws MandatorySettingNotPresent {
        for(String name: mandatory) {
            if(settings.get(name) == null)
                throw new MandatorySettingNotPresent(name, this.getClass().getSimpleName());
        }
        return true;
    }

    @Override
    public String toString(){
        String result = "SettingsList: {";
        result += "settings: {";
        for(String setting: settings.keySet()) {
            result += setting + ": " + settings.get(setting) + ",";
        }
        result += "}, mandatory: [";
        for(String setting: mandatory) {
            result += setting + ",";
        }
        result += "]";
        return result;
    }
}
