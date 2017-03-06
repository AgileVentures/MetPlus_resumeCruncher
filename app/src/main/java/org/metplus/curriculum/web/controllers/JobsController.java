package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.api.APIVersion;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.JobCruncher;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.metplus.curriculum.web.answers.JobMatchAnswer;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller that will handle
 * all the requests related with jobs
 */
@RestController
@RequestMapping("job")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
@APIVersion({1, 2, BaseController.VERSION_TESTING})
public class JobsController {
    public JobsController(){}
    public JobsController(JobRepository jobRepository, ResumeRepository resumeRepository, MatcherList matcherList, JobCruncher jobCruncher) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.matcherList = matcherList;
        this.jobCruncher = jobCruncher;
    }

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private MatcherList matcherList;

    @Autowired
    private JobCruncher jobCruncher;


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
                jobCruncher.addWork(job);
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
    public ResponseEntity<GenericAnswer> update(@PathVariable("jobId") final String jobId,
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
                jobCruncher.addWork(job);
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
    @APIVersion({2})
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv2(@PathVariable("resumeId") final String resumeId){
        return match(resumeId, true);
    }
    @RequestMapping(value = "/match/{resumeId}", method = RequestMethod.GET)
    @APIVersion({1})
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv1(@PathVariable("resumeId") final String resumeId){
        return match(resumeId, false);
    }

    @RequestMapping(value = "/match/{resumeId}", method = RequestMethod.GET)
    @APIVersion({BaseController.VERSION_TESTING})
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchCannedAnswer(@PathVariable("resumeId") final String resumeId){

        final double[] jobStars = {1.8, 4.1, 2.6, 4.9, 3.2,
                1.8, 4.1, 2.6, 4.9, 3.2};
        JobMatchAnswer answer = new JobMatchAnswer();
        double jobIdentifiers = Double.valueOf(resumeId);
        if(jobIdentifiers > 0 && jobIdentifiers < 11) {
            for(int i = 0 ; i  < jobStars.length ; i++ ) {
                Job job = jobRepository.findByJobId(Integer.toString(i));
                if(job != null) {
                    job.setStarRating(jobStars[i]);
                    answer.addJob("NaiveBayes", job, true);
                }
            }
        } else if(jobIdentifiers % 5 != 0) {
            for(int i = 0 ; i < 4 ; i++) {
                Job job = jobRepository.findByJobId(Double.toString(jobIdentifiers + i));
                if(job != null) {
                    job.setStarRating(jobStars[i]);
                    answer.addJob("NaiveBayes", job, true);
                }
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    private ResponseEntity<GenericAnswer> match(String resumeId, boolean withProbabilityAnswer){
        logger.debug("match(" + resumeId + ", " + withProbabilityAnswer + ")");
        Resume resume = resumeRepository.findByUserId(resumeId);
        if(resume == null) {
            logger.warn("Unable to find resume with id '{}'", resumeId);
            GenericAnswer answer = new GenericAnswer();
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
            answer.setMessage("Cannot find the resume");
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
        logger.debug("Start processing the Jobs");
        List<Job> matchedJobs = null;
        JobMatchAnswer answer = null;
        if(withProbabilityAnswer)
            answer = new JobMatchAnswer<JobMatchAnswer.JobWithProbability>();
        else
            answer = new JobMatchAnswer<String>();
        for(Matcher matcher: matcherList.getMatchers()) {
            matchedJobs = matcher.match(resume);
            if(matchedJobs == null) {
                logger.error("Unable to find to jobs because the resume with id '{}' is in a invalid state", resumeId);
                GenericAnswer errorAnswer = new GenericAnswer();
                errorAnswer.setResultCode(ResultCodes.FATAL_ERROR);
                errorAnswer.setMessage("Unable to find to jobs because the resume is in a invalid state");
                return new ResponseEntity<>(answer, HttpStatus.OK);
            }
            for(Job job: matchedJobs) {
                answer.addJob(matcher.getCruncherName(), job, withProbabilityAnswer);
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);
        logger.debug("Done processing: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }


    @RequestMapping(value = "/reindex", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> reindex() {
        logger.debug("reindex()");
        GenericAnswer answer = new GenericAnswer();
        int total = 0;
        for(Job job: jobRepository.findAll()) {
            jobCruncher.addWork(job);
            total++;
        }
        answer.setMessage("Going to reindex " + total + " jobs");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
