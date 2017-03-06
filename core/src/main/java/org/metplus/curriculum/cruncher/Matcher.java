package org.metplus.curriculum.cruncher;

import java.util.List;

/**
 * Created by joao on 3/21/16.
 * Interface for the matchers of resumes with title and description of jobs
 * @param <Entry> Resume type
 * @param <Result> Job Type
 */
public interface Matcher<Entry, Result> {

    List<Result> match(Entry entry);
    List<Entry> matchInverse(Result entry);

    /**
     * Retrieve the name of the cruncher associated with the
     * matcher
     * @return Name of the cruncher
     */
    String getCruncherName();

    /**
     * Check the similarity between a resume and a job
     * @param resume
     * @param job
     * @return Range between 0 and 5 being 5 similar
     */
    double matchSimilarity(Entry resume, Result job);
}
