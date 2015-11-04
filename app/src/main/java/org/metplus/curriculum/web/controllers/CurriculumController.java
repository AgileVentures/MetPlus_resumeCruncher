package org.metplus.curriculum.web.controllers;


import org.metplus.curriculum.database.domain.Resume;
import org.metplus.curriculum.database.repository.ResumeRepository;
import org.metplus.curriculum.web.GenericAnswer;
import org.metplus.curriculum.web.ResultCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/curriculum")
public class CurriculumController {
    @Autowired
    ResumeRepository resumeRepository;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<GenericAnswer> uploadCurriculum(
                @RequestParam("userId") String id,
                @RequestParam("name") String name,
                @RequestParam("file") MultipartFile file) {
        GenericAnswer answer = new GenericAnswer();

        Resume resume = resumeRepository.findOne(id);
        if(resume == null) {
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
                answer.setMessage("Error uploading the file: " + e.getMessage());
                answer.setResultCode(ResultCodes.FATAL_ERROR);
            }
        } else {
            answer.setResultCode(ResultCodes.FATAL_ERROR);
            answer.setMessage("File is empty");
        }

        return new ResponseEntity<>(answer, HttpStatus.OK);
    }
}
