package org.metplus.curriculum.database.domain;

import org.springframework.data.annotation.Transient;
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

    @Transient
    private double starRating;

    /**
     * Retrieve the Meta data from crunching the title
     * @param cruncherName Cruncher name
     * @return Meta data
     */
    public MetaData getTitleCruncherData(String cruncherName) {
        if(titleMetaData == null || titleMetaData.getMetaData() == null)
            return null;
        return titleMetaData.getMetaData().get(cruncherName);
    }

    /**
     * Retrieve all the crunchers meta data from the title
     * @return Map with all crunchers meta data
     */
    public Map<String, MetaData> getTitleCruncherData() {
        if(titleMetaData == null)
            return null;
        return titleMetaData.getMetaData();
    }

    /**
     * Retrieve the Meta Data from crunching the description
     * @param cruncherName Cruncher name
     * @return Meta data
     */
    public MetaData getDescriptionCruncherData(String cruncherName) {
        if(descriptionMetaData == null || descriptionMetaData.getMetaData() == null)
            return null;
        return descriptionMetaData.getMetaData().get(cruncherName);
    }

    /**
     * Retrieve all the crunchers meta data from the description
     * @return Map with all crunchers meta data
     */
    public Map<String, MetaData> getDescriptionCruncherData() {
        if(descriptionMetaData == null)
            return null;
        return descriptionMetaData.getMetaData();
    }

    /**
     * Set title meta data
     * @param titleMetaData Title meta data
     */
    public void setTitleMetaData(DocumentWithMetaData titleMetaData) {
        this.titleMetaData = titleMetaData;
    }

    /**
     * Retrieve the description meta data
     * @return Object with the meta data
     */
    public DocumentWithMetaData getDescriptionMetaData() {
        return descriptionMetaData;
    }

    /**
     * Set description meta data
     * @param descriptionMetaData Description meta data
     */
    public void setDescriptionMetaData(DocumentWithMetaData descriptionMetaData) {
        this.descriptionMetaData = descriptionMetaData;
    }

    /**
     * Retrieve the title meta data
     * @return Object with the meta data
     */
    public DocumentWithMetaData getTitleMetaData() {return titleMetaData;}



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

    /**
     * Retrieve the startRating of the Job
     * @return Start rating
     */
    public double getStarRating() {
        return starRating;
    }

    /**
     * Set the start rating of the Job
     * @param starRating Star rating
     */
    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }
}
