package org.metplus.cruncher.rating

import org.metplus.cruncher.resume.Resume

class CrunchResumeProcessSpy: ProcessCruncher<Resume>() {
    override fun process(work: Resume) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun nextWorkInQueue(): Resume? {
        return nextWork()
    }
}