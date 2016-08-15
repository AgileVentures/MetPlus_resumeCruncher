package org.metplus.curriculum.cruncher;

import java.util.List;

/**
 * Created by joao on 3/21/16.
 * Interface for the matchers of resumes with title and description of jobs
 * @param <T> Resume type
 * @param <E> Job Type
 */
public interface Matcher<T, E> {
    /**
     * This function will receive a title and a description of a job
     * and is responsible for retrieving a list of resumes that match
     * @param title Title of the job
     * @param description Descrption of the job
     * @return Ordered list of resumes
     */
    List<T> match(String title, String description);

    /**
     * This function will receive a job
     * and is responsible for retrieving a list of resumes that match
     * @param job Job object
     * @return Ordered list of resumes
     */
    List<T> match(E job);

    /**
     * This function will receive a meta data of a resume
     * and is responsible for retrieving a list of Jobs that match
     * @param metadata Meta data to match with
     * @return Ordered list of resumes
     */
    List<E> match(CruncherMetaData metadata);

    /**
     * Retrieve the name of the cruncher associated with the
     * matcher
     * @return Name of the cruncher
     */
    String getCruncherName();


}
