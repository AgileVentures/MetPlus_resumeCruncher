package org.metplus.curriculum.web.answers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metplus.curriculum.database.domain.Resume;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that represents all the Resumes that
 * match a specific Job information
 */
public class ResumeMatchAnswer<T> extends GenericAnswer {

    public static class ResumeWithProbability {
        private String resumeId;
        private double stars;

        public ResumeWithProbability(String resumeId, double stars) {
            this.resumeId = resumeId;
            this.setStars(stars);
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
            DecimalFormat df = new DecimalFormat("#.#");
            df.setRoundingMode(RoundingMode.FLOOR);

            this.stars = Double.parseDouble(df.format(new Double(stars)));
        }
    }

    Map<String, List<T>> resumes;

    /**
     * Retrieve all Resume information
     * @return Map with Resumes that match per matcher
     */
    public Map<String, List<T>> getResumes() {
        if(resumes == null)
            resumes = new HashMap<>();
        return resumes;
    }

    /**
     * Set the Resumes that match
     * @param resumes Map with Resumes that match per matcher
     */
    public void setResumes(Map<String, List<T>> resumes) {
        this.resumes = resumes;
    }

    /**
     * Add a Resume that matches
     * @param cruncherName Cruncher name
     * @param resume Resume to add
     * @param withProbability Answer with probability or without
     */
    public void addResume(String cruncherName, Resume resume, boolean withProbability) {
        if(!getResumes().containsKey(cruncherName))
            getResumes().put(cruncherName, new ArrayList<>());
        if(withProbability)
            getResumes().get(cruncherName).add((T)new ResumeWithProbability(resume.getUserId(), resume.getStarRating()));
        else
            getResumes().get(cruncherName).add((T)resume.getUserId() );
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
