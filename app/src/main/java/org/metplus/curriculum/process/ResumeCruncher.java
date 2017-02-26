package org.metplus.curriculum.process;

import org.metplus.curriculum.cruncher.Cruncher;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Process that will be running in the background
 * to crunch the Resume information
 */
@Component("resumeCruncher")
public class ResumeCruncher extends ProcessCruncher<Resume>{
    private static final Logger logger = LoggerFactory.getLogger(ResumeCruncher.class);

    @Autowired
    private SpringMongoConfig springMongoConfig;

    @Autowired
    ResumeRepository resumeRepository;

    @Override
    protected void process(Resume resume) {
        logger.info("Going to process the resume for: " + resume.getUserId());
        DocumentParserImpl docParser = null;
        try {
            docParser = new DocumentParserImpl(resume.getResume(springMongoConfig));
            docParser.parse();
            logger.info("Document: " + docParser.getDocument().replaceAll("\n","\\\\n").replaceAll("\t", " "));
            Map<String, MetaData> allMetaData = new HashMap<>();
            for(Cruncher cruncher: allCrunchers.getCrunchers()) {
                MetaData metaData = (MetaData) cruncher.crunch(docParser.getDocument());
                allMetaData.put(cruncher.getCruncherName(), metaData);
            }
            resume.setMetaData(allMetaData);
            resumeRepository.save(resume);
            logger.info("Done processing resume for: " + resume.getUserId());
        } catch (ResumeNotFound resumeNotFound) {
            resumeNotFound.printStackTrace();
            logger.error("Unable to find the resume: " + resume);
        } catch (ResumeReadException e) {
            e.printStackTrace();
            logger.error("Problem reading the resume: " + resume + ". " + e);
        } catch (DocumentParseException e) {
            e.printStackTrace();
            logger.error("Problem Parsing the resume: " + resume + ". " + e);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error("Problem processing the resume: " + resume + ". " + e);
        }
        logger.info("Ended process of: " + resume.getUserId());
    }

}
