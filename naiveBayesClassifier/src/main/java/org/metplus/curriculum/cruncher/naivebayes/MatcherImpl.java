package org.metplus.curriculum.cruncher.naivebayes;

import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.MetaDataField;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by joaopereira on 8/16/2016.
 * Class that implement the Matcher for Resumes using the Naive Bayes
 */
public class MatcherImpl implements Matcher<Resume, Job> {
    private static Logger logger = LoggerFactory.getLogger(MatcherImpl.class);
    private ResumeRepository resumeRepository;
    private CruncherImpl cruncher;
    private JobRepository jobRepository;

    private final int[] weightMatrix = {800, 400, 200, 100, 50};

    public static int NUM_STARS = 5;

    private int maxWeight;

    /**
     * Class constructor
     * @param cruncher Cruncher implementation
     * @param resumeRepository Resume repository to retrieve the resumes
     */
    public MatcherImpl(CruncherImpl cruncher, ResumeRepository resumeRepository, JobRepository jobRepository) {
        this.cruncher = cruncher;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        for(int weight : weightMatrix)
            maxWeight += weight * 2;
    }

    @Override
    public List<Resume> match(String title, String description) {
        logger.trace("match(" + title + "," + description + ")");
        NaiveBayesMetaData titleMetaData =(NaiveBayesMetaData)cruncher.crunch(title);
        NaiveBayesMetaData descriptionMetaData = (NaiveBayesMetaData)cruncher.crunch(description);
        if(titleMetaData == null || descriptionMetaData == null)
            return null;

        if(cruncher.getCategories().contains(title)){
            titleMetaData.setBestMatchCategory(title);
        }
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
        NaiveBayesMetaData titleData = (NaiveBayesMetaData)job.getTitleCruncherData(getCruncherName());
        if(cruncher.getCategories().contains(job.getTitle()))
            titleData.setBestMatchCategory(job.getTitle());
        return matchResumes(titleData,
                     (NaiveBayesMetaData)job.getDescriptionCruncherData(getCruncherName()));
    }
    private List<Resume> matchResumes(NaiveBayesMetaData titleMetaData, NaiveBayesMetaData descriptionMetaData) {
        logger.trace("matchResumes(" + titleMetaData + ", " + descriptionMetaData + ")");
        List<Resume> result = new ArrayList<>();
        List<Map.Entry<String, MetaDataField>> topCategoriesToMatch = getTopCategoriesOnJobs(titleMetaData, descriptionMetaData);
        List<String> topCategoriesNamesToMatch = new ArrayList<>();
        for(Map.Entry<String, MetaDataField> entry: topCategoriesToMatch) {
            topCategoriesNamesToMatch.add(entry.getKey());
            if(topCategoriesNamesToMatch.size() == weightMatrix.length)
                break;
        }

        for(Resume resume: resumeRepository.findAll()) {
            logger.debug("trying to match with resume: " + resume.getUserId());
            NaiveBayesMetaData data = (NaiveBayesMetaData)resume.getCruncherData(getCruncherName());
            if(data == null) {
                logger.warn("Resume with id: " + resume.getUserId() + " do not have meta data for cruncher");
                continue;
            }
            double probability =  matchProbability(topCategoriesNamesToMatch, data);
            if(probability > 0) {
                resume.setStarRating(probability * NUM_STARS);
                result.add(resume);
            }
        }
        return result;
    }


    @Override
    public List<Job> match(CruncherMetaData metadata) {
        logger.trace("match(" + metadata + ")");
        if(metadata == null) {
            logger.warn("Metadata is null");
            return null;
        }
        List<String> topResumeCategories = new ArrayList<>();
        for(Map.Entry<String, MetaDataField> entry:
                ((NaiveBayesMetaData)metadata).getOrderedFields(new DoubleFieldComparator()) ) {
            topResumeCategories.add(entry.getKey());
            if(topResumeCategories.size() == weightMatrix.length)
                break;
        }

        logger.debug("Top categories are: " + topResumeCategories.toString());

        List<Job> result = new ArrayList<>();
        for(Job job: jobRepository.findAll()) {
            logger.debug("Check job: " + job.getJobId());
            NaiveBayesMetaData titleMetaData = (NaiveBayesMetaData) job.getTitleCruncherData(getCruncherName());
            NaiveBayesMetaData descriptionMetaData = (NaiveBayesMetaData) job.getDescriptionCruncherData(getCruncherName());
            if(titleMetaData == null) {
                logger.warn("Job " + job.getJobId() + " does not have meta data");
                continue;
            }
            if(descriptionMetaData == null)
                descriptionMetaData = new NaiveBayesMetaData();

            List<Map.Entry<String, MetaDataField>> topCategoriesToMatch =
                    getTopCategoriesOnJobs(titleMetaData, descriptionMetaData);
            List<String> jobCategories = new ArrayList<>();
            for(Map.Entry<String, MetaDataField> category: topCategoriesToMatch) {
                jobCategories.add(category.getKey());
            }
            double probability = matchProbability(topResumeCategories, jobCategories);
            if(probability > 0) {
                job.setStarRating(probability * NUM_STARS);
                result.add(job);
            }
        }
        return result;
    }

