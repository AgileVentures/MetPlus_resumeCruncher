package org.metplus.curriculum.database.domain;

import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joao on 3/16/16.
 * This class represents the meta data of a resume
 */
@Document
public class MetaData extends AbstractDocument implements CruncherMetaData {
    @Field
    private Map<String, MetaDataField> fields;

    /**
     * Retrieve all the fields meta data
     * @return Map with all the fields
     */
    public Map<String, MetaDataField> getFields() {
        return fields;
    }

    /**
     * Add new meta data fields
     * @param name Name of the field
     * @param data Data of the field
     */
    public void addField(String name, MetaDataField data) {
        if(fields == null)
            fields = new HashMap<>();
        fields.put(name, data);
    }

    /**
     * Set the Meta data fields
     * @param fields Meta data fields
     */
    public void setFields(Map<String, MetaDataField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        String result = "MetaData: ";
        for(Map.Entry entry: fields.entrySet()) {
            result += "'" + entry.getKey() + "': '" + entry.getValue()+ "',";
        }
        return result;
    }
}
