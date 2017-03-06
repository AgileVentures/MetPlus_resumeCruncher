package org.metplus.curriculum.web.answers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metplus.curriculum.web.StarFormater;

import java.util.HashMap;
import java.util.Map;

public class StarAnswer extends GenericAnswer {
    private Map<String, Double> stars;

    /**
     * Retrieve all Stars information
     * @return Map with Stars that match per matcher
     */
    public Map<String, Double> getStars() {
        if(stars == null)
            stars = new HashMap<>();
        return stars;
    }
    /**
     * Set the Stars that match
     * @param stars Map with Stars that match per matcher
     */
    public void setStars(Map<String, Double> stars) {
        this.stars = stars;
    }

    /**
     * Add a Resume that matches
     * @param cruncherName Cruncher name
     * @param starRating Start rating
     */
    public void addStarRating(String cruncherName, double starRating) {
        getStars().put(cruncherName, StarFormater.format(starRating));
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "StarAnswer:";
        }
    }
}
