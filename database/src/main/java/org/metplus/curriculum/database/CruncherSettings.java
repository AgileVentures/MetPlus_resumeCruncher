package org.metplus.curriculum.database;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Joao Pereira on 19/08/2015.
 */
public class CruncherSettings {
    /**
     * Class that holds 1 specific setting
     * @param <E> Type of the setting
     */
    static class Setting<E> {
        /**
         * Name
         */
        private String name;
        /**
         * Data
         */
        private E data;

        /**
         * Class constructor
         * @param name Name of the setting
         * @param data Data on the setting
         */
        public Setting(String name, E data) {
            this.name = name;
            this.data = data;
        }

        /**
         * Retrieve the name of the setting
         * @return Name of the setting
         */
        public String getName() {
            return name;
        }

        /**
         * Set the name of the setting
         * @param name New name of the setting
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Retrieve the data of the setting
         * @return Data
         */
        public E getData() {
            return data;
        }

        /**
         * Set data of the setting
         * @param data Data of the setting
         */
        public void setData(E data) {
            this.data = data;
        }
    }

    /**
     * Storage of all the settings
     */
    private HashMap<String, Setting> settings;

    /**
     * Class constructor
     */
    public CruncherSettings() {
        settings = new HashMap();
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
