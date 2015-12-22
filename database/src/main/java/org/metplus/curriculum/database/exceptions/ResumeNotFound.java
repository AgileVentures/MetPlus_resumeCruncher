package org.metplus.curriculum.database.exceptions;

import org.metplus.curriculum.exceptions.CurriculumException;

/**
 * Exception used when the resume cannot be found for a specific user
 */
public class ResumeNotFound extends CurriculumException {
    /**
     * Class constructor
     * @param text Additional information
     */
    public ResumeNotFound(String text) {
        super("Unable to retrieve the resume for the specific user: " + text);
    }
}
