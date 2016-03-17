package org.metplus.curriculum.process;

import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CruncherMetaData;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.MetaData;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.exceptions.DocumentParseException;
import org.metplus.curriculum.parsers.DocumentParserImpl;
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
    @Autowired
    ResumeRepository resumeRepository;

    @PostConstruct
    public void postConstructor() {
        resumes = new ArrayDeque<>();
        start();
    }

    public void run() {
        while(true) {
            logger.debug("Going to check resumes");
            Resume resume = null;
            while ((resume = nextResume()) != null) {
                logger.info("Going to process the resume for: " + resume.getUserId());
                DocumentParserImpl docParser = null;
                try {
                    docParser = new DocumentParserImpl(resume.getResume(springMongoConfig));
                    docParser.parse();
                    Map<String, MetaData> allMetaData = new HashMap<>();
                    for(Cruncher cruncher: allCrunchers.getCrunchers()) {
                        MetaData metaData = (MetaData) cruncher.crunch(docParser.getDocument());
                        allMetaData.put(cruncher.getCruncherName(), metaData);
                    }
                    resume.setMetaData(allMetaData);
                    resumeRepository.save(resume);
                    logger.info("Done processing resume for: " + resume.getUserId());
                } catch (ResumeNotFound resumeNotFound) {
                    logger.error("Unable to find the resume: " + resume);
                } catch (ResumeReadException e) {
                    logger.error("Problem reading the resume: " + resume + ". " + e);
                    resumes.addLast(resume);
                } catch (DocumentParseException e) {
                    logger.error("Problem Parsing the resume: " + resume + ". " + e);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void start() {
        cruncher = new Thread(){
            public void run() {
                ResumeCruncher.this.run();
            }
        };
        cruncher.start();
    }
    public synchronized void addResume(Resume resume) {
        resumes.add(resume);
    }
    protected synchronized Resume nextResume() {
        try {
            return resumes.remove();
        } catch(NoSuchElementException exp) {
            return null;
        }
    }
}
