package org.metplus.curriculum.exceptions;

/**
 * Base class for all the exceptions
 */
public class CurriculumException extends Exception {
    /**
     * Class constructor
     * @param text Text to be added to the exception
     */
    public CurriculumException(String text){
        super(text);
    }
}
