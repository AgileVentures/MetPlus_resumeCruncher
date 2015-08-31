package org.metplus.curriculum.database.exceptions;

import org.metplus.curriculum.exceptions.CurriculumException;

/**
 * Exception class used when a mandatory setting is not present
 */
public class MandatorySettingNotPresent extends CurriculumException{
    protected String settingName;
    /**
     * Class constructor
     * @param settingName Name of the setting
     */
    public MandatorySettingNotPresent(String settingName) {
        super(settingName + " is mandatory!");
        this.settingName = settingName;
    }
    /**
     * Class constructor
     * @param settingName Name of the setting
     * @param at Where is this setting
     */
    public MandatorySettingNotPresent(String settingName, String at) {
        super(settingName + " is mandatory " + at + "!");
        this.settingName = settingName;
    }
}
