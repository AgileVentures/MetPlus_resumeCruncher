package org.metplus.curriculum.process;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.DocumentWithMetaData;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.exceptions.DocumentParseException;
import org.metplus.curriculum.parsers.DocumentParserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by joao on 3/16/16.
 */
@Component("jobCruncher")
public class JobCruncher extends ProcessCruncher<Job>{
    private static final Logger logger = LoggerFactory.getLogger(JobCruncher.class);

    @Autowired
    JobRepository jobRepository;

    @Override
    protected void process(Job job) {
        Map<String, MetaData> allDescriptionMetaData = new HashMap<>();
        Map<String, MetaData> allTitleMetaData = new HashMap<>();
        for(Cruncher cruncher: allCrunchers.getCrunchers()) {
            MetaData titleMetaData = (MetaData) cruncher.crunch(job.getTitle());
            MetaData descriptionMetaData = (MetaData) cruncher.crunch(job.getDescription());
            allTitleMetaData.put(cruncher.getCruncherName(), titleMetaData);
            allDescriptionMetaData.put(cruncher.getCruncherName(), descriptionMetaData);
        }
        DocumentWithMetaData titleData = new DocumentWithMetaData();
        titleData.setMetaData(allTitleMetaData);
        job.setTitleMetaData(titleData);
        DocumentWithMetaData descriptionData = new DocumentWithMetaData();
        descriptionData.setMetaData(allDescriptionMetaData);
        job.setDescriptionMetaData(descriptionData);
        jobRepository.save(job);
    }
}
