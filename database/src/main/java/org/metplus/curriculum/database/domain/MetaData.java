package org.metplus.curriculum.database.domain;

import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by joao on 3/16/16.
 */
@Document
public class MetaData implements CruncherMetaData, Serializable {
    private Map<String, MetaDataField> fields;

    public Map<String, MetaDataField> getFields() {
        return fields;
    }

    public void setFields(Map<String, MetaDataField> fields) {
        this.fields = fields;
    }
}
