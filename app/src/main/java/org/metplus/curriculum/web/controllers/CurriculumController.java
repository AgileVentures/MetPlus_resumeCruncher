package org.metplus.curriculum.web.controllers;


import org.metplus.curriculum.cruncher.Cruncher;
import org.metplus.curriculum.cruncher.CrunchersList;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.cruncher.ResumeMatcher;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.ResumeCruncher;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import  org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(BaseController.baseUrl + "/curriculum")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class CurriculumController {
    private static final Logger LOG = Logger.getLogger(CurriculumController.class);

    @Autowired
    private ResumeCruncher resumeCruncher;

    @Autowired
    ResumeRepository resumeRepository;

    @Autowired
    SpringMongoConfig dbConfig;

    @Autowired
    private MatcherList matcherList;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> uploadCurriculum(
                @RequestParam("userId") String id,
                @RequestParam("name") String name,
                @RequestParam("file") MultipartFile file) {
        System.out.println("Inside function");
        LOG.debug("File '" + name + "' is being uploaded to user: '" + id + "'");
        GenericAnswer answer = new GenericAnswer();

        Resume resume = resumeRepository.findByUserId(id);
        if(resume == null) {
            LOG.debug("No previous resume on the system");
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
                System.out.println(e.getStackTrace());
                e.printStackTrace();
                answer.setMessage("Error uploading the file: " + e.getMessage());
                answer.setResultCode(ResultCodes.FATAL_ERROR);
                LOG.warn("Error uploading file: '" + name + "' of user: '" + id + "': " + e.getMessage());
            }
        } else {
            LOG.info("File: '" + name + "' of user: '" + id + "' is empty!");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            answer.setMessage("File is empty");
        }

        LOG.debug("Result:" + answer);

        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCurriculum(
            @PathVariable("userId") String id,
            HttpServletResponse response) {
        LOG.debug("Retrieving curriculum of user: '" + id + "'");
        GenericAnswer answer = new GenericAnswer();

        Resume resume = resumeRepository.findByUserId(id);
        if(resume == null) {
            LOG.warn("Unable to find user: '" + id + "'");
            answer.setMessage("Unable to find the user: '" + id + "'");
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
            return new ResponseEntity<GenericAnswer>(answer, HttpStatus.OK);
        }
        try {
            ByteArrayOutputStream file = resume.getResume(dbConfig);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + resume.getFilename() +"\""));
            FileCopyUtils.copy(file.toByteArray(), response.getOutputStream());
            response.setContentLength(file.size());
            response.flushBuffer();
            return null;
        } catch (ResumeNotFound resumeNotFound) {
            LOG.warn("Unable to find curriculum for user: '" + id + "'");
            answer.setMessage("Unable to find the resume for the user");
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
        } catch (ResumeReadException e) {
            LOG.error("Error retrieving the resume for user: '" + id + "'");
            LOG.error(e.getStackTrace());
            answer.setMessage("Unable to retrieve resume");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
        } catch (IOException e) {
            LOG.error("Error reading resume for user: '" + id + "'");
            LOG.error(e.getStackTrace());
            answer.setMessage("Unable to read resume");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
        }

        return new ResponseEntity<GenericAnswer>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/match", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> match(@RequestParam("title") final String title,
                                               @RequestParam("description") final String description) {
        if(title.length() == 0) {
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("Title cannot be empty");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        }
        List<Resume> matchedResumes = new ArrayList<>();
        MatchAnswer answer = new MatchAnswer();
        for(ResumeMatcher matcher: matcherList.getMatchers()) {
            matchedResumes = matcher.match(title, description);
            for(Resume resume: matchedResumes) {
                answer.addResumes(matcher.getCruncherName(), resume);
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
