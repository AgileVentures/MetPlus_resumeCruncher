package org.metplus.curriculum.database.domain;


/**
 * Class that holds 1 specific setting
 * @param <E> Type of the setting
 */
public class Setting<E> {
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
     */
    public Setting() {
    }

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