package org.metplus.curriculum.web.controllers;


import org.metplus.curriculum.api.APIVersion;
import org.metplus.curriculum.cruncher.MatcherList;
import org.metplus.curriculum.cruncher.Matcher;
import org.metplus.curriculum.database.config.SpringMongoConfig;
import org.metplus.curriculum.database.domain.Job;
import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.exceptions.ResumeNotFound;
import org.metplus.curriculum.database.exceptions.ResumeReadException;
import org.metplus.curriculum.database.repository.JobRepository;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.process.ResumeCruncher;
import org.metplus.curriculum.web.answers.GenericAnswer;
import org.metplus.curriculum.web.answers.ResultCodes;
import org.metplus.curriculum.web.answers.ResumeMatchAnswer;
import org.metplus.curriculum.web.answers.StarAnswer;
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
@RequestMapping({"curriculum", "resume"})
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
@APIVersion({1, 2, BaseController.VERSION_TESTING})
public class ResumeController {
    private static Logger logger = LoggerFactory.getLogger(ResumeController.class);

    @Autowired
    private ResumeCruncher resumeCruncher;

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SpringMongoConfig dbConfig;

    @Autowired
    private MatcherList matcherList;

    public ResumeController(){}
    public ResumeController(JobRepository jobRepository, ResumeRepository resumeRepository, MatcherList matcherList, ResumeCruncher resumeCruncher) {
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
        this.matcherList = matcherList;
        this.resumeCruncher = resumeCruncher;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> uploadCurriculum(
                @RequestParam("userId") String id,
                @RequestParam("name") String name,
                @RequestParam("file") MultipartFile file) {
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
                resumeCruncher.addWork(resume);
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
    @APIVersion(1)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv1(@RequestParam("title") final String title,
                                                  @RequestParam("description") final String description) {
        return match(title, description, false);
    }

    @RequestMapping(value = "/match", method = RequestMethod.POST)
    @APIVersion(2)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv2(@RequestParam("title") final String title,
                                                  @RequestParam("description") final String description) {
        return match(title, description, true);
    }

    private ResponseEntity<GenericAnswer> match(final String title,
                                               final String description,
                                               boolean withProbability
                                               ) {
        logger.debug("Match resumes with title: '" + title + "', description: '" + description + "'");
        if(title == null || title.length() == 0) {
            logger.error("Matching resumes with empty Title is not allowed");
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("Title cannot be empty");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        }
        List<Resume> matchedResumes = null;
        ResumeMatchAnswer answer = null;
        if(withProbability)
            answer =new ResumeMatchAnswer<ResumeMatchAnswer.ResumeWithProbability>();
        else
            answer = new ResumeMatchAnswer<String>();
        for(Matcher matcher: matcherList.getMatchers()) {
            matchedResumes = matcher.match(title, description);
            if(matchedResumes == null) {
                logger.error("Matching resumes with job title and description");
                GenericAnswer errorAnswer = new GenericAnswer();
                errorAnswer.setMessage("Not all information is crunched");
                errorAnswer.setResultCode(ResultCodes.FATAL_ERROR);
                return new ResponseEntity<>(errorAnswer, HttpStatus.BAD_REQUEST);
            }
            for(Resume resume: matchedResumes) {
                answer.addResume(matcher.getCruncherName(), resume, withProbability);
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
    @RequestMapping(value = "/match/{jobId}", method = RequestMethod.GET)
    @APIVersion(1)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv1(@PathVariable("jobId") final String jobId) {
        return match(jobId, false);
    }

    @RequestMapping(value = "/match/{jobId}", method = RequestMethod.GET)
    @APIVersion(2)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchv2(@PathVariable("jobId") final String jobId) {
        return match(jobId, true);
    }

    @RequestMapping(value = "/{resumeId}/compare/{jobId}", method = RequestMethod.GET)
    @APIVersion(2)
    @ResponseBody
    public ResponseEntity<GenericAnswer> compareResumeAgainstJob(@PathVariable("resumeId") final String resumeId, @PathVariable("jobId") final String jobId) {
        logger.debug("Match resumes with job id: '" + jobId + "'");
        if(jobId == null || jobId.length() == 0) {
            logger.error("Comparing Resume with empty job identifier");
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("Empty job identifier");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        }
        if(resumeId == null || resumeId.length() == 0) {
            logger.error("Comparing Resume with empty resume id");
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("Empty resume identifier");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        }
        List<Resume> matchedResumes = null;
        Job job = jobRepository.findByJobId(jobId);
        if(job == null) {
            logger.error("Unable to find job with id: " + jobId);
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("No job found with id: " + jobId);
            answer.setResultCode(ResultCodes.JOB_NOT_FOUND);
            return new ResponseEntity<>(answer, HttpStatus.NOT_FOUND);
        }
        Resume resume = resumeRepository.findByUserId(resumeId);
        if(resume == null) {
            logger.error("Unable to find resume with id: " + resumeId);
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("No resume found with id: " + resumeId);
            answer.setResultCode(ResultCodes.RESUME_NOT_FOUND);
            return new ResponseEntity<>(answer, HttpStatus.NOT_FOUND);
        }

        StarAnswer answer = new StarAnswer();
        for(Matcher matcher: matcherList.getMatchers()) {
            answer.addStarRating(matcher.getCruncherName(), matcher.matchSimilarity(resume, job));
        }

        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/{resumeId}/compare/{jobId}", method = RequestMethod.GET)
    @APIVersion(BaseController.VERSION_TESTING)
    @ResponseBody
    public ResponseEntity<GenericAnswer> compareResumeAgainstJobCanned(
            @PathVariable("resumeId") final String resumeId,
            @PathVariable("jobId") final String jobId) {
        logger.debug("Match resumes with job id: '" + jobId + "'");
        int jobIdentifier = Integer.valueOf(jobId);
        int resumeIdentifier = Integer.valueOf(resumeId);
        double stars[][] = {{1.1, 1.2, 3., 4.3, 5.},
                             {1.2, 1.3, 3.1, 4.4, 4.9}};

        int starsId = -1;
        StarAnswer answer = new StarAnswer();
        if(resumeIdentifier == 1)
            starsId = jobIdentifier;
        if(starsId < 0 || starsId >= stars[0].length)
            return compareResumeAgainstJob(resumeId, jobId);

        int cruncherId = 0;
        for(Matcher matcher: matcherList.getMatchers()) {
            answer.addStarRating(matcher.getCruncherName(), stars[cruncherId][starsId]);
            cruncherId++;
        }

        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/match/{jobId}", method = RequestMethod.GET)
    @APIVersion(BaseController.VERSION_TESTING)
    @ResponseBody
    public ResponseEntity<GenericAnswer> matchCannedReponse(@PathVariable("jobId") final String jobId) {
        final double[] resumeStars = {1.8, 4.1, 2.6, 4.9, 3.2,
                                      1.8, 4.1, 2.6, 4.9, 3.2};
        ResumeMatchAnswer answer = new ResumeMatchAnswer();
        double jobIdentifier = Double.valueOf(jobId);
        if(jobIdentifier > 0 && jobIdentifier < 11) {
            for(int i = 0 ; i  < resumeStars.length ; i++ ) {
                Resume resume = resumeRepository.findByUserId(Integer.toString(i));
                if(resume != null) {
                    resume.setStarRating(resumeStars[i]);
                    answer.addResume("NaiveBayes", resume, true);
                }
            }
        } else if(jobIdentifier % 5 != 0) {
            for(int i = 0 ; i < 4 ; i++) {
                Resume resume = resumeRepository.findByUserId(Double.toString(jobIdentifier + i));
                if(resume != null) {
                    resume.setStarRating(resumeStars[i]);
                    answer.addResume("NaiveBayes", resume, true);
                }
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    private ResponseEntity<GenericAnswer> match(final String jobId, boolean withProbability) {
        logger.debug("Match resumes with job id: '" + jobId + "'");
        if(jobId == null || jobId.length() == 0) {
            logger.error("Matching resumes with empty job identifier");
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("Empty job identifier");
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        }
        List<Resume> matchedResumes = null;
        Job job = jobRepository.findByJobId(jobId);
        if(job == null) {
            logger.error("Unable to find job with id: " + jobId);
            GenericAnswer answer = new GenericAnswer();
            answer.setMessage("No job with id: " + jobId);
            answer.setResultCode(ResultCodes.JOB_NOT_FOUND);
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        }
        ResumeMatchAnswer answer = new ResumeMatchAnswer();
        for(Matcher matcher: matcherList.getMatchers()) {
            logger.debug("Checking for matcher: " + matcher.getCruncherName());
            matchedResumes = matcher.match(job);
            if(matchedResumes == null) {
                logger.error("Matching resumes with empty job identifier");
                GenericAnswer errorAnswer = new GenericAnswer();
                errorAnswer.setMessage("Not all information is crunched");
                errorAnswer.setResultCode(ResultCodes.FATAL_ERROR);
                return new ResponseEntity<>(errorAnswer, HttpStatus.BAD_REQUEST);
            }
            for(Resume resume: matchedResumes) {
                answer.addResume(matcher.getCruncherName(), resume, withProbability);
            }
        }
        answer.setMessage("Success");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }

    @RequestMapping(value = "/reindex", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<GenericAnswer> reindex() {
        logger.debug("reindex()");
        GenericAnswer answer = new GenericAnswer();
        int total = 0;
        for(Resume resume: resumeRepository.findAll()) {
            resumeCruncher.addWork(resume);
            total++;
        }
        answer.setMessage("Going to reindex " + total + " resumes");
        answer.setResultCode(ResultCodes.SUCCESS);

        logger.debug("Result is: " + answer);
        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
