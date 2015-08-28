package org.metplus.curriculum.database.exceptions;

import org.metplus.curriculum.exceptions.CurriculumException;

/**
 * Exception class used when a setting is not found
 */
public class SettingNotFound extends CurriculumException {
    /**
     * Class constructor
     * @param settingName Name of the setting
     */
    public SettingNotFound(String settingName) {
        super(settingName + " is not present!");
    }
}