package org.metplus.curriculum.cruncher.naivebayes;

import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.database.domain.*;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class MatcherImpl implements Matcher<Resume, Job> {

    private JobRepository jobRepository;
    private CruncherImpl cruncher;
    private ResumeRepository resumeRepository;
    private final Double[][] matchPoints = {
            {1600., 1200., 1000., 900., 850.},
            {750., 800., 600., 500., 450.},
            {700., 350., 400., 300., 250.},
            {600., 300., 150., 200., 150.},
            {400., 200., 100., 50., 100.},
    };
    private static final Double maxPoints = 1600. + 800. + 400. + 200. + 100.;
    private static final int MAX_NUMBER_CATEGORIES = 5;

    MatcherImpl(CruncherImpl cruncher, JobRepository jobRepository, ResumeRepository resumeRepository) {
        this.jobRepository = jobRepository;
        this.cruncher = cruncher;
        this.resumeRepository = resumeRepository;
    }

    @Override
    public List<Job> match(Resume resume) {
        List<Job> result = new ArrayList<>();
        if (!isResumeValid(resume))
            return result;

        List<String> resumeCategories = getCategoryListFromMetaData(resume, MAX_NUMBER_CATEGORIES);

        List<Job> allJobs = (List<Job>) jobRepository.findAll();
        for (Job job : allJobs) {
            List<String> jobCategories = getJobCategories(job);

            double starRating = calculateStarRating(resumeCategories, jobCategories);

            if (starRating > 0) {
                job.setStarRating(starRating);
                result.add(job);
            }
        }
        return result;
    }

    @Override
    public List<Resume> matchInverse(Job job) {
        List<Resume> results = new ArrayList<>();
        if (!isJobValid(job))
            return results;
        List<Resume> allResumes = (List<Resume>) resumeRepository.findAll();

        List<String> jobCategories = getJobCategories(job);

        for(Resume resume: allResumes) {
            List<String> resumeCategories = getCategoryListFromMetaData(resume, MAX_NUMBER_CATEGORIES);

            double starRating = calculateStarRating(jobCategories, resumeCategories);
            if(starRating > 0) {
                resume.setStarRating(starRating);
                results.add(resume);
            }
        }

        return results;
    }

    @Override
    public String getCruncherName() {
        return cruncher.getCruncherName();
    }

    @Override
    public double matchSimilarity(Resume resume, Job job) {
        if(!isResumeValid(resume) || !isJobValid(job))
            return 0;

        List<String> jobCategories = getJobCategories(job);
        List<String> resumeCategories = getCategoryListFromMetaData(resume, MAX_NUMBER_CATEGORIES);

        return calculateStarRating(resumeCategories, jobCategories);
    }

    private List<String> getJobCategories(Job job) {
        List<String> jobCategories = getCategoryListFromMetaData(job.getTitleMetaData(), 2);
        jobCategories.addAll(getCategoryListFromMetaData(job.getDescriptionMetaData(),
                MAX_NUMBER_CATEGORIES - jobCategories.size()));
        return jobCategories;
    }

    private List<String> getCategoryListFromMetaData(DocumentWithMetaData resume, int limit) {
        return stream(resume
                .getCruncherData(cruncher.getCruncherName())
                .getOrderedFields(new DoubleFieldComparator()).toArray())
                .map(category -> {
                    String categoryName = ((Map.Entry<String, Double>) category).getKey();
                    return categoryName.replaceAll("_job|_resume", "");
                })
                .distinct()
                .limit(limit).collect(Collectors.toList());
    }

    private boolean isResumeValid(Resume resume) {
        return !(resume == null ||
                resume.getMetaData().size() == 0 ||
                resume.getMetaData().get(cruncher.getCruncherName())
                        .getFields().size() == 0);
    }

    private boolean isJobValid(Job job) {
        return !(job == null
                || !job.haveCruncherData(cruncher.getCruncherName()));
    }

    double calculateStarRating(List<String> base, List<String> compare) {
        double probability = 0;
        int i = 0;
        for (String strToFind : compare) {
            if (base.contains(strToFind)) {
                probability += matchPoints[base.indexOf(strToFind)][i];
            }
            i++;
        }
        return probability / maxPoints * 5;
    }

    /**
     * Class that will compare the fields on the resume meta data
     * to order them by most common expression
     */
    private static class DoubleFieldComparator implements Comparator<Map.Entry<String, MetaDataField>> {

        @Override
        public int compare(Map.Entry<String, MetaDataField> o1, Map.Entry<String, MetaDataField> o2) {
            double left = (Double) o1.getValue().getData();
            double right = (Double) o2.getValue().getData();
            if (left < right)
                return 1;
            else if (left > right)
                return -1;
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass().getName().equals(Resume.class.getName());
        }
    }
}