    @Override
    public String getCruncherName() {
        return cruncher.getCruncherName();
    }


    @Override
    public double matchSimilarity(Resume resume, Job job) {
        logger.trace("matchResumes(" + resume + ", " + job + ")");
        NaiveBayesMetaData data = (NaiveBayesMetaData)resume.getCruncherData(getCruncherName());
        if(data == null) {
            logger.warn("Resume with id: " + resume.getUserId() + " do not have meta data for cruncher");
            return -1;
        }
        NaiveBayesMetaData titleMetaData = (NaiveBayesMetaData)job.getTitleCruncherData(getCruncherName());
        NaiveBayesMetaData descriptionMetaData = (NaiveBayesMetaData)job.getDescriptionCruncherData(getCruncherName());
        if(titleMetaData == null)
            titleMetaData = new NaiveBayesMetaData();
        if(descriptionMetaData == null)
            descriptionMetaData = new NaiveBayesMetaData();

        List<Map.Entry<String, MetaDataField>> topCategoriesToMatch = getTopCategoriesOnJobs(titleMetaData, descriptionMetaData);
        List<String> topCategoriesNamesToMatch = new ArrayList<>();
        for(Map.Entry<String, MetaDataField> entry: topCategoriesToMatch) {
            topCategoriesNamesToMatch.add(entry.getKey());
            if(topCategoriesNamesToMatch.size() == weightMatrix.length)
                break;
        }

        double probability =  matchProbability(topCategoriesNamesToMatch, data);
        if(probability <= 0) {
            probability = 0;
        } else {
            probability *= NUM_STARS;
        }
        return probability;
    }

    protected double matchProbability(List<String> base, NaiveBayesMetaData compareTo) {
        double total = 0.0f;
        // Retrieve the categories that are also present in the title and description
        List<String> topResumeFields = new ArrayList<> ();
        boolean foundGoodCategory = false;
        for(Map.Entry<String, MetaDataField> category: compareTo.getOrderedFields(new DoubleFieldComparator())) {
            if(base.contains(category.getKey())) {
                topResumeFields.add(category.getKey());
                foundGoodCategory = true;
            } else
                topResumeFields.add("");
            if(topResumeFields.size() == weightMatrix.length)
                break;
        }
        // Check if there is anything in common if no continue to next resume
        if(!foundGoodCategory) {
            logger.trace("No categories in common");
            return -1;
        }
        return matchProbability(base, topResumeFields);
    }

    protected double matchProbability(List<String> base, List<String> compareTo) {
        double total = 0.0f;
        int currentIndex = 0;
        for(String categoryName: base) {
            int index = compareTo.indexOf(categoryName);
            if(index != -1) {
                total += weightMatrix[currentIndex] + ((currentIndex<index?-1:1) * weightMatrix[index]);
            }
            currentIndex++;
        }
        return total / maxWeight;
    }

    private List<Map.Entry<String, MetaDataField>> getTopCategoriesOnJobs(NaiveBayesMetaData titleMetaData, NaiveBayesMetaData descriptionMetaData){

        List<Map.Entry<String, MetaDataField>> titleTopCategories = titleMetaData.getOrderedFields(new DoubleFieldComparator());
        List<Map.Entry<String, MetaDataField>> descriptionTopCategories = descriptionMetaData.getOrderedFields(new DoubleFieldComparator());
        List<Map.Entry<String, MetaDataField>> topCategoriesToMatch = new ArrayList<>();
        List<String> alreadyPresent = new ArrayList<>();
        if(!titleTopCategories.get(0).getKey().equals(titleMetaData.getBestMatchCategory()) &&
                titleMetaData.getBestMatchCategory() != null) {
            topCategoriesToMatch.add(new AbstractMap.SimpleEntry(titleMetaData.getBestMatchCategory(), new MetaDataField<Double>(Double.POSITIVE_INFINITY)));
        }
        int i = 0;
        boolean pickTitle = true;
        boolean addedNew = true;
        while(topCategoriesToMatch.size() != 5 && addedNew) {
            addedNew = false;
            if(titleTopCategories.size() > i && pickTitle) {
                topCategoriesToMatch.add(titleTopCategories.get(i));
                alreadyPresent.add(titleTopCategories.get(i).getKey());
                addedNew = true;
            }
            if(descriptionTopCategories.size() > i && !pickTitle) {
                if(!alreadyPresent.contains(descriptionTopCategories.get(i).getKey())) {
                    topCategoriesToMatch.add(descriptionTopCategories.get(i));
                    alreadyPresent.add(descriptionTopCategories.get(i).getKey());
                }
                addedNew = true;
            }
            if(i == 1 && pickTitle) {
                pickTitle = false;
                i = -1;
                addedNew = true;
            }

            i++;
        }
        return topCategoriesToMatch;
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
}
