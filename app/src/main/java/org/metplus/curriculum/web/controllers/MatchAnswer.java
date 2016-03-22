package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.web.GenericAnswer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joao on 3/22/16.
 */
public class MatchAnswer extends GenericAnswer {

    Map<String, List<String>> resumes;

    public Map<String, List<String>> getResumes() {
        if(resumes == null)
            resumes = new HashMap<>();
        return resumes;
    }

    public void setResumes(Map<String, List<String>> resumes) {
        this.resumes = resumes;
    }

    public void addResumes(String cruncherName, Resume resume) {
        if(!getResumes().containsKey(cruncherName))
            getResumes().put(cruncherName, new ArrayList<>());
        getResumes().get(cruncherName).add(resume.getUserId());
    }
}
