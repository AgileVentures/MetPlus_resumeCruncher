package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private SettingsRepository repository;

    @RequestMapping("/settings")
    public Settings greeting() {
        if(0 == repository.count()){
            repository.save(new Settings());
        }
        return repository.findAll().iterator().next();
    }
}
