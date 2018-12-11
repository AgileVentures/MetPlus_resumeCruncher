package org.metplus.curriculum.cruncher.naivebayes

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.rating.Matcher
import org.metplus.cruncher.resume.Resume

class MatcherImpl: Matcher<Resume, Job> {
    override fun match(from: Resume, allList: List<Job>): List<Job> {
        return listOf()
    }
}