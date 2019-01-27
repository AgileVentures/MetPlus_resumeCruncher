package org.metplus.curriculum.cruncher.naivebayes

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.rating.CruncherMetaData
import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.resume.Resume
import kotlin.math.min

class MatcherImpl : Matcher<Resume, Job> {
    override fun getName(): String {
        return "naiveBayes"
    }

    private val matchPoints = arrayOf(
            arrayOf(1600.0, 1200.0, 1000.0, 900.0, 850.0),
            arrayOf(750.0, 800.0, 600.0, 500.0, 450.0),
            arrayOf(700.0, 350.0, 400.0, 300.0, 250.0),
            arrayOf(600.0, 300.0, 150.0, 200.0, 150.0),
            arrayOf(400.0, 200.0, 100.0, 50.0, 100.0))
    private val maxPoints = 1600.0 + 800.0 + 400.0 + 200.0 + 100.0
    private val MAX_NUMBER_CATEGORIES = 5

    override fun match(from: Resume, allList: List<Job>): List<Job> {
        val processedJobs = mutableListOf<Job>()
        val resumeCategories = getCategoryListFromMetaData(from.cruncherData.getOrDefault(getName(), CruncherMetaData(mutableMapOf())), MAX_NUMBER_CATEGORIES)
        allList.forEach {
            val jobCategories = getJobCategories(it)
            val starRating = calculateStarRating(resumeCategories, jobCategories)
            if (starRating > 0) {
                processedJobs.add(it.copy(starRating = starRating))
            }
        }
        return processedJobs
    }

    override fun matchInverse(from: Job, allList: List<Resume>): List<Resume> {
        val processedJobs = mutableListOf<Resume>()
        val jobCategories = getJobCategories(from)

        allList.forEach {
            val resumeCategories = getCategoryListFromMetaData(it.cruncherData.getOrDefault(getName(), CruncherMetaData(mutableMapOf())), MAX_NUMBER_CATEGORIES)

            val starRating = calculateStarRating(jobCategories, resumeCategories)
            if (starRating > 0) {
                processedJobs.add(it.copy(starRating = starRating))
            }
        }

        return processedJobs
    }

    override fun similarityRating(left: Resume, right: Job): Double {
        val jobCategories = getJobCategories(right)
        val resumeCategories = getCategoryListFromMetaData(left.cruncherData.getOrDefault(getName(), CruncherMetaData(mutableMapOf())), MAX_NUMBER_CATEGORIES)

        return calculateStarRating(resumeCategories, jobCategories)
    }

    private fun getJobCategories(job: Job): List<String> {
        val jobCategories = getCategoryListFromMetaData(job.titleMetaData.getOrDefault(getName(), CruncherMetaData(mutableMapOf())), 2)
        jobCategories.addAll(getCategoryListFromMetaData(job.descriptionMetaData.getOrDefault(getName(), CruncherMetaData(mutableMapOf())), MAX_NUMBER_CATEGORIES - jobCategories.size))
        return jobCategories
    }

    private fun getCategoryListFromMetaData(resume: CruncherMetaData, limit: Int): MutableList<String> {
        val distinctMetaDataOrderedList = resume.metaData.toList()
                .sortedBy { (_, value) -> -value }
                .toMap().map {
                    it.key.replace("_job|_resume".toRegex(), "")
                }
                .distinct()
        return distinctMetaDataOrderedList.subList(0, min(limit, distinctMetaDataOrderedList.size)).toMutableList()

    }

    internal fun calculateStarRating(base: List<String>, compare: List<String>): Double {
        var probability = 0.0
        var i = 0
        for (strToFind in compare) {
            if (base.contains(strToFind)) {
                probability += this.matchPoints[base.indexOf(strToFind)][i]
            }
            i++
        }
        return probability / maxPoints * 5
    }
}