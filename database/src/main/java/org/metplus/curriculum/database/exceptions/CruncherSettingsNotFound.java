package org.metplus.curriculum.database.exceptions;

/**
 * Exception class used when a cruncher setting is not found
 */
public class CruncherSettingsNotFound extends SettingNotFound {

    /**
     * Class constructor
     * @param cruncherName Name of the cuncher
     */
    public CruncherSettingsNotFound(String cruncherName) {
        super("Cruncher " + cruncherName);
    }
}
