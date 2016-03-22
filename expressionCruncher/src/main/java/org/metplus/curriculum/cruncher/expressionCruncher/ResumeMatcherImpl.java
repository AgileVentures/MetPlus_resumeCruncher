package org.metplus.curriculum.cruncher.expressionCruncher;

import org.metplus.curriculum.cruncher.ResumeMatcher;
import org.metplus.curriculum.database.domain.MetaDataField;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by joao on 3/21/16.
 */
public class ResumeMatcherImpl implements ResumeMatcher<Resume> {
    private static Logger logger = LoggerFactory.getLogger(ResumeMatcherImpl.class);
    private ResumeRepository resumeRepository;
    private CruncherImpl cruncher;
    public ResumeMatcherImpl(CruncherImpl cruncher, ResumeRepository resumeRepository) {
        this.cruncher = cruncher;
        this.resumeRepository = resumeRepository;
    }

    @Override
    public List<Resume> match(String title, String description) {
        List<Resume> resumes = resumeRepository.resumesOnCriteria(new ResumeComparator());
        Map<String, MetaDataField> titleData = ((ExpressionCruncherMetaData)cruncher.crunch(title)).getFields();
        List<Map.Entry<String, MetaDataField>> titleOrderedData = new ArrayList<>(titleData.entrySet());
        Collections.sort(titleOrderedData, new ResumeFieldComparator());
        Map<String, MetaDataField> descriptionData = ((ExpressionCruncherMetaData)cruncher.crunch(description)).getFields();
        List<Map.Entry<String, MetaDataField>> descriptionOrderedData = new ArrayList<>(descriptionData.entrySet());
        Collections.sort(descriptionOrderedData, new ResumeFieldComparator());
        List<Resume> resultTitle = new ArrayList<Resume>();
        List<Resume> resultDescription = new ArrayList<>();
        for(Resume resume: resumes) {
            List<Map.Entry<String, MetaDataField>> data = resume.getCruncherData(cruncher.getCruncherName()).getOrderedFields(new ResumeFieldComparator());
            logger.info("Resume: " + data.get(0).getKey());
            logger.info("Title: " + titleOrderedData.get(0).getKey());
            logger.info("Description: " + descriptionOrderedData.get(0).getKey());
            if(data.get(0).getKey().compareTo(titleOrderedData.get(0).getKey()) == 0) {
                resultTitle.add(resume);
            } else if(data.get(0).getKey().compareTo(descriptionOrderedData.get(0).getKey()) == 0) {
                resultDescription.add(resume);
            }
        }
        Collections.sort(resultTitle, new ResumeSorter());
        Collections.sort(resultDescription, new ResumeSorter());
        resultTitle.addAll(resultDescription);
        return resultTitle;
    }

    public static class ResumeFieldComparator implements Comparator<Map.Entry<String, MetaDataField>> {

        @Override
        public int compare(Map.Entry<String, MetaDataField> o1, Map.Entry<String, MetaDataField> o2) {
            int left = ((Integer)o1.getValue().getData()).intValue();
            int right = ((Integer)o2.getValue().getData()).intValue();
            if(left < right)
                return 1;
            else if(left > right)
                return -1;
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj.getClass().getName() == Resume.class.getName())
                return true;
            return false;
        }
    }
    public class ResumeComparator implements Comparator<Resume>{

        @Override
        public int compare(Resume o1, Resume o2) {
            List<Map.Entry<String, MetaDataField>> leftFields  = o1.getCruncherData(cruncher.getCruncherName()).getOrderedFields(new ResumeFieldComparator());
            List<Map.Entry<String, MetaDataField>> rightFields = o2.getCruncherData(cruncher.getCruncherName()).getOrderedFields(new ResumeFieldComparator());
            if(leftFields.size() == 0)
                return -1;
            if(rightFields.size() == 0)
                return 1;
            return leftFields.get(0).getKey().compareTo(rightFields.get(0).getKey());
        }
    }
    public class ResumeSorter implements Comparator<Resume> {
        @Override
        public int compare(Resume o1, Resume o2) {
            String field = ((ExpressionCruncherMetaData)o1.getCruncherData(cruncher.getCruncherName())).getMostReferedExpression();
            int left = ((Integer)o1.getCruncherData(cruncher.getCruncherName()).getFields().get(field).getData()).intValue();
            int right = ((Integer)o2.getCruncherData(cruncher.getCruncherName()).getFields().get(field).getData()).intValue();
            if(left < right)
                return 1;
            else if(left > right)
                return -1;
            return 0;
        }
    }
}
