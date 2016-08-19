package org.metplus.curriculum.cruncher.naivebayes;

import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaopereira on 8/16/2016.
 * Class that implement the Matcher for Resumes using the Naive Bayes
 */
public class MatcherImpl implements Matcher<Resume, Job> {
    private static Logger logger = LoggerFactory.getLogger(MatcherImpl.class);
    private ResumeRepository resumeRepository;
    private CruncherImpl cruncher;
    private JobRepository jobRepository;

    /**
     * Class constructor
     * @param cruncher Cruncher implementation
     * @param resumeRepository Resume repository to retrieve the resumes
     */
    public MatcherImpl(CruncherImpl cruncher, ResumeRepository resumeRepository, JobRepository jobRepository) {
        this.cruncher = cruncher;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Resume> match(String title, String description) {
        logger.trace("match(" + title + "," + description + ")");
        NaiveBayesMetaData titleMetaData =(NaiveBayesMetaData)cruncher.crunch(title);
        NaiveBayesMetaData descriptionMetaData = (NaiveBayesMetaData)cruncher.crunch(description);
        if(titleMetaData == null || descriptionMetaData == null)
            return null;
        return matchResumes(titleMetaData, descriptionMetaData);
    }

    @Override
    public List<Resume> match(Job job) {
        logger.trace("match(" + job + ")");
        if(job == null)
            return null;
        if(job.getTitleCruncherData(getCruncherName()) == null)
            return null;
        if(job.getDescriptionCruncherData(getCruncherName()) == null)
            return null;

        return matchResumes((NaiveBayesMetaData)job.getTitleCruncherData(getCruncherName()),
                     (NaiveBayesMetaData)job.getDescriptionCruncherData(getCruncherName()));
    }
    private List<Resume> matchResumes(NaiveBayesMetaData titleMetaData, NaiveBayesMetaData descriptionMetaData) {
        logger.trace("matchResumes(" + titleMetaData + ", " + descriptionMetaData + ")");
        List<Resume> result = new ArrayList<>();
        for(Resume resume: resumeRepository.findAll()) {
            logger.debug("trying to match with resume: " + resume.getUserId());
            NaiveBayesMetaData data = (NaiveBayesMetaData)resume.getCruncherData(getCruncherName());
            if(data == null) {
                logger.warn("Resume with id: " + resume.getUserId() + " do not have meta data for cruncher");
                continue;
            }
            data.
        }
        return result;
    }

    @Override
    public List<Job> match(CruncherMetaData metadata) {
        logger.trace("match(" + metadata + ")");
        return null;
    }

    @Override
    public String getCruncherName() {
        return cruncher.getCruncherName();
    }
}
