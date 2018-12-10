package org.metplus.cruncher.rating

import org.metplus.cruncher.job.Job

class CrunchJobProcessSpy : ProcessCruncher<Job>() {
    override fun process(work: Job) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun nextWorkInQueue(): Job? {
        return nextWork()
    }
}