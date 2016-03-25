package org.metplus.curriculum.web.answers;

import java.io.Serializable;

/**
 * Generic JSON answer object
 */
public class GenericAnswer  implements Serializable {

    private static final long serialVersionUID = -7788619177798124712L;

    /**
     * Result code
     */
    private ResultCodes resultCode;

    /**
     * Message
     */
    private String message;

    /**
     * Class constructor
     * @param resultCode Result code of the message
     * @param message Message to be sent
     */
    public GenericAnswer(ResultCodes resultCode, String message) {
        this.message = message;
        this.resultCode = resultCode;
    }

    /**
     * Class constructor
     */
    public GenericAnswer() {
        this.resultCode = ResultCodes.FATAL_ERROR;
        this.message = "Default constructor error";
    }

    /**
     * Retrieve the Result Code
     * @return Result code
     */
    public ResultCodes getResultCode() {
        return resultCode;
    }

    /**
     * Set the result code
     * @param resultCode Result Code
     */
    public void setResultCode(ResultCodes resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * Retrieve the message
     * @return Message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message
     * @param message Message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
