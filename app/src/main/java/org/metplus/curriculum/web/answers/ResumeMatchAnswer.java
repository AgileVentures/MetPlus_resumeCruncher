package org.metplus.curriculum.web.answers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metplus.curriculum.database.domain.Resume;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents all the Resumes that
 * match a specific Job information
 */
public class ResumeMatchAnswer extends GenericAnswer {

    static class ResumeWithProbability {
        private String resumeId;
        private double stars;

        public ResumeWithProbability(String resumeId, double stars) {
            this.resumeId = resumeId;
            this.stars = stars;
        }

        public String getResumeId() {
            return resumeId;
        }

        public void setResumeId(String resumeId) {
            this.resumeId = resumeId;
        }

        public double getStars() {
            return stars;
        }

        public void setStars(double stars) {
            this.stars = stars;
        }
    }

    Map<String, List<ResumeWithProbability>> resumes;

    /**
     * Retrieve all Resume information
     * @return Map with Resumes that match per matcher
     */
    public Map<String, List<ResumeWithProbability>> getResumes() {
        if(resumes == null)
            resumes = new HashMap<>();
        return resumes;
    }

    /**
     * Set the Resumes that match
     * @param resumes Map with Resumes that match per matcher
     */
    public void setResumes(Map<String, List<ResumeWithProbability>> resumes) {
        this.resumes = resumes;
    }

    /**
     * Add a Resume that matches
     * @param cruncherName Cruncher name
     * @param resume Resume to add
     */
    public void addResume(String cruncherName, Resume resume) {
        if(!getResumes().containsKey(cruncherName))
            getResumes().put(cruncherName, new ArrayList<>());
        getResumes().get(cruncherName).add(new ResumeWithProbability(resume.getUserId(), resume.getStarRating()));
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "ResumeMatchAnswer:";
        }
    }
}
