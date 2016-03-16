package org.metplus.curriculum.process;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by joao on 3/16/16.
 */
@Component
public class ResumeCruncher {
    private static final Logger logger = LoggerFactory.getLogger(ResumeCruncher.class);
    private Thread cruncher;
    Deque<Resume> resumes;
    @Autowired
    private CrunchersList allCrunchers;
    @Autowired
    private SpringMongoConfig springMongoConfig;

    @PostConstruct
    public void postConstructor(){
        resumes = new ArrayDeque<>();
    }

    public void run() {
        while(true) {
            while (resumes.size() > 0) {
                Resume resume = resumes.remove();
                try {
                    Map<String, MetaData> allMetaData = new HashMap<>();
                    for(Cruncher cruncher: allCrunchers.getCrunchers()) {
                            MetaData metaData = (MetaData) cruncher.crunch(resume.getResume(springMongoConfig).toString());
                        allMetaData.put(cruncher.getCruncherName(), metaData);
                    }
                    resume.setMetaData(allMetaData);
                } catch (ResumeNotFound resumeNotFound) {
                    logger.error("Unable to find the resume: " + resume);
                } catch (ResumeReadException e) {
                    logger.error("Problem reading the resume: " + resume + ". " + e);
                    resumes.addLast(resume);
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
