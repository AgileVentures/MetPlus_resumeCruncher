package org.metplus.curriculum.database.exceptions;

import org.metplus.curriculum.exceptions.CurriculumException;

/**
 * Exception used when an error happen while trying to read the file from mongo
 */
public class ResumeReadException extends CurriculumException {
    /**
     * Class constructor
     *
     * @param text Text to be added to the exception
     */
    public ResumeReadException(String text) {
        super("Exception occur while trying to read the content of the file:" + text);
    }
}
