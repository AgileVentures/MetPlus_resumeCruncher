package org.metplus.curriculum.database.domain;

/**
 * Meta data field information
 */
public class MetaDataField<T> {
    T data;
    /**
     * Class constructor
     * @param data Data to be saved
     */
    public MetaDataField(T data) {
        this.data = data;
    }

    /**
     * Retrieve the field information
     * @return Information
     */
    public T getData() {
        return data;
    }

    /**
     * Set the field information
     * @param data Data to be saved
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String result = "MetaDataField: ";
        result += "'" + data + "'";
        return result;
    }
}
