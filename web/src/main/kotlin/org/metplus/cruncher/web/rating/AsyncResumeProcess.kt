package org.metplus.cruncher.web.rating

import org.metplus.cruncher.rating.CrunchResumeProcess
import org.metplus.cruncher.rating.CruncherList
import org.metplus.cruncher.resume.ResumeFileRepository
import org.metplus.cruncher.resume.ResumeRepository

class AsyncResumeProcess(
        allCrunchers: CruncherList,
        resumeRepository: ResumeRepository,
        resumeFileRepository: ResumeFileRepository
) : CrunchResumeProcess(allCrunchers, resumeRepository, resumeFileRepository) {
    init {
        start()
    }
}