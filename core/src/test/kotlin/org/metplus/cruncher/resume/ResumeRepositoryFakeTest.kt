package org.metplus.cruncher.resume

class ResumeRepositoryFakeTest: ResumeRepositoryTest() {
    private val resumeRepository = ResumeRepositoryFake()
    override fun getRepository(): ResumeRepository {
        return resumeRepository
    }
}