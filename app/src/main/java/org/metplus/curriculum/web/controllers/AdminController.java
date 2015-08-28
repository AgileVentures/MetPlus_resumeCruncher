package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.MandatorySettingNotPresent;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private SettingsRepository repository;

    @RequestMapping("/settings")
    public Settings mainPage() {
        if(0 == repository.count()){
            repository.save(new Settings());
        }
        return repository.findAll().iterator().next();
    }

    @RequestMapping(name = "/settings", method= RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Settings> save(@RequestParam Settings settings) throws MandatorySettingNotPresent {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
