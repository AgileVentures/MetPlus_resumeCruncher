package org.metplus.curriculum.web.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller that will handle
 * all the requests related with jobs
 */
@RestController
@RequestMapping(BaseController.baseUrl + "job")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class JobsController {
}
