package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller that will handle
 * all the requests related with jobs
 */
@RestController
@RequestMapping(BaseController.baseUrl + "job")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class JobsController {
    public JobsController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    @Autowired
    private JobRepository jobRepository;

    private static Logger logger = LoggerFactory.getLogger(JobsController.class);
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> create(
                                @RequestParam("jobId") String id,
                                @RequestParam("title") String title,
                                @RequestParam("description") String description) {
        logger.trace("create(" + id + ", " + title + ", " + description + ")");
        GenericAnswer answer = new GenericAnswer();
        Job job = jobRepository.findByJobId(id);
        if(job != null && job.getJobId().equals(id)) {
            answer.setResultCode(ResultCodes.JOB_ID_EXISTS);
            answer.setMessage("Trying to create job that already exists");
        }
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/{jobId}/update", method = RequestMethod.PATCH)
    @ResponseBody
    public ResponseEntity<GenericAnswer> update(final String jobId,
                                                @RequestParam(value = "title", required = false) String title,
                                                @RequestParam(value = "description", required = false) String description){
        logger.trace("update(" + jobId + ", " + title + ", " + description + ")");
        GenericAnswer answer = new GenericAnswer();
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
    @RequestMapping(value = "/match/{resumeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> match(final String resumeId){
        logger.trace("match(" + resumeId + ")");
        GenericAnswer answer = new GenericAnswer();
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
