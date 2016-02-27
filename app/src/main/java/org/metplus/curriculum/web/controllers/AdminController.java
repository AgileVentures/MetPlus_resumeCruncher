package org.metplus.curriculum.web.controllers;

import org.metplus.curriculum.database.domain.Settings;
import org.metplus.curriculum.database.exceptions.MandatorySettingNotPresent;
import org.metplus.curriculum.database.repository.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(BaseController.baseUrl + "/admin")
public class AdminController {
    private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private SettingsRepository repository;

    /**
     * Retrieve all the settings of the cruncher
     * @summary Retrieve Cruncher settings
     * @return A list of settings
     */
    @RequestMapping(value = "/settings", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<Settings> mainPage() {
        LOG.debug("mainPage()");
        if(0 == repository.count()){
            repository.save(new Settings());
        }
        LOG.info("Output is: " + repository.findAll().iterator().next());
        return new ResponseEntity<>(repository.findAll().iterator().next(), HttpStatus.OK);
    }

    /**
     * Save all the settings
     * @summary Save settings
     * @return The answer stating if the settings were saved or not
     */
    @RequestMapping(value = "/settings", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Settings> save(@RequestBody Settings settings) throws MandatorySettingNotPresent {
        LOG.info("Post is: " + settings);
        settings.validate();
        repository.save(settings);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ExceptionHandler(MandatorySettingNotPresent.class)
    void handleIllegalArgumentException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }
}
