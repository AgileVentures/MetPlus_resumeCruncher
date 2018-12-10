package org.metplus.cruncher.web.rating

import org.metplus.cruncher.job.JobsRepository
import org.metplus.cruncher.rating.CrunchJobProcess
import org.metplus.cruncher.rating.CruncherList

class AsyncJobProcess(allCrunchers: CruncherList, jobsRepository: JobsRepository)
    : CrunchJobProcess(allCrunchers, jobsRepository) {
    init {
        start()
    }
}