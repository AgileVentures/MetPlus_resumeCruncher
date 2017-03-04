package org.metplus.curriculum.web.answers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.web.StarFormater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents all the Job that
 * match a specific resume
 */
public class JobMatchAnswer<T> extends GenericAnswer {

    static class JobAnswer {
        private String jobId;

        public JobAnswer(String jobId) {
            this.jobId = jobId;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }
    }

    public static class JobWithProbability extends JobAnswer {
        private double stars;

        public JobWithProbability(String jobId, double stars) {
            super(jobId);
            setStars(stars);
        }

        public double getStars() {
            return stars;
        }

        public void setStars(double stars) {
            this.stars = StarFormater.format(stars);
        }


    }

    Map<String, List<T>> jobs;

    /**
     * Retrieve all jobs information
     * @return Map with Jobs that match per matcher
     */
    public Map<String, List<T>> getJobs() {
        if(jobs == null)
            jobs = new HashMap<>();
        return jobs;
    }

    /**
     * Set the jobs that match
     * @param jobs Map with Jobs that match per matcher
     */
    public void setJobs(Map<String, List<T>> jobs) {
        this.jobs = jobs;
    }

    /**
     * Add a Job that matches
     * @param cruncherName Cruncher name
     * @param job Job to add
     */
    public void addJob(String cruncherName, Job job, boolean withProbability) {
        if(!getJobs().containsKey(cruncherName))
            getJobs().put(cruncherName, new ArrayList<>());
        if(withProbability)
            getJobs().get(cruncherName).add((T)new JobWithProbability(job.getJobId(), job.getStarRating()));
        else
            getJobs().get(cruncherName).add((T)job.getJobId());
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "JobMatchAnswer:";
        }
    }
}
