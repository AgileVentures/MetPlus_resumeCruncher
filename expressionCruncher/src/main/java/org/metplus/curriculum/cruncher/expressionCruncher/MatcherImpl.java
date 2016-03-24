package org.metplus.curriculum.cruncher.expressionCruncher;

import org.apache.poi.ss.formula.functions.T;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.database.domain.DocumentWithMetaData;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.MetaDataField;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Joao on 3/21/16.
 * Class that implement the Matcher for Resumes using the Expression Cruncher
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
        logger.trace("match(" + title + ", " + description + ")");
        // Retrieve all the resumes
        List<Resume> resumes = resumeRepository.resumesOnCriteria(new ResumeComparator());
        // Crunch the title
        String titleExpression = ((ExpressionCruncherMetaData)cruncher.crunch(title)).getMostReferedExpression();
        // Crunch the description
        String descriptionExpression = ((ExpressionCruncherMetaData)cruncher.crunch(description)).getMostReferedExpression();
        List<Resume> resultTitle = new ArrayList<>();
        List<Resume> resultDescription = new ArrayList<>();
        for(Resume resume: resumes) {
            logger.debug("Checking viability of the resume: " + resume);
            // Retrieve the meta data of the resume
            ExpressionCruncherMetaData metaDataCruncher = (ExpressionCruncherMetaData)resume.getCruncherData(cruncher.getCruncherName());
            if(metaDataCruncher == null)
                continue;
            String resumeExpression = metaDataCruncher.getMostReferedExpression();
            if(resumeExpression == null || resumeExpression.length() == 0)
                resumeExpression = resume.getCruncherData(cruncher.getCruncherName())
                                         .getOrderedFields(new ResumeFieldComparator()).get(0).getKey();
            // Does resume and title have the same most common expression
            if(resumeExpression.compareTo(titleExpression) == 0) {
                logger.debug("Resume checks up with the title");
                resultTitle.add(resume);
                // Does resume and description have the same most common expression
            } else if(resumeExpression.compareTo(descriptionExpression) == 0) {
                logger.debug("Resume checks up with the description");
                resultDescription.add(resume);
            }
        }
        // Sort the resumes that match title to have on top the one with more expressions
        Collections.sort(resultTitle, new ResumeSorter());
        // Sort the resumes that match description to have on top the one with more expressions
        Collections.sort(resultDescription, new ResumeSorter());
        resultTitle.addAll(resultDescription);
        return resultTitle;
    }

    @Override
    public List<Job> match(CruncherMetaData metadata) {
        ExpressionCruncherMetaData auxMetaData = (ExpressionCruncherMetaData)metadata;
        List<Job> result = new ArrayList<>();
        for(Job job: jobRepository.findAll()) {
            ExpressionCruncherMetaData jobMetaData = (ExpressionCruncherMetaData)job.getCruncherData(getCruncherName());
            if(jobMetaData.getMostReferedExpression().equals(auxMetaData.getMostReferedExpression()))
                result.add(job);
        }
        Collections.sort(result, new JobSorter());
        return result;
    }

    @Override
    public String getCruncherName() {
        return cruncher.getCruncherName();
    }

    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private static class ResumeFieldComparator implements Comparator<Map.Entry<String, MetaDataField>> {

        @Override
        public int compare(Map.Entry<String, MetaDataField> o1, Map.Entry<String, MetaDataField> o2) {
            int left = (Integer) o1.getValue().getData();
            int right = (Integer) o2.getValue().getData();
            if(left < right)
                return 1;
            else if(left > right)
                return -1;
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass().getName().equals(Resume.class.getName());
        }
    }

    /**
     * Class that will compare the resumes
     * to group them by most common expression
     */
    private class ResumeComparator implements Comparator<Resume>{

        @Override
        public int compare(Resume o1, Resume o2) {
            List<Map.Entry<String, MetaDataField>> leftFields  = o1.getCruncherData(cruncher.getCruncherName())
                                                                   .getOrderedFields(new ResumeFieldComparator());
            List<Map.Entry<String, MetaDataField>> rightFields = o2.getCruncherData(cruncher.getCruncherName())
                                                                   .getOrderedFields(new ResumeFieldComparator());
            if(leftFields.size() == 0)
                return -1;
            if(rightFields.size() == 0)
                return 1;
            return leftFields.get(0).getKey().compareTo(rightFields.get(0).getKey());
        }
    }
    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private class EntitySorter<T extends DocumentWithMetaData> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            String field = ((ExpressionCruncherMetaData)o1.getCruncherData(cruncher.getCruncherName()))
                    .getMostReferedExpression();
            int left = (Integer) o1.getCruncherData(cruncher.getCruncherName())
                    .getFields().get(field).getData();
            int right = (Integer) o2.getCruncherData(cruncher.getCruncherName())
                    .getFields().get(field).getData();
            if(left < right)
                return 1;
            else if(left > right)
                return -1;
            return 0;
        }
    }
    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private class ResumeSorter extends EntitySorter<Resume> {
    }
    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private class JobSorter extends EntitySorter<Job> {
    }
}