package org.metplus.curriculum.web.controllers;


import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import  org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(BaseController.baseUrl + "/curriculum")
public class CurriculumController {
    private static final Logger LOG = Logger.getLogger(CurriculumController.class);

    @Autowired
    ResumeRepository resumeRepository;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> uploadCurriculum(
                @RequestParam("userId") String id,
                @RequestParam("name") String name,
                @RequestParam("file") MultipartFile file) {
        System.out.println("Inside function");
        LOG.debug("File '" + name + "' is being uploaded to user: '" + id + "'");
        GenericAnswer answer = new GenericAnswer();

        Resume resume = resumeRepository.findOne(id);
        if(resume == null) {
            LOG.debug("No previous resume on the system");
            resume = new Resume(id);
        }
        if (!file.isEmpty()) {
            try {
                resume.setFilename(name);
                resume.setResume(file.getInputStream());
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
}
