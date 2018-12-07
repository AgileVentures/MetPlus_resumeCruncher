package org.metplus.cruncher.rating

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.slf4j.LoggerFactory


class CrunchJobProcess(
        private val allCrunchers: CruncherList,
        private val jobRepository: JobsRepository) : ProcessCruncher<Job>() {
    private val logger = LoggerFactory.getLogger(CrunchJobProcess::class.java)
    override fun process(work: Job) {
        logger.trace("process({})", work)
        var titleMetaData: CruncherMetaData = work.titleMetaData
        var descriptionMetaData: CruncherMetaData = work.descriptionMetaData
        val cruncher = allCrunchers.getCrunchers().first()
        try {
            titleMetaData = cruncher.crunch(work.title)
        } catch (exp: Exception) {
            logger.warn("Error crunching the title of job: ${work.id}: $exp")
            exp.printStackTrace()
        }

        try {
            descriptionMetaData = cruncher.crunch(work.description)
        } catch (exp: Exception) {
            logger.warn("Error crunching the description of job: ${work.id}: $exp")
            exp.printStackTrace()
        }
        jobRepository.save(work.copy(titleMetaData = titleMetaData, descriptionMetaData = descriptionMetaData))
        logger.debug("Job [${work}] processed successfully")
    }
}