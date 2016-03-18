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
    private Deque<Resume> resumes;
    private boolean keepRunning = true;
    @Autowired
    private CrunchersList allCrunchers;
    @Autowired
    private SpringMongoConfig springMongoConfig;
    @Autowired
    ResumeRepository resumeRepository;

    /**
     * Post constructor function
     * The function will initialize the resumes and start
     * the thread that will crunch the resumes
     */
    @PostConstruct
    public void postConstructor() {
        resumes = new ArrayDeque<>();
        start();
    }

    /**
     * Main function that will be always running and crunching the resumes
     */
    public void run() {
        do {
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
                } catch (DocumentParseException e) {
                    logger.error("Problem Parsing the resume: " + resume + ". " + e);
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        } while(keepRunning);
    }

    /**
     * Stop the thread
     */
    public void stop(){
        setKeepRunning(false);
        cruncher.interrupt();
    }

    /**
     * Change the control variable of the thread
     * @param keepRunning True if the thread should keep on running, false otherwise
     */
    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    /**
     * Function to start the thread
     */
    public void start() {
        cruncher = new Thread(){
            public void run() {
                ResumeCruncher.this.run();
            }
        };
        cruncher.start();
    }

    /**
     * Add a new resume to be crunched by the thread
     * @param resume Resume to be added
     */
    public synchronized void addResume(Resume resume) {
        resumes.add(resume);
    }

    /**
     * Retrieve the next resume to be crunched
     * @return Resume
     */
    protected synchronized Resume nextResume() {
        try {
            return resumes.remove();
        } catch(NoSuchElementException exp) {
            return null;
        }
    }
}
