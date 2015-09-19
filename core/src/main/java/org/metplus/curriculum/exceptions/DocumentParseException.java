package org.metplus.curriculum.exceptions;

/**
 * Exception thrown when an exception is reported while parsing a document
 */
public class DocumentParseException extends CurriculumException {
    /**
     * Class constructor
     *
     * @param text Text to be added to the exception
     */
    public DocumentParseException(String text) {
        super("Exception while parsing a file: " + text);
    }
}
