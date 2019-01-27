package org.metplus.cruncher.resume

class ResumeFileRepositoryFakeTest : ResumeFileRepositoryTest() {
    private val repo = ResumeFileRepositoryFake()
    override fun getRepository(): ResumeFileRepository {
        return repo
    }
}