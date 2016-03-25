package org.metplus.curriculum.database.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

/**
 * Job Model
 */
@Document
public class Job extends DocumentWithMetaData {
    @Field
    private String title;
    @Field
    private String jobId;
    @Field
    private String description;

    @Field
    private DocumentWithMetaData titleMetaData;

    @Field
    private DocumentWithMetaData descriptionMetaData;

    public MetaData getTitleCruncherData(String cruncherName) {
        return titleMetaData.getMetaData().get(cruncherName);
    }
    public Map<String, MetaData> getTitleCruncherData() {
        return titleMetaData.getMetaData();
    }


    public void setTitleMetaData(DocumentWithMetaData titleMetaData) {
        this.titleMetaData = titleMetaData;
    }

    public DocumentWithMetaData getDescriptionMetaData() {
        return descriptionMetaData;
    }

    public void setDescriptionMetaData(DocumentWithMetaData descriptionMetaData) {
        this.descriptionMetaData = descriptionMetaData;
    }
    public DocumentWithMetaData getTitleMetaData() {return titleMetaData;};



    /**
     * Retrieve the title of the Job
     * @return Job Title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the Job Title
     * @param title Job Title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieve the job identifier
     * @return Job Identifier
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Set the job identifier
     * @param jobId Job identifier
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * Retrieve the job description
     * @return Job Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the job description
     * @param description Job Description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
