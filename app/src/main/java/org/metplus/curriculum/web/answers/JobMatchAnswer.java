package org.metplus.curriculum.web.answers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metplus.curriculum.database.domain.Job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents all the Job that
 * match a specific resume
 */
public class JobMatchAnswer extends GenericAnswer {


    static class JobWithProbability {
        private String jobId;
        private double stars;

        public JobWithProbability(String jobId, double stars) {
            this.jobId = jobId;
            this.stars = stars;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(String jobId) {
            this.jobId = jobId;
        }

        public double getStars() {
            return stars;
        }

        public void setStars(double stars) {
            this.stars = stars;
        }
    }

    Map<String, List<JobWithProbability>> jobs;

    /**
     * Retrieve all jobs information
     * @return Map with Jobs that match per matcher
     */
    public Map<String, List<JobWithProbability>> getJobs() {
        if(jobs == null)
            jobs = new HashMap<>();
        return jobs;
    }

    /**
     * Set the jobs that match
     * @param jobs Map with Jobs that match per matcher
     */
    public void setJobs(Map<String, List<JobWithProbability>> jobs) {
        this.jobs = jobs;
    }

    /**
     * Add a Job that matches
     * @param cruncherName Cruncher name
     * @param job Job to add
     */
    public void addJob(String cruncherName, Job job) {
        if(!getJobs().containsKey(cruncherName))
            getJobs().put(cruncherName, new ArrayList<>());
        getJobs().get(cruncherName).add(new JobWithProbability(job.getJobId(), job.getStarRating()));
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
