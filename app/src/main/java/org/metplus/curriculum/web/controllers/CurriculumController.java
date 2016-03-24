package org.metplus.curriculum.web.controllers;


import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.ResumeCruncher;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(BaseController.baseUrl + "/curriculum")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class CurriculumController {
    private static Logger logger = LoggerFactory.getLogger(CurriculumController.class);

    @Autowired
    private ResumeCruncher resumeCruncher;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private SpringMongoConfig dbConfig;

    @Autowired
    private MatcherList matcherList;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> uploadCurriculum(
                @RequestParam("userId") String id,
                @RequestParam("name") String name,
                @RequestParam("file") MultipartFile file) {
        System.out.println("Inside function");
        logger.debug("File '" + name + "' is being uploaded to user: '" + id + "'");
        GenericAnswer answer = new GenericAnswer();

        Resume resume = resumeRepository.findByUserId(id);
        if(resume == null) {
            logger.debug("No previous resume on the system");
            resume = new Resume(id);
        }
        if (!file.isEmpty()) {
            try {
                resume.setFilename(name);
                resume.setResume(file.getInputStream(), dbConfig);
                String[] fullName = name.split("\\.");
                resume.setFileType(fullName[fullName.length-1]);
                resumeRepository.save(resume);
                answer.setMessage("File uploaded successfully");
                answer.setResultCode(ResultCodes.SUCCESS);
                resumeCruncher.addResume(resume);
            } catch (Exception e) {
                e.printStackTrace();
                answer.setMessage("Error uploading the file: " + e.getMessage());
                answer.setResultCode(ResultCodes.FATAL_ERROR);
                logger.warn("Error uploading file: '" + name + "' of user: '" + id + "': " + e.getMessage());
            }
        } else {
            logger.info("File: '" + name + "' of user: '" + id + "' is empty!");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            answer.setMessage("File is empty");
        }

        logger.debug("Result:" + answer);

        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCurriculum(
            @PathVariable("userId") String id,
            HttpServletResponse response) {
        logger.debug("Retrieving curriculum of user: '" + id + "'");
        GenericAnswer answer = new GenericAnswer();

        Resume resume = resumeRepository.findByUserId(id);
        if(resume == null) {
            logger.warn("Unable to find user: '" + id + "'");
            answer.setMessage("Unable to find the user: '" + id + "'");
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
        try {
            ByteArrayOutputStream file = resume.getResume(dbConfig);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + resume.getFilename() +"\"");
            FileCopyUtils.copy(file.toByteArray(), response.getOutputStream());
            response.setContentLength(file.size());
            response.flushBuffer();
            return null;
        } catch (ResumeNotFound resumeNotFound) {
            logger.warn("Unable to find curriculum for user: '" + id + "'");
            answer.setMessage("Unable to find the resume for the user");
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
        } catch (ResumeReadException e) {
            logger.error("Error retrieving the resume for user: '" + id + "'");
            logger.error(String.valueOf(e.getStackTrace()));
            answer.setMessage("Unable to retrieve resume");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
        } catch (IOException e) {
            logger.error("Error reading resume for user: '" + id + "'");
            logger.error(String.valueOf(e.getStackTrace()));
            answer.setMessage("Unable to read resume");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
        }

        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/match", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> match(@RequestParam("title") final String title,
                                               @RequestParam("description") final String description) {
        logger.debug("Match resumes with title: '" + title + "', description: '" + description + "'");
        if(title == null || title.length() == 0) {
            logger.error("Matching resumes with empty Title is not allowed");
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("Title cannot be empty");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        }
        List<Resume> matchedResumes = null;
        MatchAnswer answer = new MatchAnswer();
        for(Matcher matcher: matcherList.getMatchers()) {
            matchedResumes = matcher.match(title, description);
            for(Resume resume: matchedResumes) {
                answer.addResumes(matcher.getCruncherName(), resume);
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
