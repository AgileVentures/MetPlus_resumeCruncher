package org.metplus.curriculum.web.controllers;


import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.exceptions.CurriculumException;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import  org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequestMapping(BaseController.baseUrl + "/curriculum")
public class CurriculumController {
    private static final Logger LOG = Logger.getLogger(CurriculumController.class);

    @Autowired
    ResumeRepository resumeRepository;

    @Autowired
    SpringMongoConfig dbConfig;

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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCurriculum(
            @RequestParam("userId") String id,
            HttpServletResponse response) {
        LOG.debug("Retrieving curriculum of user: '" + id + "'");
        GenericAnswer answer = new GenericAnswer();

        Resume resume = resumeRepository.findByUserId(id);
        if(resume == null) {
            answer.setMessage("Unable to find the user: '" + id + "'");
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
            return new ResponseEntity<GenericAnswer>(answer, HttpStatus.OK);
        }
        ByteArrayOutputStream file = null;
        try {
            file = resume.getResume(dbConfig);

            HttpHeaders respHeaders = new HttpHeaders();
            //respHeaders.setContentType(new MediaType("application", resume.getFileType()));
            respHeaders.setContentType(new MediaType("application", "file"));
            respHeaders.setContentLength(file.size());
            respHeaders.setContentDispositionFormData("attachment", resume.getFilename());
            FileCopyUtils.copy(file.toByteArray(), response.getOutputStream());
            response.setContentLength(file.size());
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "inline; filename=\"" + resume.getFilename() + "\"");
            response.flushBuffer();
            LOG.debug(response.getHeader("Content-Disposition"));

            return null;
        } catch (ResumeNotFound resumeNotFound) {
            answer.setMessage("Unable to find the resume for the user");
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
        } catch (ResumeReadException e) {
            LOG.error("Error retrieving the resume for user: '" + id + "'");
            LOG.error(e.getStackTrace());
            answer.setMessage("Unable to retrieve resume");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOG.debug("Result:" + answer);

        return new ResponseEntity<GenericAnswer>(answer, HttpStatus.OK);
    }
}
