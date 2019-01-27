package org.metplus.cruncher.rating

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.job.JobsRepository
import org.slf4j.LoggerFactory


open class CrunchJobProcess(
        private val allCrunchers: CruncherList,
        private val jobRepository: JobsRepository) : ProcessCruncher<Job>() {
    private val logger = LoggerFactory.getLogger(CrunchJobProcess::class.java)
    override fun process(work: Job) {
        logger.trace("process({})", work)
        val titleMetaData: MutableMap<String, CruncherMetaData> = work.titleMetaData.toMutableMap()
        val descriptionMetaData: MutableMap<String, CruncherMetaData> = work.descriptionMetaData.toMutableMap()
        allCrunchers.getCrunchers().forEach {
            try {
                titleMetaData[it.getCruncherName()] = it.crunch(work.title)
            } catch (exp: Exception) {
                logger.warn("Error crunching the title of job: ${work.id}: $exp")
                exp.printStackTrace()
            }

            try {
                descriptionMetaData[it.getCruncherName()] = it.crunch(work.description)
            } catch (exp: Exception) {
                logger.warn("Error crunching the description of job: ${work.id}: $exp")
                exp.printStackTrace()
            }
        }
        jobRepository.save(work.copy(titleMetaData = titleMetaData, descriptionMetaData = descriptionMetaData))
        logger.debug("Job [$work] processed successfully")
    }
}