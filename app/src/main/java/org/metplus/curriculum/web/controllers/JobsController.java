package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
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
    public JobsController(){}
    public JobsController(JobRepository jobRepository, ResumeRepository resumeRepository) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
    }

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeRepository resumeRepository;


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
            logger.error("Job with job id '" + id + "' already exist");
            answer.setResultCode(ResultCodes.JOB_ID_EXISTS);
            answer.setMessage("Trying to create job that already exists");
        } else {
            logger.debug("Going to create the job");
            job = new Job();
            job.setJobId(id);
            job.setTitle(title);
            job.setDescription(description);
            try {
                jobRepository.save(job);
                logger.debug("Job added successfully");
                answer.setResultCode(ResultCodes.SUCCESS);
                answer.setMessage("Job added successfully");
            } catch(Exception exp) {
                logger.error("Unable to create the job '" + job + "' due to: " + exp.getMessage());
                answer.setResultCode(ResultCodes.FATAL_ERROR);
                answer.setMessage("Unable to create the job '" + job + "' due to: " + exp.getMessage());
            }
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
        Job job = jobRepository.findByJobId(jobId);
        if(job == null) {
            logger.error("Job with job id '" + jobId + "' do not exist");
            answer.setResultCode(ResultCodes.JOB_NOT_FOUND);
            answer.setMessage("Job not found");
        } else {
            logger.debug("Going to update the job");
            if(title != null)
                job.setTitle(title);
            if(description != null)
                job.setDescription(description);
            try {
                jobRepository.save(job);
                logger.debug("Job updated successfully");
                answer.setResultCode(ResultCodes.SUCCESS);
                answer.setMessage("Job updated successfully");
            } catch(Exception exp) {
                logger.error("Unable to save the job '" + job + "' due to: " + exp.getMessage());
                answer.setResultCode(ResultCodes.FATAL_ERROR);
                answer.setMessage("Unable to save the job '" + job + "' due to: " + exp.getMessage());
            }
        }
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
    @RequestMapping(value = "/match/{resumeId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> match(@PathVariable("resumeId") final String resumeId){
        logger.trace("match(" + resumeId + ")");
        GenericAnswer answer = new GenericAnswer();
        Resume resume = resumeRepository.findByUserId(resumeId);
        if(resume == null) {
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
            answer.setMessage("Cannot find the resume");
        }
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
