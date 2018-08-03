package org.metplus.cruncher.web.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/v1/settings")
class Settings {
    @GetMapping("/")
    fun getSettings() {

    }
}