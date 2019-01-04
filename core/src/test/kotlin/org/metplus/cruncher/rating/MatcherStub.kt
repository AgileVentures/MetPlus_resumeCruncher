package org.metplus.cruncher.rating

import org.metplus.cruncher.job.Job
import org.metplus.cruncher.resume.Resume

class MatcherStub(
        var matchReturnValue: List<Job> = emptyList(),
        var matchInverseReturnValue: List<Resume> = emptyList(),
        var similarityRatingReturnValue: Double = 99999999.0
) : Matcher<Resume, Job> {
    override fun match(from: Resume, allList: List<Job>): List<Job> {
        return matchReturnValue
    }

    override fun matchInverse(from: Job, allList: List<Resume>): List<Resume> {
        return matchInverseReturnValue
    }

    override fun similarityRating(left: Resume, right: Job): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}