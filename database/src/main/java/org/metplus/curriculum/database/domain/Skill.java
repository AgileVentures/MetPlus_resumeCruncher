package org.metplus.curriculum.database.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by joao on 5/28/16.
 */
@Document
public class Skill extends AbstractDocument {
    @Field
    private String name;
    /**
     * Retrieve the name of the skill
     * @return Skill name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the skill
     * @param name Skill name
     */
    public void setName(String name) {
        this.name = name;
    }
}
