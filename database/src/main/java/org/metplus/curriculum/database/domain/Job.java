package org.metplus.curriculum.database.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

/**
 * Created by joao on 3/23/16.
 */
@Document
public class Job extends AbstractDocument {
    @Field
    private String title;
    @Field
    private String jobId;
    @Field
    private String description;
    @Field
    private Map<String, MetaData> metaData;
}
