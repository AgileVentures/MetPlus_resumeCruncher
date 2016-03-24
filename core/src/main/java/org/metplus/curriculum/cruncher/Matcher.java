package org.metplus.curriculum.cruncher;

import java.util.List;

/**
 * Created by joao on 3/21/16.
 * Interface for the matchers of resumes with title and description of jobs
 * @param <T> Resume type
 */
public interface Matcher<T> {
    /**
     * This function will receive a title and a description of a job
     * and is responsible for retrieving a list of resumes that match
     * @param title Title of the job
     * @param description Descrption of the job
     * @return Ordered list of resumes
     */
    List<T> match(String title, String description);

    /**
     * Retrieve the name of the cruncher associated with the
     * matcher
     * @return Name of the cruncher
     */
    String getCruncherName();
}
