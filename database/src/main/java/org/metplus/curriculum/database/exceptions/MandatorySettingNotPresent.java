package org.metplus.curriculum.database.exceptions;

import org.metplus.curriculum.exceptions.CurriculumException;

/**
 * Exception class used when a mandatory setting is not present
 */
public class MandatorySettingNotPresent extends CurriculumException{
    /**
     * Class constructor
     * @param settingName Name of the setting
     */
    public MandatorySettingNotPresent(String settingName) {
        super(settingName + " is mandatory!");
    }
}
