package org.metplus.curriculum.database.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that can be extended when we need a document
 * with meta data
 */
public class DocumentWithMetaData extends AbstractDocument {

    @Field
    private Map<String, MetaData> metaData;

    /**
     * Retrieve all the meta data of the specific resume
     * @return Structure with all the meta data
     */
    public Map<String, MetaData> getMetaData() {
        if(metaData == null)
            metaData = new HashMap<>();
        return metaData;
    }

    /**
     * Over write all the meta data of this resume
     * @param metaData New meta data
     */
    public void setMetaData(Map<String, MetaData> metaData) {
        this.metaData = metaData;
    }

    /**
     * Check if a cruncher already have processed this resume
     * @param cruncherName Name of the cruncher
     * @return True if meta data is present, false otherwise
     */
    @JsonIgnore
    public boolean isCruncherDataAvailable(String cruncherName) {
        if(getMetaData() == null)
            return false;
        return getMetaData().containsKey(cruncherName);
    }

    /**
     * Retrieve meta data from a specific cruncher
     * @param cruncherName Name of the cruncher
     * @return Cruncher meta data
     */
    public MetaData getCruncherData(String cruncherName) {
        if(getMetaData() == null)
            return null;
        return getMetaData().get(cruncherName);
    }
}
